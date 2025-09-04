package eu.sedimark.model.persistence;

import eu.sedimark.exception.StorageException;
import eu.sedimark.model.offering.Offering;

import java.util.Map;
import java.util.Optional;

public interface OfferingStorage {
    void save(Offering offering, String nftAddress, String offeringHash);
    Offering findById(String id);
    String findNftAddress(String id);
    String findOfferingHash(String id);
    void deleteById(String id);
    Map<String, Offering> findAll();

    /**
     * Paginated retrieval of offerings.
     *
     * @param page zero-based page index
     * @param size maximum number of items per page
     * @return a map of offerings keyed by id
     */
    Map<String, Offering> findAll(int page, int size);

    int countOfferings();
}