/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.common.transaction;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.switchyard.SwitchYardException;

/**
 * Simple factory which creates TransactedManager instances.
 * 
 * @author Daniel Bevenius
 */
public final class TransactionManagerFactory {

    /**
     * JBoss AS specific UserTransaction JNDI name.
     */
    public static final String JBOSS_USER_TRANSACTION = "java:jboss/UserTransaction";

    /**
     * JBoss AS specific TransactionManager JNDI name.
     */
    public static final String JBOSS_TRANSACTION_MANANGER = "java:jboss/TransactionManager";

    /**
     * JBoss AS specific TransactionSynchronizationRegistry JNDI name.
     */
    public static final String JBOSS_TRANSACTION_SYNC_REG = "java:jboss/TransactionSynchronizationRegistry";

    /**
     * Configuration name for the JtaTransactionManager.
     */
    public static final String TM = "jtaTransactionManager";

    private static final TransactionManagerFactory INSTANCE = new TransactionManagerFactory();

    private TransactionManagerFactory() {
    }

    /**
     * Gets the singleton instance.
     * 
     * @return TransactionManagerFactory the singleton instance.
     */
    public static TransactionManagerFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Factory method that creates a {@link PlatformTransactionManager}.
     * 
     * @return {@link PlatformTransactionManager} the created PlatformTransactionManager.
     */
    public PlatformTransactionManager create() {
        final JtaTransactionManager transactionManager = new JtaTransactionManager();

        if (isBound(JBOSS_USER_TRANSACTION)) {
            transactionManager.setUserTransactionName(JBOSS_USER_TRANSACTION);
            transactionManager.setTransactionManagerName(JBOSS_TRANSACTION_MANANGER);
            transactionManager.setTransactionSynchronizationRegistryName(JBOSS_TRANSACTION_SYNC_REG);
        } else if (isBound(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME)) {
            transactionManager.setUserTransactionName(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME);
        } else {
            throw new SwitchYardException("Could not create a JtaTransactionManager as no TransactionManager was found"
                    + " in JNDI. Tried [" + JBOSS_USER_TRANSACTION + ", " 
                    + JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME + "]");
        }

        // Initialize the transaction manager.
        transactionManager.afterPropertiesSet();
        return transactionManager;
    }

    private boolean isBound(final String jndiName) {
       return lookupInJndi(jndiName) != null; 
    }

    private Object lookupInJndi(final String name) {
        InitialContext context = null;
        try {
            context = new InitialContext();
            return context.lookup(name);
        } catch (final NamingException e) {
            return null;
        } catch (final Exception e) {
            throw new SwitchYardException("Unexpected Exception retrieving '" + name + "' from JNDI namespace.", e);
        } finally {
            if (context != null) {
                try {
                    context.close();
                } catch (final Exception e) {
                    throw new SwitchYardException("Unexpected error closing InitialContext.", e);
                }
            }
        }
    }

}
