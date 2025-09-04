package eu.sedimark.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sedimark.config.DefaultPaths;
import eu.sedimark.exception.DltBoothException;
import eu.sedimark.exception.TechnicalException;
import eu.sedimark.model.connector.ConnectorIdResponse;
import eu.sedimark.model.dltbooth.DidDocument;
import eu.sedimark.model.dltbooth.OfferingNft;
import eu.sedimark.model.dltbooth.OfferingNftResponse;
import eu.sedimark.model.dltbooth.VerifiableCredentialWrapper;
import eu.sedimark.service.helper.HttpClientHelper;
import lombok.Getter;

import java.io.IOException;
import java.util.logging.Logger;

public class DLTBoothService {
    @Getter
    private final String dltBoothBasePath;

    private final Logger LOGGER;

    private final HttpClientHelper httpClientHelper = new HttpClientHelper();

    DLTBoothService(String dltBoothBasePath) {
        this.dltBoothBasePath = dltBoothBasePath;
        this.LOGGER = Logger.getLogger(this.getClass().getName());
    }

    public ObjectMapper getDltBoothMapper() {
        ObjectMapper dltBoothMapper = new ObjectMapper();
        dltBoothMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        dltBoothMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return dltBoothMapper;
    }

    private OfferingNft createNftBean(String name, String offeringEndpoint, String offeringHash) {
        OfferingNft offeringNft = new OfferingNft();
        offeringNft.setName(name);
        offeringNft.setDescriptionUri(offeringEndpoint);
        offeringNft.setDescriptionHash(offeringHash);
        return offeringNft;
    }

    private OfferingNftResponse postNft(OfferingNft offeringNft) {
        ObjectMapper mapper = getDltBoothMapper();
        try {
            String jsonDltNftString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(offeringNft);
            LOGGER.info("DLT-Booth postNft address: "
                    + this.dltBoothBasePath + DefaultPaths.DLT_BOOTH_PATH_OFFERING + " with body: " + jsonDltNftString);

            String responseStr = httpClientHelper.sendPostRequest(this.dltBoothBasePath + DefaultPaths.DLT_BOOTH_PATH_OFFERING, jsonDltNftString);
            LOGGER.info("POST NFT response: " + responseStr);
            return mapper.readValue(responseStr, OfferingNftResponse.class);
        } catch (IOException e) {
            throw new DltBoothException("Failed while creating a new DLT entry through the DLT-Booth: " + e.getMessage(), e);
        }
    }

    public VerifiableCredentialWrapper getIdentity() {
        ObjectMapper mapper = getDltBoothMapper();
        try {
            LOGGER.info("Get Identity URL: " + this.dltBoothBasePath + DefaultPaths.DLT_BOOTH_PATH_IDENTITY);
            String responseStr = httpClientHelper.sendGetRequest( this.dltBoothBasePath + DefaultPaths.DLT_BOOTH_PATH_IDENTITY);
            LOGGER.info("Get Identity response: " + responseStr);
            return mapper.readValue(responseStr, VerifiableCredentialWrapper.class);
        } catch (IOException e) {
            throw new TechnicalException("Failed while getting the identity from the DLT-Booth: " + e.getMessage(), e);
        }
    }

    public DidDocument resolveDid(String did) {
        ObjectMapper mapper = getDltBoothMapper();
        try {
            LOGGER.info("Get DID URL: " + this.dltBoothBasePath + DefaultPaths.DLT_BOOTH_PATH_DIDS + "?did=" + did);
            String responseStr = httpClientHelper.sendGetRequest( this.dltBoothBasePath + DefaultPaths.DLT_BOOTH_PATH_DIDS + "?did=" + did);
            LOGGER.info("Resolve DID response: " + responseStr);
            return mapper.readValue(responseStr, DidDocument.class);
        } catch (IOException e) {
            throw new TechnicalException("Failed while resolving a DID from the DLT-Booth: " + e.getMessage(), e);
        }
    }

    public String createNewNft(String offeringId, String offeringEnpdoint, String offeringHash) {
        OfferingNft offeringNft = createNftBean(offeringId, offeringEnpdoint, offeringHash);
        OfferingNftResponse offeringNftResponse = postNft(offeringNft);
        if(offeringNftResponse == null) {
            LOGGER.severe("DLT-Booth NFT creation response null");
            throw new TechnicalException("Could not create the NFT");
        }
        LOGGER.info("DLT-Booth NFT address created: " + offeringNftResponse.getNftAddress());
        return offeringNftResponse.getNftAddress();
    }
}
