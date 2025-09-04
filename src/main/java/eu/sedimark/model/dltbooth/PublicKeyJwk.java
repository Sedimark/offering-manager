package eu.sedimark.model.dltbooth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PublicKeyJwk {
    // Getters and Setters
    private String kty;
    private String alg;
    private String kid;
    private String crv;
    private String x;

}
