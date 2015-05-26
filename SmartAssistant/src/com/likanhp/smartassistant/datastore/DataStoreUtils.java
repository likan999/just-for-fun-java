package com.likanhp.smartassistant.datastore;

import com.likanhp.smartassistant.util.LoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Utilities related to data store.
 */
public class DataStoreUtils {
    private static final Logger logger = LoggerFactory.getLogger(DataStoreUtils.class);
    private static final String ENV_DBPATH = "sa.main.dbpath";
    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    private static volatile EntityManagerFactory entityManagerFactory;

    public static boolean isInitialized() {
        return initialized.get();
    }

    public static void initializeDataStore() throws SQLException {
        if (System.getProperty(ENV_DBPATH) == null) {
            String path = DataStoreUtils.class.getClassLoader().getResource(".").getPath();
            System.setProperty(ENV_DBPATH, path);
            logger.debug("Setting system property {}={}", ENV_DBPATH, path);
        }

        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            LoggerUtils.fatalAndExit(logger, "Failed to load HSQLDB JDBC driver", e);
        }

        String databaseUrl = String.format("jdbc:hsqldb:file:${%s}/db;" +
                "shutdown=true;hsqldb.write_delay=false", ENV_DBPATH);
        logger.debug("Connecting to database {}", databaseUrl);
        try (Connection connection = DriverManager.getConnection(databaseUrl);
             Statement statement = connection.createStatement()) {
            if (!connection.getMetaData().getSchemas(null, "MAIN").next()) {
                statement.executeUpdate("CREATE SCHEMA MAIN AUTHORIZATION SA");
            }
            statement.executeUpdate("CREATE CACHED TABLE IF NOT EXISTS MAIN.configuration (" +
                    "key LONGVARCHAR NOT NULL PRIMARY KEY, value LONGVARCHAR NOT NULL)");
            connection.commit();
        }
        initialized.set(true);
    }

    public static void closeDataStore() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (!isInitialized()) {
            LoggerUtils.fatalAndExit(logger, "Can't create EntityManagerFactory before it is initialized");
        }

        // Double-checked idiom copied from org.apache.commons.lang3.concurrent.LazyInitializer.
        EntityManagerFactory factory = entityManagerFactory;
        if (factory == null) {
            synchronized (DataStoreUtils.class) {
                factory = entityManagerFactory;
                if (factory == null) {
                    entityManagerFactory = factory = Persistence.createEntityManagerFactory("MainPersistenceUnit");
                }
            }
        }
        return factory;
    }
}
