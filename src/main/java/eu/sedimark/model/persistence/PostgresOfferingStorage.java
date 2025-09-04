package eu.sedimark.model.persistence;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sedimark.config.Config;
import eu.sedimark.exception.StorageException;
import eu.sedimark.exception.TechnicalException;
import eu.sedimark.model.offering.Offering;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class PostgresOfferingStorage implements OfferingStorage {

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final ObjectMapper offeringMapper;

    public PostgresOfferingStorage() {
        this.jdbcUrl = "jdbc:postgresql://" + Config.getPostgresHost() + ":"
                + Config.getPostgresPort() + "/" + Config.getPostgresDatabase();
        this.username = Config.getPostgresUsername();
        this.password = Config.getPostgresPassword();
        this.offeringMapper = new ObjectMapper();
        offeringMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        offeringMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
            ensureTableExists();
        } catch (SQLException e) {
            throw new TechnicalException("Error ensuring offerings table exists in PostgreSQL", e);
        }
    }

    private Connection getConnection() {
        try {
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            throw new StorageException("Error while connecting to PostgreSQL database " + this.jdbcUrl, e);
        }
    }

    private void ensureTableExists() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS offerings (
                id TEXT PRIMARY KEY,
                offering_json JSONB NOT NULL,
                nft_address TEXT,
                offering_hash TEXT
            )
        """;
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        stmt.execute(sql);
    }

    @Override
    public void save(Offering offering, String nftAddress, String offeringHash)  throws StorageException {
        String sql = """
            INSERT INTO offerings (id, offering_json, nft_address, offering_hash)
            VALUES (?, ?::jsonb, ?, ?)
            ON CONFLICT (id)
            DO UPDATE SET
              offering_json = EXCLUDED.offering_json,
              nft_address = EXCLUDED.nft_address,
              offering_hash = EXCLUDED.offering_hash
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, offering.getId());
            ps.setString(2, this.offeringMapper.writeValueAsString(offering));
            ps.setString(3, nftAddress);
            ps.setString(4, offeringHash);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new StorageException("Error saving offering to PostgreSQL", e);
        }
    }

    @Override
    public Offering findById(String id) {
        String sql = "SELECT offering_json FROM offerings WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String json = rs.getString("offering_json");
                return this.offeringMapper.readValue(json, Offering.class);
            }
        } catch (Exception e) {
            throw new StorageException("Error retrieving offering from PostgreSQL", e);
        }
        return null;
    }

    @Override
    public String findNftAddress(String id) {
        return querySingleColumn(id, "nft_address");
    }

    @Override
    public String findOfferingHash(String id) {
        return querySingleColumn(id, "offering_hash");
    }

    private String querySingleColumn(String id, String column) {
        String sql = "SELECT " + column + " FROM offerings WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            throw new StorageException("Error querying column " + column, e);
        }
        return null;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM offerings WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException("Error deleting Offering " + id + " from PostgreSQL database", e);
        }
    }

    @Override
    public Map<String, Offering> findAll() {
        Map<String, Offering> result = new HashMap<>();
        String sql = "SELECT id, offering_json FROM offerings";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                String json = rs.getString("offering_json");
                Offering offering = this.offeringMapper.readValue(json, Offering.class);
                result.put(id, offering);
            }
        } catch (Exception e) {
            throw new StorageException("Error fetching all offerings", e);
        }
        return result;
    }

    @Override
    public Map<String, Offering> findAll(int page, int size) {
        Map<String, Offering> result = new HashMap<>();
        String sql = "SELECT id, offering_json FROM offerings ORDER BY id LIMIT ? OFFSET ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, size);
            ps.setInt(2, page * size);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    String json = rs.getString("offering_json");
                    Offering offering = this.offeringMapper.readValue(json, Offering.class);
                    result.put(id, offering);
                }
            }
        } catch (Exception e) {
            throw new StorageException("Error fetching paginated offerings", e);
        }
        return result;
    }

    @Override
    public int countOfferings() {
        String sql = "SELECT COUNT(*) FROM offerings";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new StorageException("Error counting offerings", e);
        }
        return 0;
    }
}
