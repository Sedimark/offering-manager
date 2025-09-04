package eu.sedimark.model.dltbooth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DidDocument {
    // Getters and Setters
    private String id;

    private List<VerificationMethod> verificationMethod;

    private List<Service> service;

}
