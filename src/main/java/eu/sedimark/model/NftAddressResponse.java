package eu.sedimark.model;

public class NftAddressResponse {
    private String nftAddress;

    public NftAddressResponse() {}

    public NftAddressResponse(String nftAddress) {
        this.nftAddress = nftAddress;
    }

    public String getNftAddress() {
        return nftAddress;
    }

    public void setNftAddress(String nftAddress) {
        this.nftAddress = nftAddress;
    }
}
