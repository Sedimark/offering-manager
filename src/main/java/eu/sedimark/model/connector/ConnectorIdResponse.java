package eu.sedimark.model.connector;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConnectorIdResponse {

    @JsonProperty("@type")
    private String type;

    @JsonProperty("@id")
    private String id;

    private long createdAt;

    @JsonProperty("@context")
    private Context context;

    @Data
    public static class Context {
        @JsonProperty("@vocab")
        private String vocab;
        private String edc;
        private String odrl;
    }
}
