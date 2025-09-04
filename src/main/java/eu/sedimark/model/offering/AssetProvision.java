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
public class AssetProvision {
    @JsonProperty("@id")
    private String id;

    @JsonProperty("@type")
    private String type;

    @JsonProperty(OntologyDefinitions.DCT_PREFIX + ":title")
    private ValueWithType title;

    @JsonProperty(OntologyDefinitions.DCT_PREFIX + ":format")
    private IdOnly format;

    @JsonProperty(OntologyDefinitions.DCT_PREFIX + ":description")
    private ValueWithType description;

    @JsonProperty(OntologyDefinitions.DCT_PREFIX + ":issued")
    private ValueWithType issued;

    @JsonProperty(OntologyDefinitions.DCAT_PREFIX + ":accessURL")
    private IdOnly accessURL;

    @JsonProperty(OntologyDefinitions.SEDI_PREFIX + ":headers")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<Header> headers;

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
