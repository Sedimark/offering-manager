package eu.sedimark.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sedimark.config.DefaultPaths;
import eu.sedimark.exception.*;
import eu.sedimark.model.ShaclValidationResult;
import eu.sedimark.model.dltbooth.CredentialSubject;
import eu.sedimark.model.dltbooth.VC;
import eu.sedimark.model.offering.*;
import eu.sedimark.config.Config;
import eu.sedimark.config.OntologyDefinitions;
import eu.sedimark.model.dltbooth.VerifiableCredentialWrapper;
import eu.sedimark.model.persistence.InMemoryOfferingStorage;
import eu.sedimark.model.persistence.OfferingStorage;
import eu.sedimark.model.persistence.PostgresOfferingStorage;
import eu.sedimark.service.helper.JsonLdHelper;
import eu.sedimark.service.helper.ShaclValidatorHelper;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotFoundException;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static eu.sedimark.service.helper.JsonLdHelper.buildSedimarkContext;

@Singleton
public class OfferingService {

    private static final OfferingService INSTANCE = new OfferingService();

    private final Logger LOGGER;

    private final OfferingStorage offeringStorage;

    // TODO: obtain this information from the VC/DID
    @Getter
    @Setter
    private String participantProfile;

    public OfferingService() {
        this.LOGGER = Logger.getLogger(this.getClass().getName());
        if(Config.isEnablePostgresPersistence()) {
            offeringStorage = new PostgresOfferingStorage();
        } else {
            offeringStorage = new InMemoryOfferingStorage();
        }
    }

    public static OfferingService getInstance() {
        return INSTANCE;
    }

    public ObjectMapper getOfferingMapper() {
        ObjectMapper offeringMapper = new ObjectMapper();
        offeringMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        offeringMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return offeringMapper;
    }

    public String getOfferingString (Offering offering) {
        try {
            return getOfferingMapper().writeValueAsString(offering);
        } catch (JsonProcessingException e) {
            LOGGER.severe("Error while serializing offering: " + offering);
            throw new TechnicalException("Could not serialize Offering: " + e.getMessage(), e);
        }
    }

    public Offering registerOffering(String offeringDescription) {

        LOGGER.info("Offering received: " + offeringDescription);
        try {
            ShaclValidationResult shaclValidationResult = ShaclValidatorHelper.validate(offeringDescription, OntologyDefinitions.SHACL_URL);
            if (!shaclValidationResult.conforms()) {
                LOGGER.info("SHACL validation error: " + shaclValidationResult.getReport());
                throw new BusinessException("SHACL validation error: " + shaclValidationResult.getReport());
            }
        } catch (ShaclValidatorException e) {
            throw new TechnicalException("Error found in the SHACL shape: " + e.getMessage(), e);
        }

        // Compact the received JSON-LD and use specific context prefixes
        Object offeringJsonObject = JsonLdHelper.compact(offeringDescription);
        LOGGER.info("Compacted JSON LD Offering description: " + JsonLdHelper.toJsonString(offeringJsonObject));
        Offering offering = getOfferingMapper().convertValue(offeringJsonObject, Offering.class);

        updateOfferingIds(offering);

        updateUidOfferingContract(offering);

        updateTargetOfferingContractPolicies(offering);

        ConnectorService connectorService = null;
        if (Config.isEnableConnectorService()) {
            connectorService = new ConnectorService(Config.getInternalConnectorEndpoint());
            connectorService.registerAssetsInConnector(offering);
        }

        // We call it after updating the connector so we can forget about backend URL
        updateAssetsAccessUrlWithConnectorPublicAddress(offering, Config.getExternalConnectorEndpoint());

        setParticipantProfile(offering.getIsListedBy().getBelongsTo().getId());

        // TODO: check that existing DIDs are the ones corresponding to the participant

        String offeringHash = setOfferingHash(offering);

        // TODO: test whether the asset policy and contract are being properly removed if there is a DLTBoothException
        String nftAddress = "";
        if (Config.isEnableDltBoothService()) {
            try {
                DLTBoothService dltBoothService = new DLTBoothService(Config.getInternalDltBoothEndpoint());
                nftAddress = dltBoothService.createNewNft(
                        offering.getId().substring(offering.getId().lastIndexOf('/') + 1),
                        offering.getId(),
                        offeringHash);
            } catch (DltBoothException e) {
                if (connectorService != null) {
                    // TODO: fix this removal as it is not working properly
                    connectorService.removeAssetPolicyContractInConnector(offering);
                }
                throw new TechnicalException("Failed while creating a new DLT NFT through the DLT-Booth: " + e.getMessage(), e);
            }
        }

        try {
            offeringStorage.save(offering, nftAddress, offeringHash);
        } catch (StorageException e) {
            throw new TechnicalException("Database error: " + e.getMessage(), e);
        }

        LOGGER.info("Offering parsed: " + getOfferingString(offering));

        return offering;
    }

    private void updateTargetOfferingContractPolicies(Offering offering) {
        for (Permission permission : offering.getHasOfferingContract().getPermissions()) {
            permission.setTarget(offering.getId());
        }
    }

    private void updateUidOfferingContract(Offering offering) {
        offering.getHasOfferingContract().setUid(offering.getHasOfferingContract().getId());
    }

    private void updateOfferingIds(Offering offering) {
        String offeringId = UUID.randomUUID().toString();

        offering.setId(getBasePath() + "/" + offeringId);

        for (Asset asset : offering.getHasAsset()) {
            if (OntologyDefinitions.ASSET_TYPES.contains(asset.getType())) {
                asset.setId(offering.getId() + "/assets/" + UUID.randomUUID());
                asset.getOfferedBy().setId(offeringId);
                if (OntologyDefinitions.ASSET_PROVISION_TYPES.contains(asset.getIsProvidedBy().getType())) {
                    asset.getIsProvidedBy().setId(offering.getId() + "/assetProvision/" + UUID.randomUUID());
                }
            }
        }
        if (OntologyDefinitions.OFFERING_CONTRACT_TYPES.contains(offering.getHasOfferingContract().getType())) {
            offering.getHasOfferingContract().setId(offering.getId() + "/offeringContract/" + UUID.randomUUID());
        }
    }

    private void updateAssetsAccessUrlWithConnectorPublicAddress(Offering offering, String publicConnectorEndpointDsp) {
        // TODO: check if we need to extract backendURL
        for (Asset asset : offering.getHasAsset()) {
            if (OntologyDefinitions.ASSET_TYPES.contains(asset.getType())) {
                if (OntologyDefinitions.ASSET_PROVISION_TYPES.contains(asset.getIsProvidedBy().getType())) {
                    asset.getIsProvidedBy().getAccessURL().setId(publicConnectorEndpointDsp);
                }
            }
        }
    }

    public SelfListing createSelfListingNoOfferings(String absolutePath) {
        VerifiableCredentialWrapper verifiableCredentialWrapper = null;
        String didDocument = null;
        if (Config.isEnableDltBoothService()) {
            try {
                DLTBoothService dltBoothService = new DLTBoothService(Config.getInternalDltBoothEndpoint());
                verifiableCredentialWrapper = dltBoothService.getIdentity();
                didDocument = verifiableCredentialWrapper.getSub();
            } catch (DltBoothException e) {
                throw new TechnicalException("Failed while fetching offerings: " + e.getMessage(), e);
            }
        } else {
            // Dummy did document in case it does not exist
            didDocument = "did:iota:lnk:0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
            verifiableCredentialWrapper = new VerifiableCredentialWrapper();
            verifiableCredentialWrapper.setVc(new VC());
            verifiableCredentialWrapper.getVc().setCredentialSubject(new CredentialSubject());
            verifiableCredentialWrapper.getVc().getCredentialSubject().setAlternateName("NoDLT-BOOTHDummyName");
        }

        SelfListing selfListing = new SelfListing();

        selfListing.setId(absolutePath);
        selfListing.setType(OntologyDefinitions.SEDI_PREFIX + ":Self-Listing");
        selfListing.setBelongsTo(new Participant());
        // TODO: improve the method to obtain the participant self-description endpoint (now, if there is no offerings is empty)
        if (getParticipantProfile() != null) {
            selfListing.getBelongsTo().setId(getParticipantProfile());
        } else {
            selfListing.getBelongsTo().setId("http://self-description-profile-endpoint");
        }
        selfListing.getBelongsTo().setType(OntologyDefinitions.SEDI_PREFIX + ":Participant");
        selfListing.getBelongsTo().setAlternateName(new ValueWithType());
        selfListing.getBelongsTo().getAlternateName().setType(OntologyDefinitions.XSD_PREFIX + ":string");
        selfListing.getBelongsTo().getAlternateName().setValue(verifiableCredentialWrapper.getVc().getCredentialSubject().getAlternateName());
        selfListing.getBelongsTo().setAccountId(new ValueWithType());
        selfListing.getBelongsTo().getAccountId().setType(OntologyDefinitions.XSD_PREFIX + ":string");
        selfListing.getBelongsTo().getAccountId().setValue(didDocument);

        // Adding SEDIMARK context
        selfListing.setAdditionalProperty("@context", buildSedimarkContext(JsonLdHelper.ContextPreset.SELFLISTING).get("@context"));
        return selfListing;
    }

    public PaginatedSelfListingResult<String> createSelfListing(Integer page, Integer size) {
        SelfListing selfListing = createSelfListingNoOfferings(getBasePath());
        selfListing.setHasOffering(new ArrayList<>());
        try {
            Map<String, Offering> offeringsMap;
            int totalCount = offeringStorage.countOfferings();
            if (page != null && size != null && size > 0) {
                offeringsMap = offeringStorage.findAll(page, size);
                // remove existing context and update the global one
                for (Offering offering : offeringsMap.values()) {
                    offering.setContext(null);
                    selfListing.getHasOffering().add(offering);
                }
                if (!selfListing.getHasOffering().isEmpty()) {
                    selfListing.setAdditionalProperty("@context", buildSedimarkContext().get("@context"));
                }
            } else {
                offeringsMap = offeringStorage.findAll();
                for (Offering offering : offeringsMap.values()) {
                    IdOnly idOnly = new IdOnly();
                    idOnly.setId(offering.getId());
                    selfListing.getHasOffering().add(idOnly);
                }
            }
            LOGGER.info(JsonLdHelper.toJsonString(selfListing));
            Object selfListingJsonObject = JsonLdHelper.compact(JsonLdHelper.toJsonString(selfListing));
            LOGGER.info("Compacted JSON LD Self-Listing: " + JsonLdHelper.toJsonString(selfListingJsonObject));
            return new PaginatedSelfListingResult<>(getOfferingMapper().writeValueAsString(selfListing), totalCount);
        } catch (StorageException e) {
            throw new TechnicalException("Database error: " + e.getMessage(), e);
        } catch (JsonProcessingException e) {
            throw new TechnicalException("Error while parsing JSON Self-Listing: " + e.getMessage(), e);
        }
    }

    public Offering getOfferingById(String offeringId) throws NotFoundException {
        LOGGER.info("Looking for offering: " + offeringId);
        Offering offering = null;
        try {
            offering = offeringStorage.findById(offeringId);
        } catch (StorageException e) {
            throw new TechnicalException("Database error: " + e.getMessage(), e);
        }

        if (offering == null) {
            throw new NotFoundException();
        }
        return offering;
    }

    public Offering getOfferingByShortId(String offeringId) throws NotFoundException {
        return getOfferingById(getBasePath() + "/" + offeringId);
    }

    // TODO: Shall we remove the Offering from the Connector? By the moment, we do not remove it.
    public void removeOfferingById(String offeringId) throws NotFoundException {
        LOGGER.info("Looking for offering: " + offeringId);
        try {
            this.offeringStorage.deleteById(offeringId);
        } catch (StorageException e) {
            LOGGER.warning("Offering with ID: " + offeringId + " could not be removed from local storage.");
            throw new TechnicalException("Database error: " + e.getMessage(), e);
        }
    }

    public void removeOfferingByShortId(String id) throws NotFoundException {
        removeOfferingById(getBasePath() + "/" + id);
    }

    public String getNftAddressFromOfferingId (String offeringId) {
        try {
            return this.offeringStorage.findNftAddress(offeringId);
        } catch (StorageException e) {
            throw new TechnicalException("Database error: " + e.getMessage(), e);
        }
    }

    public String getNftAddressFromOfferingShortId(String offeringId) {
        return getNftAddressFromOfferingId(getBasePath() + "/" + offeringId);
    }

    public String getOfferingHashFromOfferingId (String offeringId) {
        try {
            return this.offeringStorage.findOfferingHash(offeringId);
        } catch (StorageException e) {
            throw new TechnicalException("Database error: " + e.getMessage(), e);
        }
    }

    private String setOfferingHash(Offering offering) {
        String hashAlgorithm = Config.getHashAlgorithm();
        try {
            // Choose the SHA-3 algorithm (e.g., SHA3-256)
            MessageDigest digest = MessageDigest.getInstance(hashAlgorithm);

            // Compute the hash as a byte array
            byte[] hashBytes = digest.digest(getOfferingString(offering).getBytes(StandardCharsets.UTF_8));

            // Convert the byte array into a hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new TechnicalException(hashAlgorithm + " algorithm not available", e);
        }
    }

    private String getBasePath() {
        return Config.getExternalEndpoint() + DefaultPaths.OFFERING_MANAGER_PATH;
    }

}
