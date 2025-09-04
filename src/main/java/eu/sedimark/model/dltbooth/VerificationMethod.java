package eu.sedimark.model.dltbooth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerificationMethod {
    // Getters and Setters
    private String id;
    private String controller;
    private String type;

    private PublicKeyJwk publicKeyJwk;

    private String blockchainAccountId;  // Optional field

}
