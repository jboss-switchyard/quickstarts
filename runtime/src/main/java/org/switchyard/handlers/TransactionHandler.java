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

package org.switchyard.handlers;

import javax.naming.InitialContext;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.HandlerException;
import org.switchyard.policy.PolicyUtil;
import org.switchyard.policy.TransactionPolicy;


/**
 * Interprets transactional policy specified on an exchange and handles 
 * transactional requirements.
 */
public class TransactionHandler implements ExchangeHandler {
    
    private static final String JNDI_TRANSACTION_MANAGER = "java:jboss/TransactionManager";
    private static final String SUSPENDED_TRANSACTION_PROPERTY = 
            "org.switchyard.exchange.transaction.suspended";
    private static final String INITIATED_TRANSACTION_PROPERTY = 
            "org.switchyard.exchange.transaction.initiated";
    
    private static Logger _log = Logger.getLogger(TransactionHandler.class);
    
    private TransactionManager _transactionManager;

    /**
     * Create a new TransactionHandler.
     */
    public TransactionHandler() {
        try {
            _transactionManager = (TransactionManager)
                new InitialContext().lookup(JNDI_TRANSACTION_MANAGER);
        } catch (javax.naming.NamingException nmEx) {
            _log.debug("Unable to find TransactionManager in JNDI at " + JNDI_TRANSACTION_MANAGER 
                    + " - Transaction Policy handling will not be available.");
        }
    }
    
    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        // if no TM is available, there's nothing to do
        if (_transactionManager == null) {
            return;
        }
        
        // execute policy behavior based on exchange phase
        if (managedGlobalProvided(exchange) || noManagedProvided(exchange)) {
            // OUT phase in IN_OUT exchange or 2nd invocation in IN_ONLY exchange
            handleAfter(exchange);
        } else {
            handleBefore(exchange);
        }
    }
    
    @Override
    public void handleFault(Exchange exchange) {
        try {
            Transaction transaction = (Transaction)
                    exchange.getContext().getPropertyValue(INITIATED_TRANSACTION_PROPERTY);
            if (transaction != null) {
                transaction.setRollbackOnly();
            }
            handleAfter(exchange);
        } catch (Exception e) {
            _log.error(e);
        }
    }
    
    void setTransactionManager(TransactionManager transactionManager) {
        _transactionManager = transactionManager;
    }
    
    TransactionManager getTransactionManager() {
        return _transactionManager;
    }
    
    private void handleAfter(Exchange exchange) throws HandlerException {
        Transaction transaction = null;
        try {
            // complete the transaction which is initiated by this handler
            transaction = (Transaction) exchange.getContext().getPropertyValue(INITIATED_TRANSACTION_PROPERTY);
            if (transaction != null) {
                endTransaction();
            }
        } catch (Exception e) {
            throw new HandlerException("TransactionHandler failed to complete a transaction", e);
        } finally {
            // resume the transaction which is suspended by this handler
            transaction = (Transaction) exchange.getContext().getPropertyValue(SUSPENDED_TRANSACTION_PROPERTY);
            if (transaction != null) {
                resumeTransaction(transaction);
            }
        }
    }
    
    private void handleBefore(Exchange exchange) throws HandlerException {
        // check for incompatible policy definition 
        if (suspendsRequired(exchange) && propagatesRequired(exchange)) {
            throw new HandlerException("Invalid transaction policy : "
                + TransactionPolicy.SUSPENDS_TRANSACTION + " and " + TransactionPolicy.PROPAGATES_TRANSACTION
                + " cannot be requested simultaneously.");
        }
        if (managedLocalRequired(exchange)) {
            throw new HandlerException("Unsupported transaction policy : "
                    + TransactionPolicy.MANAGED_TRANSACTION_LOCAL + " is not supported for now. Use "
                    + TransactionPolicy.SUSPENDS_TRANSACTION + " on the callee service instead.");
        }
        if (managedGlobalRequired(exchange) && noManagedRequired(exchange)) {
            throw new HandlerException("Invalid transaction policy : "
                    + TransactionPolicy.MANAGED_TRANSACTION_GLOBAL + ", " + TransactionPolicy.MANAGED_TRANSACTION_LOCAL
                    + " and " + TransactionPolicy.NO_MANAGED_TRANSACTION + " cannot be requested simultaneously with each other.");
        }
        if (propagatesRequired(exchange) && noManagedRequired(exchange)) {
            throw new HandlerException("Invalid transaction policy : "
                    + TransactionPolicy.PROPAGATES_TRANSACTION + " cannot be requested with "
                    + TransactionPolicy.MANAGED_TRANSACTION_LOCAL + " nor " + TransactionPolicy.NO_MANAGED_TRANSACTION);
        }

        // platform default behavior: do not touch the transaction at all if interaction policy is absent
        if (!suspendsRequired(exchange) && !(propagatesRequired(exchange))) {
            return;
        }
        
        if (propagatesRequired(exchange)) {
            if (propagatesProvided(exchange)) {
                PolicyUtil.provide(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL);

            } else if (managedGlobalRequired(exchange)) {
                // propagates & managedGlobal are required but transaction doesn't exist: create new one
                try {
                    startTransaction(exchange);
                } catch (Exception e) {
                    throw new HandlerException("TransactionHandler failed to initiate a transaction", e);
                }
                PolicyUtil.provide(exchange, TransactionPolicy.PROPAGATES_TRANSACTION);
                PolicyUtil.provide(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL);

            } else {
                // platform default behavior: raise an error if propagates required and implementation policy is absent,
                // however, active transaction is not provided
                throw new HandlerException(
                        "Transaction policy requires an active transaction, but no transaction is present.");
            }
            return;
        }
        
        if (suspendsRequired(exchange)) {
            if (_log.isDebugEnabled()) {
                _log.debug("Suspending active transaction for exchange.");
            }

            suspendTransaction(exchange);
            // if an active transaction was present, it has been suspended -
            // mark the policy requirement as provided.
            PolicyUtil.provide(exchange, TransactionPolicy.SUSPENDS_TRANSACTION);
            
            if (managedGlobalRequired(exchange)) {
                // start new global transaction
                try {
                    startTransaction(exchange);
                } catch (Exception e) {
                    throw new HandlerException("TransactionHandler failed to initiate a transaction", e);
                }
                PolicyUtil.provide(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL);

            } else {
                // requested implementation policy is noManaged or absent
                PolicyUtil.provide(exchange, TransactionPolicy.NO_MANAGED_TRANSACTION);
            }
        }
    }

    private boolean propagatesProvided(Exchange exchange) {
        return PolicyUtil.isProvided(exchange, TransactionPolicy.PROPAGATES_TRANSACTION);
    }
    
    private boolean managedGlobalProvided(Exchange exchange) {
        return PolicyUtil.isProvided(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL);
    }

    private boolean noManagedProvided(Exchange exchange) {
        return PolicyUtil.isProvided(exchange, TransactionPolicy.NO_MANAGED_TRANSACTION);
    }
    
    private boolean managedGlobalRequired(Exchange exchange) {
        return PolicyUtil.isRequired(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL);
    }

    private boolean managedLocalRequired(Exchange exchange) {
        return PolicyUtil.isRequired(exchange, TransactionPolicy.MANAGED_TRANSACTION_LOCAL);
    }
    
    private boolean noManagedRequired(Exchange exchange) {
        return PolicyUtil.isRequired(exchange, TransactionPolicy.NO_MANAGED_TRANSACTION);
    }
    
    private boolean suspendsRequired(Exchange exchange) {
        return PolicyUtil.isRequired(exchange, TransactionPolicy.SUSPENDS_TRANSACTION);
    }
    
    private boolean propagatesRequired(Exchange exchange) {
        return PolicyUtil.isRequired(exchange, TransactionPolicy.PROPAGATES_TRANSACTION);
    }
    
    private void startTransaction(Exchange exchange) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("creating new transaction");
        }

        if (_transactionManager.getStatus() == Status.STATUS_NO_TRANSACTION) {
            _transactionManager.begin();
            Transaction transaction = _transactionManager.getTransaction();
            exchange.getContext().setProperty(INITIATED_TRANSACTION_PROPERTY, transaction);
        } else {
            throw new HandlerException("Transaction already exists");
        }
    }
    
    private void endTransaction() throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("completing transaction");
        }
        
        Transaction transaction = _transactionManager.getTransaction();
        if (transaction.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
            transaction.rollback();
        } else if (transaction.getStatus() == Status.STATUS_ACTIVE) {
            transaction.commit();
        } else {
            throw new HandlerException("Could not complete transaction due to invalid status - code="
                    + transaction.getStatus() + ": see javax.transaction.Status.");
        }
    }

    private void suspendTransaction(Exchange exchange) {
        Transaction transaction = null;
        try {
                transaction = _transactionManager.suspend();
        } catch (SystemException sysEx) {
            _log.error("Failed to suspend transaction on exchange.", sysEx);
        }
        if (transaction != null) {
            exchange.getContext().setProperty(SUSPENDED_TRANSACTION_PROPERTY, transaction);
        }
    }
    
    private void resumeTransaction(Transaction transaction) {
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("Resuming suspended transaction");
            }
            _transactionManager.resume(transaction);
        } catch (Exception ex) {
            _log.error("Failed to resume transaction after service invocation.", ex);
        }
    }
}
