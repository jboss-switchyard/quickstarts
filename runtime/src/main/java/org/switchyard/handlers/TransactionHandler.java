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
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.policy.PolicyUtil;
import org.switchyard.policy.TransactionPolicy;
import org.switchyard.runtime.RuntimeLogger;
import org.switchyard.runtime.RuntimeMessages;


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
    private static final String BEFORE_INVOKED_PROPERTY =
            "org.switchyard.exchange.transaction.beforeInvoked";
    
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
        
        Property prop = exchange.getContext().getProperty(BEFORE_INVOKED_PROPERTY, Scope.EXCHANGE);
        if (prop != null && Boolean.class.cast(prop.getValue())) {
            // OUT phase in IN_OUT exchange or 2nd invocation in IN_ONLY exchange
            handleAfter(exchange);
        } else {
            exchange.getContext().setProperty(BEFORE_INVOKED_PROPERTY, Boolean.TRUE, Scope.EXCHANGE).addLabels(BehaviorLabel.TRANSIENT.label());
            handleBefore(exchange);
        }
    }
    
    @Override
    public void handleFault(Exchange exchange) {
        // if no TM is available, there's nothing to do
        if (_transactionManager == null) {
            return;
        }
        
        try {
            Property rollbackOnFaultProperty = exchange.getContext().getProperty(Exchange.ROLLBACK_ON_FAULT);
            if (rollbackOnFaultProperty != null && rollbackOnFaultProperty.getValue() != null
                    && Boolean.class.cast(rollbackOnFaultProperty.getValue())) {
                    Transaction transaction = getCurrentTransaction();
                    if (transaction != null) {
                        transaction.setRollbackOnly();
                    }
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
            throw RuntimeMessages.MESSAGES.failedToCompleteTransaction(e);
        } finally {
            // resume the transaction which is suspended by this handler
            transaction = (Transaction) exchange.getContext().getPropertyValue(SUSPENDED_TRANSACTION_PROPERTY);
            if (transaction != null) {
                resumeTransaction(transaction);
            }
        }
    }
    
    private void handleBefore(Exchange exchange) throws HandlerException {
        if (!(propagatesRequired(exchange) || suspendsRequired(exchange) || managedGlobalRequired(exchange)
                || managedLocalRequired(exchange) || noManagedRequired(exchange))) {
            return;
        }
        
        evaluatePolicyCombination(exchange);
        evaluateTransactionStatus(exchange);
        
        if (isEligibleToSuspendTransaction(exchange)) {
            suspendTransaction(exchange);
        }
        
        if (isEligibleToStartTransaction(exchange)) {
            startTransaction(exchange);
        }
        
        provideRequiredPolicies(exchange);
    }

    private void evaluatePolicyCombination(Exchange exchange) throws HandlerException {
        // check for incompatible policy definition 
        if (suspendsRequired(exchange) && propagatesRequired(exchange)) {
            throw RuntimeMessages.MESSAGES.invalidTransactionPolicy(TransactionPolicy.SUSPENDS_TRANSACTION.toString(), 
                    TransactionPolicy.PROPAGATES_TRANSACTION.toString());
        }
        if (managedGlobalRequired(exchange) && managedLocalRequired(exchange)
                || managedGlobalRequired(exchange) && noManagedRequired(exchange)
                || managedLocalRequired(exchange) && noManagedRequired(exchange)) {
            throw RuntimeMessages.MESSAGES.invalidTransactionPolicy(TransactionPolicy.MANAGED_TRANSACTION_GLOBAL.toString(), 
                    TransactionPolicy.NO_MANAGED_TRANSACTION.toString());
        }
        if (propagatesRequired(exchange) && managedLocalRequired(exchange)
                || propagatesRequired(exchange) && noManagedRequired(exchange)) {
            throw RuntimeMessages.MESSAGES.invalidTransactionPolicyCombo(TransactionPolicy.PROPAGATES_TRANSACTION.toString(), 
                    TransactionPolicy.MANAGED_TRANSACTION_LOCAL.toString(), TransactionPolicy.NO_MANAGED_TRANSACTION.toString());
        }
    }
    
    private void evaluateTransactionStatus(Exchange exchange) throws HandlerException {
        Transaction transaction = getCurrentTransaction();

        if (transaction == null && propagatesRequired(exchange) && !managedGlobalRequired(exchange)) {
            throw RuntimeMessages.MESSAGES.invalidTransactionStatus(TransactionPolicy.PROPAGATES_TRANSACTION.toString());
        }
    }
    
    private boolean isEligibleToSuspendTransaction(Exchange exchange) throws HandlerException {
        Transaction transaction = getCurrentTransaction();
        if (transaction == null) {
           return false;
       }

        if (managedLocalRequired(exchange) || noManagedRequired(exchange) || suspendsRequired(exchange)) {
            return true;
        }

       return false;
    }
    
    private boolean isEligibleToStartTransaction(Exchange exchange) throws HandlerException {
        Transaction transaction = getCurrentTransaction();
        
        if (managedLocalRequired(exchange)) {
            return true;
        } else if (managedGlobalRequired(exchange)) {
            if (transaction == null) {
                return true;
            }
        }
        return false;
    }
    
    private void provideRequiredPolicies(Exchange exchange) {
        if (suspendsRequired(exchange)) {
            provideSuspends(exchange);
        } else if (propagatesRequired(exchange)) {
            providePropagates(exchange);
        }
        
        if (managedGlobalRequired(exchange)) {
            provideManagedGlobal(exchange);
        } else if (managedLocalRequired(exchange)) {
            provideManagedLocal(exchange);
        } else if (noManagedRequired(exchange)) {
            provideNoManaged(exchange);
        }
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
    
    private void providePropagates(Exchange exchange) {
        PolicyUtil.provide(exchange, TransactionPolicy.PROPAGATES_TRANSACTION);
    }
    
    private void provideSuspends(Exchange exchange) {
        PolicyUtil.provide(exchange, TransactionPolicy.SUSPENDS_TRANSACTION);
    }

    private void provideManagedGlobal(Exchange exchange) {
        PolicyUtil.provide(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL);
    }
    
    private void provideManagedLocal(Exchange exchange) {
        PolicyUtil.provide(exchange, TransactionPolicy.MANAGED_TRANSACTION_LOCAL);
    }
    
    private void provideNoManaged(Exchange exchange) {
        PolicyUtil.provide(exchange, TransactionPolicy.NO_MANAGED_TRANSACTION);
    }

    private void startTransaction(Exchange exchange) throws HandlerException {
        if (_log.isDebugEnabled()) {
            _log.debug("creating new transaction");
        }

        int txStatus = getCurrentTransactionStatus();

        if (txStatus == Status.STATUS_NO_TRANSACTION) {
            Transaction transaction = null;
            try {
                _transactionManager.begin();
                transaction = _transactionManager.getTransaction();
            } catch (Exception e) {
                throw RuntimeMessages.MESSAGES.failedCreateNewTransaction(e);
            }
            exchange.getContext().setProperty(INITIATED_TRANSACTION_PROPERTY, transaction, Scope.EXCHANGE).addLabels(BehaviorLabel.TRANSIENT.label());
        } else {
            throw RuntimeMessages.MESSAGES.transactionAlreadyExists();
        }
    }
    
    private void endTransaction() throws HandlerException {
        if (_log.isDebugEnabled()) {
            _log.debug("completing transaction");
        }
        
        int txStatus = getCurrentTransactionStatus();

        if (txStatus == Status.STATUS_MARKED_ROLLBACK) {
            try {
                _transactionManager.rollback();
            } catch (Exception e) {
                throw RuntimeMessages.MESSAGES.failedToRollbackTransaction(e);
            }
        } else if (txStatus == Status.STATUS_ACTIVE) {
            try {
                _transactionManager.commit();
            } catch (Exception e) {
                throw RuntimeMessages.MESSAGES.failedToCommitTransaction(e);
            }
        } else {
            throw RuntimeMessages.MESSAGES.failedToCompleteWithStatus(txStatus);
        }
    }

    private void suspendTransaction(Exchange exchange) {
        if (_log.isDebugEnabled()) {
            _log.debug("Suspending active transaction for exchange.");
        }

        Transaction transaction = null;
        try {
            transaction = _transactionManager.suspend();
        } catch (SystemException sysEx) {
            RuntimeLogger.ROOT_LOGGER.failedToSuspendTransactionOnExchange(sysEx);
        }
        if (transaction != null) {
            exchange.getContext().setProperty(SUSPENDED_TRANSACTION_PROPERTY, transaction, Scope.EXCHANGE).addLabels(BehaviorLabel.TRANSIENT.label());
        }
    }
    
    private void resumeTransaction(Transaction transaction) {
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("Resuming suspended transaction");
            }
            _transactionManager.resume(transaction);
        } catch (Exception ex) {
            RuntimeLogger.ROOT_LOGGER.failedToResumeTransaction(ex);
        }
    }

    private Transaction getCurrentTransaction() throws HandlerException {
        try {
            return _transactionManager.getTransaction();
        } catch (Exception e) {
            throw RuntimeMessages.MESSAGES.failedToRetrieveStatus(e);
        }
    }
    
    private int getCurrentTransactionStatus() throws HandlerException {
        try {
            return _transactionManager.getStatus();
        } catch (Exception e) {
            throw RuntimeMessages.MESSAGES.failedToRetrieveStatus(e);
        }
    }
}
