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
public class Offering {
    @JsonProperty("@context")
    private Map<String, Object> context;

    @JsonProperty("@id")
    private String id;

    @JsonProperty("@type")
    private String type;

    @JsonProperty(OntologyDefinitions.SEDI_PREFIX + ":isListedBy")
    private SelfListing isListedBy;

    @JsonProperty(OntologyDefinitions.SEDI_PREFIX + ":hasOfferingContract")
    private OfferingContract hasOfferingContract;

    @JsonProperty(OntologyDefinitions.DCT_PREFIX + ":issued")
    private ValueWithType issued;

    @JsonProperty(OntologyDefinitions.DCT_PREFIX + ":language")
    private ValueWithType language;

    @JsonProperty(OntologyDefinitions.DCT_PREFIX + ":title")
    private ValueWithType title;

    @JsonProperty(OntologyDefinitions.DCT_PREFIX + ":description")
    private ValueWithType description;

    @JsonProperty(OntologyDefinitions.DCT_PREFIX + ":publisher")
    private IdOnly publisher;

    @JsonProperty(OntologyDefinitions.DCT_PREFIX + ":creator")
    private ValueWithType creator;

    @JsonProperty(OntologyDefinitions.DCAT_PREFIX + ":themeTaxonomy")
    private IdOnly themeTaxonomy;

    @JsonProperty(OntologyDefinitions.DCT_PREFIX + ":license")
    private ValueWithType license;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @JsonProperty(OntologyDefinitions.SEDI_PREFIX + ":hasAsset")
    private List<Asset> hasAsset;

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
