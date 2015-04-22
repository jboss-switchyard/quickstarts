/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.runtime.util;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;

import org.jboss.logging.Logger;
import org.switchyard.common.util.ProviderRegistry;

/**
 * A TransactionManagerLocator.
 */
public final class TransactionManagerLocator {

    private static final String JBOSS_JNDI_TRANSACTION_MANAGER = "java:jboss/TransactionManager";
    private static final String OSGI_JNDI_TRANSACTION_MANAGER = "osgi:service/javax.transaction.TransactionManager";

    private static final Logger LOGGER = Logger.getLogger(TransactionManagerLocator.class);
    private TransactionManagerLocator() {
    }

    /**
     * Lookup TransactionManager.
     * @return TransactionManager
     */
    public static TransactionManager locateTransactionManager() {
        TransactionManager tm = ProviderRegistry.getProvider(TransactionManager.class);
        if (tm != null) {
            LOGGER.debug("Found a TransactionManager in ProviderRegistry");
            return tm;
        }

        InitialContext ic;
        try {
            ic = new InitialContext();
        } catch (Exception e) {
            LOGGER.debug("Failed to create InitialContext - Transaction Policy handling will not be available.", e);
            return null;
        }

        try {
            tm = (TransactionManager) lookupInJndi(ic, JBOSS_JNDI_TRANSACTION_MANAGER);
            if (tm != null) {
                LOGGER.debug("Found a TransactionManager in JNDI at '" + JBOSS_JNDI_TRANSACTION_MANAGER + "'");
                return tm;
            }

            tm = (TransactionManager) lookupInJndi(ic, OSGI_JNDI_TRANSACTION_MANAGER);
            if (tm != null) {
                LOGGER.debug("Found a TransactionManager in JNDI at '" + OSGI_JNDI_TRANSACTION_MANAGER + "'");
                return tm;
            }
        } finally {
            try {
                ic.close();
            } catch (Exception e) {
                LOGGER.debug(e);
            }
        }
        LOGGER.debug("No TransactionManager found at '" + JBOSS_JNDI_TRANSACTION_MANAGER
                    + "' nor '" + OSGI_JNDI_TRANSACTION_MANAGER + "'");
        return null;
    }

    private static Object lookupInJndi(InitialContext ic, String name) {
        try {
            return ic.lookup(name);
        } catch (Exception e) {
            LOGGER.debug(e);
            return null;
        }
    }
}
