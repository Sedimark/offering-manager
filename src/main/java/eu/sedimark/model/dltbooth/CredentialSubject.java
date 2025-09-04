package eu.sedimark.model.dltbooth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CredentialSubject {

    @JsonProperty("schema:alternateName")
    private String alternateName;

    @JsonProperty("schema:memberOf")
    private String memberOf;

}