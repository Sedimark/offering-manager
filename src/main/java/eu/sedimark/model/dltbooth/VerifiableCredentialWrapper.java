package eu.sedimark.model.dltbooth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VerifiableCredentialWrapper {
    private long exp;
    private String iss;
    private long nbf;
    private String jti;
    private String sub;

    private VC vc;

}
