package com.likanhp.smartassistant.datastore;

import com.likanhp.smartassistant.datastore.entity.ConfigurationEntity;

import javax.persistence.EntityManager;

/**
 * Wrapper for ConfigurationEntity.
 */
public class Configuration {
    public static String get(String key, String defaultValue) {
        EntityManager entityManager = DataStoreUtils.getEntityManagerFactory().createEntityManager();
        ConfigurationEntity entity = entityManager.find(ConfigurationEntity.class, key);
        return entity != null ? entity.getValue() : defaultValue;
    }

    public static void set(String key, String value) {
        ConfigurationEntity entity = new ConfigurationEntity();
        entity.setKey(key);
        entity.setValue(value);

        EntityManager entityManager = DataStoreUtils.getEntityManagerFactory().createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(entity);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    public void createTable() {
        EntityManager entityManager = DataStoreUtils.getEntityManagerFactory().createEntityManager();
    }
}
