package eu.sedimark.config;

import lombok.Getter;

public class Config {
    @Getter private static String containerEndpoint;
    @Getter private static String externalEndpoint;
    @Getter private static String internalConnectorEndpoint;
    @Getter private static String externalConnectorEndpoint;
    @Getter private static String internalDltBoothEndpoint;
    @Getter private static String hashAlgorithm;
    @Getter private static String postgresUsername;
    @Getter private static String postgresPassword;
    @Getter private static String postgresHost;
    @Getter private static String postgresPort;
    @Getter private static String postgresDatabase;
    @Getter private static boolean enablePostgresPersistence;
    @Getter private static boolean enableConnectorService;
    @Getter private static boolean enableDltBoothService;

    private Config() {}

    public static void initialize() {
        containerEndpoint = System.getenv("CONTAINER_ENDPOINT");
        externalEndpoint = System.getenv("EXTERNAL_ENDPOINT");
        internalConnectorEndpoint = System.getenv("INTERNAL_CONNECTOR_ENDPOINT");
        externalConnectorEndpoint = System.getenv("EXTERNAL_CONNECTOR_ENDPOINT");
        internalDltBoothEndpoint = System.getenv("INTERNAL_DLTBOOTH_ENDPOINT");
        hashAlgorithm = System.getenv("HASH_ALGORITHM");
        postgresUsername = System.getenv("POSTGRES_USER");
        postgresPassword = System.getenv("POSTGRES_PASSWORD");
        postgresHost = System.getenv("POSTGRES_HOST");
        postgresPort = System.getenv("POSTGRES_PORT");
        postgresDatabase = System.getenv("POSTGRES_DB");

        String enablePostgresPersistenceEnv = System.getenv("ENABLE_POSTGRES_PERSISTENCE");
        if (enablePostgresPersistenceEnv == null) {
            enablePostgresPersistence = true;
        } else {
            enablePostgresPersistence = Boolean.parseBoolean(enablePostgresPersistenceEnv);
        }

        String enableConnectorServiceEnv = System.getenv("ENABLE_CONNECTOR_SERVICE");
        if (enableConnectorServiceEnv == null) {
            enableConnectorService = true;
        } else {
            enableConnectorService = Boolean.parseBoolean(enableConnectorServiceEnv);
        }

        String enableDltBoothServiceEnv = System.getenv("ENABLE_DLTBOOTH_SERVICE");
        if (enableDltBoothServiceEnv == null) {
            enableDltBoothService = true;
        } else {
            enableDltBoothService = Boolean.parseBoolean(enableDltBoothServiceEnv);
        }

        if(!enableConnectorService || !enableDltBoothService) {
            enablePostgresPersistence = false;
        }

        if (containerEndpoint == null) throw new IllegalStateException("CONTAINER_ENDPOINT is required.");
        if (externalEndpoint == null) throw new IllegalStateException("EXTERNAL_ENDPOINT is required.");
        if (internalConnectorEndpoint == null) throw new IllegalStateException("INTERNAL_CONNECTOR_ENDPOINT is required.");
        if (externalConnectorEndpoint == null) throw new IllegalStateException("EXTERNAL_CONNECTOR_ENDPOINT is required.");
        if (internalDltBoothEndpoint == null) throw new IllegalStateException("INTERNAL_DLTBOOTH_ENDPOINT is required.");
        if (hashAlgorithm == null) throw new IllegalStateException("HASH_ALGORITHM is required.");
        if (enablePostgresPersistence) {
            if (postgresUsername == null) throw new IllegalStateException("POSTGRES_USER is required.");
            if (postgresPassword == null) throw new IllegalStateException("POSTGRES_PASSWORD is required.");
            if (postgresHost == null) throw new IllegalStateException("POSTGRES_HOST is required.");
            if (postgresPort == null) throw new IllegalStateException("POSTGRES_PORT is required.");
            if (postgresDatabase == null) throw new IllegalStateException("POSTGRES_DATABASE is required.");
        }
    }
}
