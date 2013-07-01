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
package org.switchyard.component.bpm.transaction;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.switchyard.HandlerException;

/**
 * AS7TransactionHelper.
 *
 * @author Tomohisa Igarashi &lt;<a href="mailto:tigarash@redhat.com">tigarash@redhat.com</a>&gt; &copy; 2012 Red Hat Inc.
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class AS7TransactionHelper {

    private static final Logger LOGGER = Logger.getLogger(AS7TransactionHelper.class);

    /** java:jboss/TransactionManager . */
    public static final String JNDI_TRANSACTION_MANAGER = "java:jboss/TransactionManager";
    /** java:jboss/UserTransaction . */
    public static final String JNDI_USER_TRANSACTION = "java:jboss/UserTransaction";

    private final boolean _enabled;
    private UserTransaction _userTx = null;
    private boolean _isInitiator = false;

    /**
     * Constructs a new utx helper.
     * @param enabled if enabled
     */
    public AS7TransactionHelper(boolean enabled) {
        _enabled = enabled;
    }

    /**
     * Begin.
     * @throws HandlerException oops
     */
    public void begin() throws HandlerException {
        if (_enabled) {
            try {
                _userTx = getUserTransaction();
                if (_userTx.getStatus() == Status.STATUS_NO_TRANSACTION) {
                    _userTx.begin();
                    _isInitiator = true;
                }
            } catch (SystemException se) {
                throw new HandlerException("UserTransaction begin failed", se);
            } catch (NotSupportedException nse) {
                throw new HandlerException("UserTransaction begin failed", nse);
            }
        }
    }

    /**
     * Commit.
     * @throws HandlerException oops
     */
    public void commit() throws HandlerException {
        if (_isInitiator) {
            try {
                _userTx.commit();
            } catch (SystemException se) {
                throw new HandlerException("UserTransaction commit failed", se);
            } catch (HeuristicRollbackException hre) {
                throw new HandlerException("UserTransaction commit failed", hre);
            } catch (HeuristicMixedException hme) {
                throw new HandlerException("UserTransaction commit failed", hme);
            } catch (RollbackException re) {
                throw new HandlerException("UserTransaction commit failed", re);
            }
        }
    }

    /**
     * Rollback.
     * @throws HandlerException oops
     */
    public void rollback() throws HandlerException {
        if (_isInitiator) {
            try {
                _userTx.rollback();
            } catch (SystemException se) {
                throw new HandlerException("UserTransaction rollback failed", se);
            }
        } else if (_userTx != null) {
            try {
                _userTx.setRollbackOnly();
            } catch (SystemException se) {
                throw new HandlerException("UserTransaction setRollbackOnly failed", se);
            }
        }
    }

    /**
     * Helper method to get the TransactionManager, if possible.
     * @return the TransactionManager
     */
    public static TransactionManager getTransactionManager() {
        return getTransactionManager(null);
    }

    /**
     * Helper method to get the TransactionManager, if possible.
     * @param properties optional jndi initial context properties, or null
     * @return the TransactionManager
     */
    public static TransactionManager getTransactionManager(Properties properties) {
        return (TransactionManager)jndiLookup(JNDI_TRANSACTION_MANAGER, properties);
    }

    /**
     * Helper method to get the UserTransaction, if possible.
     * @return the UserTransaction
     */
    public static UserTransaction getUserTransaction() {
        return (UserTransaction)jndiLookup(JNDI_USER_TRANSACTION, null);
    }

    private static Object jndiLookup(String name, Properties properties) {
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
