package org.dpppt.backend.sdk.data.util;

import org.testcontainers.containers.PostgreSQLContainer;

// JVM handles shut down
public class SingletonPostgresContainer {

    private static final String IMAGE_VERSION = "postgres:11.6";
    private static final String DB_URL = "DB_URL";
    private static final String DB_PORT = "DB_PORT";
    private static final String DB_USERNAME = "DB_USERNAME";
    private static final String DB_PASSWORD = "DB_PASSWORD";
    private static SingletonPostgresContainer INSTANCE;

    private PostgreSQLContainer<?> container;

    private SingletonPostgresContainer() {
        container = new PostgreSQLContainer<>(IMAGE_VERSION)
            .withUsername("test")
            .withPassword("test")
            .withDatabaseName("test-db");
    }

    public static SingletonPostgresContainer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SingletonPostgresContainer();
        }
        return INSTANCE;
    }

    public void start() {
        String baseUrl = "jdbc:postgresql://%s:%s/test-db";
        String dbUrl = System.getenv(DB_URL);
        String dbPort = System.getenv(DB_PORT);
        String jdbcUrl = String.format(baseUrl, dbUrl, dbPort);
        String username = System.getenv(DB_USERNAME);
        String password = System.getenv(DB_PASSWORD);

        // avoid start container if is already up
        if (System.getenv("SKIP_POSTGRES_CONTAINER") == null) {
            container.start();
            jdbcUrl = container.getJdbcUrl();
            username = container.getUsername();
            password = container.getPassword();
        }

        System.setProperty(DB_URL, jdbcUrl);
        System.setProperty(DB_USERNAME, username);
        System.setProperty(DB_PASSWORD, password);
    }

    public String getDriverClassName() {
        return container.getDriverClassName();
    }

    public String getJdbcUrl() {
        return container.getJdbcUrl();
    }

    public String getDatabaseName() {
        return container.getDatabaseName();
    }

    public String getUsername() {
        return container.getUsername();
    }

    public String getPassword() {
        return container.getPassword();
    }
}
