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
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePattern;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.policy.ExchangePolicy;
import org.switchyard.policy.TransactionPolicy;


/**
 * Interprets transactional policy specified on an exchange and handles 
 * transactional requirements.
 */
public class TransactionHandler implements ExchangeHandler {
    
    private static final String JNDI_TRANSACTION_MANAGER = "java:jboss/TransactionManager";
    private static final String TRANSACTION_PROPERTY = 
            "org.switchyard.exchange.transaction";
    
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
            _log.info("Unable to find TransactionManager in JNDI at " + JNDI_TRANSACTION_MANAGER 
                    + " - Transaction Policy handling will not be avaialble.");
        }
    }
    
    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        // if no TM is available, there's nothing to do
        if (_transactionManager == null) {
            return;
        }
        
        // execute policy behavior based on exchange phase
        if (ExchangePhase.OUT.equals(exchange.getPhase())) {
            handleOut(exchange);
        } else {
            handleIn(exchange);
        }
    }
    
    @Override
    public void handleFault(Exchange exchange) {
        handleOut(exchange);
    }
    
    void setTransactionManager(TransactionManager transactionManager) {
        _transactionManager = transactionManager;
    }
    
    TransactionManager getTransactionManager() {
        return _transactionManager;
    }
    
    private void handleOut(Exchange exchange) {
        if (!suspendRequired(exchange)) {
            // nothing to do here
            return;
        }
        
        // need to resume the suspended transaction
        Transaction transaction = (Transaction)
                exchange.getContext().getPropertyValue(TRANSACTION_PROPERTY);
        if (transaction != null) {
            resumeTransaction(transaction);
        }
    }
    
    private void handleIn(Exchange exchange) throws HandlerException {
        // check for incompatible policy definition 
        if (suspendRequired(exchange) && propagateRequired(exchange)) {
            throw new HandlerException("Invalid transaction policy : "
                + TransactionPolicy.SUSPEND + " and " + TransactionPolicy.PROPAGATE
                + " cannot be requested simultaneously.");
        }
        
        // are we expecting a transaction to be provided?
        if (propagateRequired(exchange) && !propagateProvided(exchange)) {
            throw new HandlerException(
                    "Transaction policy requires an active transaction, but no transaction is present.");
        }
        
        if (suspendRequired(exchange)) {
            Transaction transaction = (Transaction)
                    exchange.getContext().getPropertyValue(TRANSACTION_PROPERTY);
            
            if (transaction == null) {
                // before invocation - check for active transaction
                if (_log.isDebugEnabled()) {
                    _log.debug("Suspending active transaction for exchange.");
                }
                try {
                    transaction = _transactionManager.suspend();
                } catch (SystemException sysEx) {
                    throw new HandlerException("Failed to suspend transaction on exchange.", sysEx);
                }
                if (transaction != null) {
                    exchange.getContext().setProperty(TRANSACTION_PROPERTY, transaction);
                }
                // if an active transaction was present, it has been suspended -
                // mark the policy requirement as provided.
                ExchangePolicy.provide(exchange, TransactionPolicy.SUSPEND);
            } else {
                // after invocation - check to see if we need to resume transaction
                if (isInOnly(exchange)) {
                    // we need to resume transactions on InOnly exchanges as part
                    // of the 'in' phase handlers because there is no out phase
                    resumeTransaction(transaction);
                }
            }
        }
    }
    
    private boolean propagateProvided(Exchange exchange) {
        return ExchangePolicy.isProvided(exchange, TransactionPolicy.PROPAGATE);
    }
    
    private boolean suspendRequired(Exchange exchange) {
        return ExchangePolicy.isRequired(exchange, TransactionPolicy.SUSPEND);
    }
    
    private boolean propagateRequired(Exchange exchange) {
        return ExchangePolicy.isRequired(exchange, TransactionPolicy.PROPAGATE);
    }
    
    private boolean isInOnly(Exchange exchange) {
        return ExchangePattern.IN_ONLY.equals(
                exchange.getContract().getServiceOperation().getExchangePattern());
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
