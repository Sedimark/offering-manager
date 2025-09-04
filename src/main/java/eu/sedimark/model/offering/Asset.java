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
public class Asset {
    @JsonProperty("@id")
    private String id;

    @JsonProperty("@type")
    private String type;

    @JsonProperty(OntologyDefinitions.DCT_PREFIX + ":title")
    private ValueWithType title;

    @JsonProperty(OntologyDefinitions.SEDI_PREFIX + ":offeredBy")
    private IdOnly offeredBy;

    @JsonProperty(OntologyDefinitions.DCT_PREFIX + ":description")
    private ValueWithType description;

    @JsonProperty(OntologyDefinitions.DCT_PREFIX + ":issued")
    private ValueWithType issued;

    @JsonProperty(OntologyDefinitions.DCT_PREFIX + ":creator")
    private ValueWithType creator;

    @JsonProperty(OntologyDefinitions.DCAT_PREFIX + ":theme")
    private IdOnly theme;

    @JsonProperty(OntologyDefinitions.DCAT_PREFIX + ":keyword")
    private List<ValueWithType> keywords;

    @JsonProperty(OntologyDefinitions.DCT_PREFIX + ":spatial")
    private IdWithType spatial;

    @JsonProperty(OntologyDefinitions.SEDI_PREFIX + ":isProvidedBy")
    private AssetProvision isProvidedBy;

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
