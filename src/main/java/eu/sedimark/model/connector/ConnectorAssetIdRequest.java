package eu.sedimark.model.connector;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public class ConnectorAssetIdRequest {
    @JsonProperty("@context")
    private Context context;

    @JsonProperty("@type")
    private String type;

    @JsonProperty("@id")
    private String id;

    private String counterPartyAddress;
    private String protocol;

    @Data
    public static class Context {
        @JsonProperty("@vocab")
        private String vocab;
    }
}
