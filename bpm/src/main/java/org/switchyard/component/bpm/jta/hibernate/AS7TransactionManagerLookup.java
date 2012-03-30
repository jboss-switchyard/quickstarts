/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.switchyard.component.bpm.jta.hibernate;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.transaction.TransactionManagerLookup;

/**
 * AS7TransactionManagerLookup.
 * <br/><br/>
 * See: <a href="http://kverlaen.blogspot.com/2011/07/jbpm5-on-as7-lightning.html">jBPM5 on AS7: Lightning !</a>
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class AS7TransactionManagerLookup implements TransactionManagerLookup {

    private static final Logger LOGGER = Logger.getLogger(AS7TransactionManagerLookup.class);

    /**
     * {@inheritDoc}
     * @Override
     */
    public Object getTransactionIdentifier(Transaction transaction) {
        return transaction;
    }

    /**
     * {@inheritDoc}
     * @Override
     */
    public TransactionManager getTransactionManager(Properties properties) throws HibernateException {
        return (TransactionManager)jndiLookup(properties, "java:jboss/TransactionManager");
    }

    /**
     * {@inheritDoc}
     * @Override
     */
    public String getUserTransactionName() {
        return "java:jboss/UserTransaction";
    }

    /**
     * Helper method to get the TransactionManager, if possible.
     * @return the TransactionManager, or null if it couldn't be found
     */
    public static TransactionManager getTransactionManager() {
        return (TransactionManager)jndiLookup(null, "java:jboss/TransactionManager");
    }

    /**
     * Helper method to get the UserTransaction, if possible.
     * @return the UserTransaction, or null if it couldn't be found
     */
    public static UserTransaction getUserTransaction() {
        return (UserTransaction)jndiLookup(null, "java:jboss/UserTransaction");
    }

    private static Object jndiLookup(Properties properties, String name) throws HibernateException {
        Context ctx = null;
        try {
            ctx = properties != null ? new InitialContext(properties) : new InitialContext();
            return ctx.lookup(name);
        } catch (NamingException ne) {
            LOGGER.error(ne.getMessage());
            throw new HibernateException(ne);
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Throwable t) {
                    LOGGER.error(t.getMessage());
                }
            }
        }
    }

}
