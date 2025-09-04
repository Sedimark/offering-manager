package eu.sedimark.model.persistence;

import eu.sedimark.model.offering.Offering;

import java.util.*;
import java.util.logging.Logger;

public class InMemoryOfferingStorage implements OfferingStorage {

    private final Map<String, Offering> offerings = new HashMap<>();
    private final Map<String, String> nftAddresses = new HashMap<>();
    private final Map<String, String> hashes = new HashMap<>();

    private static Logger LOGGER;

    public InMemoryOfferingStorage() {
        LOGGER = Logger.getLogger(this.getClass().getName());
    }

    @Override
    public void save(Offering offering, String nftAddress, String offeringHash) {
        offerings.put(offering.getId(), offering);
        nftAddresses.put(offering.getId(), nftAddress);
        hashes.put(offering.getId(), offeringHash);
    }

    @Override
    public Offering findById(String id) {
        return offerings.get(id);
    }

    @Override
    public String findNftAddress(String id) {
        return nftAddresses.get(id);
    }

    @Override
    public String findOfferingHash(String id) {
        return hashes.get(id);
    }

    @Override
    public void deleteById(String id) {
        offerings.remove(id);
        nftAddresses.remove(id);
        hashes.remove(id);
    }

    @Override
    public Map<String, Offering> findAll() {
        return new HashMap<>(offerings);
    }

    @Override
    public Map<String, Offering> findAll(int page, int size) {
        List<Map.Entry<String, Offering>> entries = new ArrayList<>(offerings.entrySet());

        int fromIndex = page * size;
        if (fromIndex >= entries.size()) {
            return Collections.emptyMap();
        }
        int toIndex = Math.min(fromIndex + size, entries.size());

        Map<String, Offering> result = new LinkedHashMap<>();
        for (Map.Entry<String, Offering> entry : entries.subList(fromIndex, toIndex)) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public int countOfferings() {
        return offerings.size();
    }
}
