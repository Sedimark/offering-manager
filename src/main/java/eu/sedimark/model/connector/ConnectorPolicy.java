package eu.sedimark.model.connector;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConnectorPolicy {
    @JsonProperty("@context")
    private Context context;

    @JsonProperty("@id")
    private String id;

    private Policy policy;

    @Data
    public static class Context {
        @JsonProperty("@vocab")
        private String vocab;
        private String sedi;
        private String odrl;
    }

    @Data
    public static class Policy {
        @JsonProperty("@type")
        private String type;

        private Object[] permission;
        private Object[] prohibition;
        private Object[] obligation;
    }
}
