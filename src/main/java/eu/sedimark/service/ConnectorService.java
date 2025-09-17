package eu.sedimark.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sedimark.config.DefaultPaths;
import eu.sedimark.config.OntologyDefinitions;
import eu.sedimark.exception.TechnicalException;
import eu.sedimark.model.connector.ConnectorAsset;
import eu.sedimark.model.connector.ConnectorContract;
import eu.sedimark.model.connector.ConnectorIdResponse;
import eu.sedimark.model.connector.ConnectorPolicy;
import eu.sedimark.model.offering.*;
import eu.sedimark.service.helper.HttpClientHelper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static eu.sedimark.config.OntologyDefinitions.CONNECTOR_VOCAB;

public class ConnectorService {

    private final String connectorBasePath;

    private final static String EXCHANGE_DATA_TYPE = "HttpData";

    private final HttpClientHelper httpClientHelper = new HttpClientHelper();

    private final Logger LOGGER;

    public ConnectorService(String connectorBasePath) {
        this.connectorBasePath = connectorBasePath;
        this.LOGGER = Logger.getLogger(this.getClass().getName());
    }

    public ObjectMapper getConnectorMapper() {
        ObjectMapper connectorMapper = new ObjectMapper();
        connectorMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        connectorMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return connectorMapper;
    }

    // TODO: Add required/additional parameters such as proxyBody, proxyMethod, etc.
    private ConnectorAsset createConnectorAsset(Asset asset) {
        ConnectorAsset connectorAsset = new ConnectorAsset();
        ConnectorAsset.Context ctx = new ConnectorAsset.Context();
        ctx.setVocab(CONNECTOR_VOCAB);
        connectorAsset.setContext(ctx);

        connectorAsset.setId(asset.getId());

        ConnectorAsset.Properties props = new ConnectorAsset.Properties();
        props.setContenttype("application/json");
        props.setAssetType(asset.getType());
        connectorAsset.setProperties(props);

        ConnectorAsset.DataAddress addr = new ConnectorAsset.DataAddress();
        if (asset.getIsProvidedBy().getFormat().getId() == null) {
            LOGGER.warning("Using default exchangeDataType: " + EXCHANGE_DATA_TYPE);
            addr.setType(EXCHANGE_DATA_TYPE);
        } else {
            addr.setType(asset.getIsProvidedBy().getFormat().getId());
        }

        if (asset.getIsProvidedBy().getHeaders() != null) {
            for (Header header : asset.getIsProvidedBy().getHeaders()) {
                addr.setAdditionalProperty("header:" + header.getHeaderName().getValue(), header.getHeaderValue().getValue());
            }
        }

        // We update the accessURL afterwards to include the public one in the offeringService class
        addr.setBaseUrl(asset.getIsProvidedBy().getAccessURL().getId());
        connectorAsset.setDataAddress(addr);
        return connectorAsset;
    }

    // TODO: Add required/additional parameters such as proxyBody, proxyMethod, etc.
    private ConnectorPolicy createConnectorPolicy(OfferingContract offeringContract) {
        ConnectorPolicy connectorPolicy = new ConnectorPolicy();

        connectorPolicy.setId(offeringContract.getId());

        ConnectorPolicy.Context ctx = new ConnectorPolicy.Context();
        ctx.setVocab(CONNECTOR_VOCAB);
        ctx.setOdrl(OntologyDefinitions.ODRL_URI);
        ctx.setSedi(OntologyDefinitions.SEDI_URI);
        connectorPolicy.setContext(ctx);

        ConnectorPolicy.Policy policy = new ConnectorPolicy.Policy();
        policy.setType(OntologyDefinitions.ODRL_PREFIX + ":Set");

        policy.setPermission(offeringContract.getPermissions().toArray(new Permission[0]));
        policy.setObligation(offeringContract.getObligations().toArray(new Obligation[0]));
        policy.setProhibition(offeringContract.getProhibitions().toArray(new Prohibition[0]));

        connectorPolicy.setPolicy(policy);

        return connectorPolicy;
    }

    // TODO: Add required/additional parameters such as proxyBody, proxyMethod, etc.
    private ConnectorContract createConnectorContract(Asset asset, OfferingContract offeringContract) {
        ConnectorContract connectorContract = new ConnectorContract();

        connectorContract.setId(offeringContract.getId());

        ConnectorContract.Context ctx = new ConnectorContract.Context();
        ctx.setVocab(CONNECTOR_VOCAB);
        connectorContract.setContext(ctx);

        connectorContract.setAccessPolicyId(offeringContract.getId());
        connectorContract.setContractPolicyId(offeringContract.getId());

        ConnectorContract.AssetSelectorCriterion criterion = new ConnectorContract.AssetSelectorCriterion();
        criterion.setType("Criterion");
        criterion.setOperandLeft("id"); // should it be @id?
        criterion.setOperandRight(asset.getId());
        criterion.setOperator("=");

        connectorContract.setAssetsSelector(List.of(criterion));

        return connectorContract;
    }

    private ConnectorIdResponse postConnectorAsset(ConnectorAsset connectorAsset) {
        ObjectMapper mapper = getConnectorMapper();
        try {
            String jsonAsset = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(connectorAsset);
            LOGGER.info("postConnectorAsset body: " + jsonAsset);
            String responseStr = httpClientHelper.sendPostRequest(this.connectorBasePath + DefaultPaths.CONNECTOR_PATH_MANAGEMENT_ASSET, jsonAsset);
            LOGGER.info("PostConnectorAsset response: " + responseStr);
            return mapper.readValue(responseStr, ConnectorIdResponse.class);
        } catch (IOException e) {
            throw new TechnicalException("Failed while creating an asset in the connector: " + e.getMessage(), e);
        }
    }

    private ConnectorIdResponse deleteConnectorAsset(String connectorAssetId) {
        ObjectMapper mapper = getConnectorMapper();
        LOGGER.info("DeleteConnectorAsset removing asset ID: " + connectorAssetId);
        try {
            String responseStr = httpClientHelper.sendDeleteRequest(this.connectorBasePath + DefaultPaths.CONNECTOR_PATH_MANAGEMENT_ASSET + "/" + connectorAssetId);
            LOGGER.info("DeleteConnectorAsset response: " + responseStr);
            return mapper.readValue(responseStr, ConnectorIdResponse.class);
        } catch (IOException e) {
            throw new TechnicalException("Failed while deleting an asset in the connector: " + e.getMessage(), e);
        }
    }

    private ConnectorIdResponse postConnectorPolicy(ConnectorPolicy connectorPolicy) {
        ObjectMapper mapper = getConnectorMapper();
        try {
            String jsonPolicy = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(connectorPolicy);
            LOGGER.info("PostConnectorPolicy body: " + jsonPolicy);
            String responseStr = httpClientHelper.sendPostRequest(this.connectorBasePath + DefaultPaths.CONNECTOR_PATH_MANAGEMENT_POLICY, jsonPolicy);
            LOGGER.info("PostConnectorPolicy response: " + responseStr);
            return mapper.readValue(responseStr, ConnectorIdResponse.class);
        } catch (IOException e) {
            throw new TechnicalException("Failed while creating a policy in the connector: " + e.getMessage(), e);
        }
    }

    private ConnectorIdResponse deleteConnectorPolicy(String connectorPolicyId) {
        ObjectMapper mapper = getConnectorMapper();
        try {
            String responseStr = httpClientHelper.sendDeleteRequest(this.connectorBasePath + DefaultPaths.CONNECTOR_PATH_MANAGEMENT_POLICY + "/" + connectorPolicyId);
            LOGGER.info("DeleteConnectorPolicy response: " + responseStr);
            return mapper.readValue(responseStr, ConnectorIdResponse.class);
        } catch (IOException e) {
            throw new TechnicalException("Failed while deleting a Policy in the connector: " + e.getMessage(), e);
        }
    }

    private ConnectorIdResponse postConnectorContract(ConnectorContract connectorContract) {
        ObjectMapper mapper = getConnectorMapper();
        try {
            String jsonContract = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(connectorContract);
            LOGGER.info("PostConnectorContract body: " + jsonContract);
            String responseStr = httpClientHelper.sendPostRequest(this.connectorBasePath + DefaultPaths.CONNECTOR_PATH_MANAGEMENT_CONTRACT, jsonContract);
            LOGGER.info("PostConnectorContract response: " + responseStr);
            return mapper.readValue(responseStr, ConnectorIdResponse.class);
        } catch (IOException e) {
            throw new TechnicalException("Failed while creating a contract in the connector: " + e.getMessage(), e);
        }
    }

    private ConnectorIdResponse deleteConnectorContract(String connectorContractId) {
        ObjectMapper mapper = getConnectorMapper();
        try {
            String responseStr = httpClientHelper.sendDeleteRequest(this.connectorBasePath + DefaultPaths.CONNECTOR_PATH_MANAGEMENT_CONTRACT + "/" + connectorContractId);
            LOGGER.info("DeleteConnectorContract response: " + responseStr);
            return mapper.readValue(responseStr, ConnectorIdResponse.class);
        } catch (IOException e) {
            throw new TechnicalException("Failed while deleting a Contract in the connector: " + e.getMessage(), e);
        }
    }

    public void registerAssetsInConnector(Offering offering) {
        // Create Policy
        ConnectorPolicy connectorPolicy = createConnectorPolicy(offering.getHasOfferingContract());

        // Post Policy to Connector
        ConnectorIdResponse postConnectorPolicyIdResponse = postConnectorPolicy(connectorPolicy);
        LOGGER.info("Connector response to PolicyId creation: " + postConnectorPolicyIdResponse);

        if (postConnectorPolicyIdResponse == null) {
            throw new TechnicalException("Could not create connector policy");
        }
        for (Asset asset : offering.getHasAsset()) {
            if (OntologyDefinitions.ASSET_TYPES.contains(asset.getType())) {
                ConnectorAsset connectorAsset = createConnectorAsset(asset);
                LOGGER.info("Connector asset: " + connectorAsset);
                ConnectorIdResponse postConnectorAssetIdResponse = postConnectorAsset(connectorAsset);
                if (postConnectorAssetIdResponse == null) {
                    throw new TechnicalException("Could not create connector asset: " + connectorAsset);
                }
                LOGGER.info("Connector IdResponse to Asset creation: " + postConnectorAssetIdResponse);

                // Create Contract linked to each asset
                ConnectorContract connectorContract = createConnectorContract(asset, offering.getHasOfferingContract());
                // Post Contract to Connector
                ConnectorIdResponse postConnectorContractIdResponse = postConnectorContract(connectorContract);
                if (postConnectorContractIdResponse == null) {
                    throw new TechnicalException("Could not create connector contract with AssetId: " + asset.getId() +
                            " and policyId: " + offering.getHasOfferingContract().getId());
                }
                LOGGER.info("Connector IdResponse to contract creation: " + postConnectorContractIdResponse);
            }
        }
    }

    public void removeAssetPolicyContractInConnector(Offering offering) {
        ConnectorIdResponse connectorIdResponse = null;
        for (Asset asset : offering.getHasAsset()) {
            if (OntologyDefinitions.ASSET_TYPES.contains(asset.getType())) {
                connectorIdResponse = deleteConnectorAsset(asset.getId());
                LOGGER.info("Connector Asset " + asset.getId() + " removed");
            }
        }
        connectorIdResponse = deleteConnectorContract(offering.getHasOfferingContract().getId());
        LOGGER.info("Connector Contract " + offering.getHasOfferingContract().getId() + " removed");
        connectorIdResponse = deleteConnectorPolicy(offering.getHasOfferingContract().getId());
        LOGGER.info("Connector Policy " + offering.getHasOfferingContract().getId() + " removed");
    }
}
