package eu.sedimark.model.dltbooth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class VC {

    @JsonProperty("@context")
    private List<Object> context;

    private List<String> type;

    private CredentialSubject credentialSubject;

}
