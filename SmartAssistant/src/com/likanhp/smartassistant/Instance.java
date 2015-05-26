package com.likanhp.smartassistant;

import com.likanhp.smartassistant.datastore.DataStoreUtils;
import com.likanhp.smartassistant.util.LoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main entrance of SmartAssistant.
 */
public class Instance {
    private static final Logger logger = LoggerFactory.getLogger(Instance.class);
    private static final Instance instance = new Instance();

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public static void main(String[] args) throws Exception {
        instance.run();
    }

    public boolean isInitialized() {
        return initialized.get();
    }

    public static Instance getInstance() {
        if (!instance.isInitialized()) {
            LoggerUtils.fatalAndExit(logger, "Instance object not yet initialized.");
        }
        return instance;
    }

    private Instance() {
    }

    private void run() throws Exception {
        try {
            initialize();
        } finally {
            shutdown();
        }
    }

    private void initialize() throws Exception {
        if (initialized.get()) {
            LoggerUtils.fatalAndExit(logger, "Already initialized");
        }

        DataStoreUtils.initializeDataStore();
        initialized.set(true);
    }

    private void shutdown() {
        DataStoreUtils.closeDataStore();
    }
}
