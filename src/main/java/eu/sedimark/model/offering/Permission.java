package eu.sedimark.model.offering;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.sedimark.config.OntologyDefinitions;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Permission {
    @JsonProperty(OntologyDefinitions.ODRL_PREFIX + ":target")
    private String target;
    @JsonProperty(OntologyDefinitions.ODRL_PREFIX + ":assigner")
    private String assigner;
    @JsonProperty(OntologyDefinitions.ODRL_PREFIX + ":action")
    private String action;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @JsonProperty(OntologyDefinitions.ODRL_PREFIX + ":constraint")
    private List<Constraint> constraint;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @JsonProperty(OntologyDefinitions.ODRL_PREFIX + ":duty")
    private List<Duty> duty;

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
