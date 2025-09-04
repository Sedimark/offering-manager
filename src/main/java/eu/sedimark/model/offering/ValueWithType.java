package eu.sedimark.model.offering;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ValueWithType {
    @JsonProperty("@value")
    private String value;

    @JsonProperty("@type")
    private String type;
}