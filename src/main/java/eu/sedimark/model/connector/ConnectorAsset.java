package eu.sedimark.model.connector;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ConnectorAsset {

    @JsonProperty("@context")
    private Context context;

    @JsonProperty("@id")
    private String id;

    private Properties properties;
    private DataAddress dataAddress;

    @Data
    public static class Context {
        @JsonProperty("@vocab")
        private String vocab;
    }

    @Data
    public static class Properties {
        // We do not take into account properties that are not mandatory at the moment
        // Private String name;
        private String contenttype;

        private String assetType;
    }

    @Data
    public static class DataAddress {
        private String type;
        private String baseUrl;

        private Map<String, Object> additionalProperties = new HashMap<>();

        /*
        // We do not take into account properties that are not mandatory at the moment
        private String name;
        private String proxyPath;

        @JsonProperty("header:Accept")
        private String headerAccept;

        private String proxyQueryParams;
        private String proxyBody;
        private String proxyMethod;
        */
        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }
    }

}
