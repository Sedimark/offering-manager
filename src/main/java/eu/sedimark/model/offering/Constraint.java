package eu.sedimark.model.offering;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.sedimark.config.OntologyDefinitions;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Constraint {
    @JsonProperty(OntologyDefinitions.ODRL_PREFIX + ":leftOperand")
    private String leftOperand;
    @JsonProperty(OntologyDefinitions.ODRL_PREFIX + ":operator")
    private IdOnly operator;
    @JsonProperty(OntologyDefinitions.ODRL_PREFIX + ":rightOperand")
    private Object rightOperand;
    @JsonProperty(OntologyDefinitions.ODRL_PREFIX + ":unit")
    private String unit;

    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }
}