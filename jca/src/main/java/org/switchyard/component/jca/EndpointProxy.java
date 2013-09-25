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
package org.switchyard.component.jca;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;
import javax.resource.ResourceException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;

import org.jboss.logging.Logger;
import org.switchyard.component.jca.deploy.JCAInflowDeploymentMetaData;
import org.switchyard.component.jca.endpoint.AbstractInflowEndpoint;

/**
 * Message endpoint proxy to set up common facilities like transaction, concurrency control,
 * and ClassLoader before/after delegating to the concrete endpoint class.
 * 
 * The idea of this code originally came from org.jboss.soa.esb.listeners.jca.EndpointProxy.
 * 
 * @author <a href="bill@jboss.com">Bill Burke</a>
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class EndpointProxy implements InvocationHandler, MessageEndpoint {

    private Logger _logger = Logger.getLogger(EndpointProxy.class);

    private final MessageEndpointFactory _messageEndpointFactory;
    private final AbstractInflowEndpoint _delegate;
    private final TransactionManager _transactionManager;
    private final XAResource _xaResource;
    private final ClassLoader _appClassLoader;

    private Transaction _suspendedTx;
    private Transaction _startedTx;
    private boolean _waitAfterDeliveryInvoked = false;
    private Thread _inUseThread = null;
    private boolean _beforeDeliveryInvoked;
    private ClassLoader _origClassLoader;
    
    private boolean _useBatchCommit;
    private int _batchSize;
    private long _batchTimeout;
    private static ThreadLocal<BatchTransactionHelper> _batchHelper = new ThreadLocal<BatchTransactionHelper>();
        
    private ReentrantLock _deliveryThreadLock = new ReentrantLock();
    private ScheduledExecutorService _scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Constructor.
     * 
     * @param metadata {@link JCAInflowDeploymentMetaData}
     * @param factory {@link MessageEndpointFactory}
     * @param xaResource {@link XAResource}
     */
    public EndpointProxy(JCAInflowDeploymentMetaData metadata,MessageEndpointFactory factory, XAResource xaResource) {
        _messageEndpointFactory = factory;
        _delegate = metadata.getMessageEndpoint();
        _transactionManager = metadata.getTransactionManager();
        _xaResource = xaResource;
        _appClassLoader = metadata.getApplicationClassLoader();
        _useBatchCommit = metadata.useBatchCommit();
        _batchSize = metadata.getBatchSize();
        _batchTimeout = metadata.getBatchTimeout();
    }
    
    @Override
    public void beforeDelivery(Method method) throws NoSuchMethodException, ResourceException {
        if (_beforeDeliveryInvoked) {
           throw JCAMessages.MESSAGES.missingAfterDeliveryFromThePreviousBeforeDeliveryForMessageEndpoint(_delegate.toString());
        }
        _beforeDeliveryInvoked = true;

        try {
            before(method);
        } catch (Exception e) {
            throw new ResourceException(e);
        }
    }

    @Override
    public void afterDelivery() throws ResourceException {
        if (!_beforeDeliveryInvoked) {
            // SWITCHYARD-1640 Avoid remaining thread lock when RM invokes afterDelivery twice for the same delivery
            releaseThreadLock();
           throw JCAMessages.MESSAGES.afterDeliveryWithoutAPreviousBeforeDeliveryForMessageEndpoint(_delegate.toString());
        }

        try {
           finish(true);
        } catch (Throwable t) {
           throw new ResourceException(t);
        } finally {
            _beforeDeliveryInvoked = false;
            _waitAfterDeliveryInvoked = false;
        }
    }

    @Override
    public void release() {
        // JCA 1.6 spec 13.5 suggests the reuse of released proxy instance,
        // so we won't prohibit to reuse this instance after release() is called.
        
        if (_beforeDeliveryInvoked) {
           try {
              finish(false);
           } catch (Throwable t) {
              JCALogger.ROOT_LOGGER.errorInRelease(t);
           } finally {
               _beforeDeliveryInvoked = false;
               _waitAfterDeliveryInvoked = false;
           }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (_logger.isDebugEnabled()) {
            _logger.debug(Thread.currentThread().getName() + " is invoking " + method.getName() + " on " + this);
        }
        
        acquireThreadLock();

        // beforeDelivery, afterDelivery, or release
        if (method.getDeclaringClass().equals(MessageEndpoint.class)) {
            return method.invoke(this, args);
        }

        // actual delivery
        Object ret = null;
        try {
            if (!_beforeDeliveryInvoked) {
                before(method);
            }

            ret = delivery(_delegate, method, args);
        } catch (Throwable t) {
            if (!_beforeDeliveryInvoked) {
                finish(false);
            }
            throw t;
        }
        
        if (!_beforeDeliveryInvoked) {
            finish(true);
        }
        
        return ret;
    }

    private void before(Method method) throws Exception {
        switchToApplicationClassLoader();

        try {
            startTransaction(method);
        } catch (Exception e) {
            resetContextClassLoader();
           throw e;
        }
    }
    
    private Object delivery(Object delegate, Method method, Object[] args) throws Exception {
        if (_waitAfterDeliveryInvoked) {
            throw JCAMessages.MESSAGES.multipleMessageDeliveryBetweenBeforeAndAfterDeliveryIsNotAllowedForMessageEndpoint(delegate.toString());
        }

        if (_beforeDeliveryInvoked) {
            _waitAfterDeliveryInvoked = true;
        }
        
        return method.invoke(delegate, args);
    }
    
    private void finish(boolean commit) throws Exception {
        try {
            endTransaction(commit);
        } finally {
            resetContextClassLoader();
            releaseThreadLock();
        }
    }
    
    private void switchToApplicationClassLoader() {
        _origClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(_appClassLoader);
    }
    
    private void resetContextClassLoader() {
        if (_origClassLoader != null) {
            _inUseThread.setContextClassLoader(_origClassLoader);
            _origClassLoader = null;
        }
    }
    
    private void acquireThreadLock() {
        if (_deliveryThreadLock.isHeldByCurrentThread()) {
            return;
        }
        
        if (_inUseThread != null && !_inUseThread.equals(Thread.currentThread())) {
            throw JCAMessages.MESSAGES.threadCurrentThreadGetNameCouldnTAcquireAThreadLockSinceThisIsAlreadyInUseByAnotherThreadInUseThreadGetName(Thread.currentThread().getName(), this.toString(), _inUseThread.getName());
        }
        _deliveryThreadLock.lock();
        _inUseThread = Thread.currentThread();
        
        if (_logger.isDebugEnabled()) {
            _logger.debug(Thread.currentThread().getName() + " acquired thread lock on " + this);
        }
    }

    private void releaseThreadLock() {
        _inUseThread = null;
        _deliveryThreadLock.unlock();

        if (_logger.isDebugEnabled()) {
            _logger.debug(Thread.currentThread().getName() + " released thread lock on " + this);
        }
    }
    
    private void startTransaction(Method method) throws Exception {
        boolean endpointRequiresTx = _messageEndpointFactory.isDeliveryTransacted(method);
        boolean hasSourceManagedTx;
        
        if (_logger.isDebugEnabled()) {
            _logger.debug(Thread.currentThread().getName() + " is invoking startTransaction: currentTx=" + _transactionManager.getTransaction()
                + ", _startedTx=" + _startedTx);
        }
        
        if (_useBatchCommit) {
            BatchTransactionHelper helper = _batchHelper.get();
            if (helper != null && helper.isTransactionActive()) {
                // under batch processing ... just continue with existing transaction
                _startedTx = helper.getAssociatedTransaction();
                return;
            }
        }
        
        int txStatus = _transactionManager.getStatus();
        switch (txStatus) {
        case Status.STATUS_ACTIVE:
            hasSourceManagedTx = true;
            break;
        case Status.STATUS_NO_TRANSACTION:
            hasSourceManagedTx = false;
            break;
        case Status.STATUS_COMMITTED:
            hasSourceManagedTx = false;
            // possibly the one which has been commited by reaper thread. try to disassociate...
            _transactionManager.suspend();
            break;
        default:
            throw JCAMessages.MESSAGES.methodNewTransactionCouldnTBeStartedDueToTheStatusOfExistingTransactionStatusCodeTxStatusSeeJavaxTransactionStatus();
        }
        
        if (hasSourceManagedTx && _useBatchCommit) {
            throw JCAMessages.MESSAGES.batchCommitModeCannotBeUsedWithSourceManagedTransactionPleaseTurnOffTheBatchCommit();
        }

        // JCA1.6 SPEC 13.5.9
        if (endpointRequiresTx && !hasSourceManagedTx) {
            _transactionManager.begin();
            _startedTx = _transactionManager.getTransaction();
            _startedTx.enlistResource(_xaResource);
            if (_useBatchCommit) {
                BatchTransactionHelper helper = new BatchTransactionHelper(_startedTx);
                helper.scheduleReaperThread(_scheduler, _batchTimeout, TimeUnit.MILLISECONDS);
                _batchHelper.set(helper);
            }
        } else if (!endpointRequiresTx && hasSourceManagedTx) {
            _suspendedTx = _transactionManager.suspend();
        }
    }
    
    private void endTransaction(boolean commit) throws Exception {
        Transaction currentTx = null;
        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug(Thread.currentThread().getName() + " is invoking endTransaction: currentTx=" + _transactionManager.getTransaction()
                    + ", _startedTx=" + _startedTx);
            }
            
            // If we started the transaction, commit it
            if (_startedTx != null) {
                // Suspend any bad transaction - there is bug somewhere, but we will try to tidy things up
                currentTx = _transactionManager.getTransaction();
                if (currentTx == null || !currentTx.equals(_startedTx)) {
                    JCALogger.ROOT_LOGGER.currentTransactionIsNotSameAsThe(currentTx, _startedTx.toString());
                    _transactionManager.suspend();
                    _transactionManager.resume(_startedTx);
                } else {
                    currentTx = null;
                }

                BatchTransactionHelper helper = _batchHelper.get();
                // Commit or rollback depending on the status
                if (!commit || _startedTx.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
                    _transactionManager.rollback();
                    if (_useBatchCommit) {
                        helper.cancelScheduledReaperThread();
                    }
                } else {
                    if (_useBatchCommit) {
                        if (helper.getCounter() + 1 < _batchSize) {
                            // keep the transaction active for next message
                            helper.setCounter(helper.getCounter()+1);
                        } else {
                            _transactionManager.commit();
                            helper.cancelScheduledReaperThread();
                        }
                        _startedTx = null;
                        return;
                    } else {
                        _transactionManager.commit();
                    }
                }
                _startedTx = null;
            }

            // If we suspended the incoming transaction, resume it
            if (_suspendedTx != null) {
                try {
                    _transactionManager.resume(_suspendedTx);
                } finally {
                    _suspendedTx = null;
                }
            }
        } finally {
            // Resume any suspended transaction
            if (currentTx != null) {
                try {
                    _transactionManager.resume(currentTx);
                } catch (Throwable t) {
                    JCALogger.ROOT_LOGGER.messageEndpointFailedToResumeOldTransaction(_delegate.toString(), currentTx.toString());
                }
            }
        }
    }
        
    private class BatchTransactionHelper extends Thread {
        private Transaction _transaction;
        private int _counter = 0;
        private ScheduledFuture<?> _future;
        
        public BatchTransactionHelper(Transaction tx) {
            _transaction = tx;
        }
        
        public void setCounter(int counter) {
            _counter = counter;
        }
        
        public int getCounter() {
            return _counter;
        }
        
        public boolean isTransactionActive() {
            try {
                return _transaction.getStatus() == Status.STATUS_ACTIVE;
            } catch (Exception e) {
                JCALogger.ROOT_LOGGER.failedToRetrieveTransactionStatus(e);
                return false;
            }
        }
        
        public Transaction getAssociatedTransaction() {
            return _transaction;
        }
        
        public void scheduleReaperThread(ScheduledExecutorService service, long delay, TimeUnit unit) {
            _future = service.schedule(this, delay, unit);
        }
        
        public void cancelScheduledReaperThread() {
            if (_future != null) {
                _future.cancel(true);
            }
        }
        
         public void run() {
             _deliveryThreadLock.lock();
             try {
                 if (_transaction.getStatus() == Status.STATUS_ACTIVE) {
                     _transactionManager.resume(_transaction);
                     _transactionManager.commit();
                     JCALogger.ROOT_LOGGER.transactionHasBeenCommittedByReaperThread(_counter);
                     _counter = 0;
                 }
             } catch (Exception e) {
                     JCALogger.ROOT_LOGGER.failedToCommitExpiringTransaction(e);
             } finally {
                 _deliveryThreadLock.unlock();
             }
         }
    }
}
