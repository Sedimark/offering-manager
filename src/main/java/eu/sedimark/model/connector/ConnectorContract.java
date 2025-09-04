package eu.sedimark.model.connector;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class ConnectorContract {
    @JsonProperty("@context")
    private Context context;

    @JsonProperty("@id")
    private String id;

    private String accessPolicyId;
    private String contractPolicyId;
    private List<AssetSelectorCriterion> assetsSelector;

    @Data
    public static class Context {
        @JsonProperty("@vocab")
        private String vocab;
    }

    @Data
    public static class AssetSelectorCriterion {
        @JsonProperty("@type")
        private String type;

        private String operandLeft;
        private String operandRight;
        private String operator;
    }
}
