package eu.sedimark.model.dltbooth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferingNft {
    private String name;
    private String symbol = "SDK";
    private String descriptionUri;
    private String descriptionHash;
    private String dtName = "SEDIMARK";
    private String dtSymbol = "OFFER";
    private int maxSupply = 12;
}