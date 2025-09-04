package eu.sedimark.model.offering;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdWithType {
    @JsonProperty("@id")
    private String id;

    @JsonProperty("@type")
    private String type;
}
