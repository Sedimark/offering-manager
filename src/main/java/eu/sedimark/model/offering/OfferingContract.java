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
public class OfferingContract {
    @JsonProperty("@id")
    private String id;

    @JsonProperty("@type")
    private String type;

    @JsonProperty(OntologyDefinitions.ODRL_PREFIX + ":profile")
    private String profile;

    @JsonProperty(OntologyDefinitions.ODRL_PREFIX + ":uid")
    private String uid;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @JsonProperty(OntologyDefinitions.ODRL_PREFIX + ":permission")
    private List<Permission> permissions;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @JsonProperty(OntologyDefinitions.ODRL_PREFIX + ":prohibition")
    private List<Prohibition> prohibitions;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @JsonProperty(OntologyDefinitions.ODRL_PREFIX + ":obligation")
    private List<Obligation> obligations;

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
