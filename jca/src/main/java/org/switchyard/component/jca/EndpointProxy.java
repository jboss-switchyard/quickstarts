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
package org.switchyard.component.jca;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;

import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;
import javax.resource.ResourceException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;

import org.apache.log4j.Logger;
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
    
    /**
     * Constructor.
     * 
     * @param factory {@link MessageEndpointFactory}
     * @param endpoint concrete subclass of {@link AbstractInflowEndpoint}
     * @param tm {@link TransactionManager}
     * @param xaResource {@link XAResource}
     * @param appLoader {@link ClassLoader} for this application
     */
    public EndpointProxy(MessageEndpointFactory factory,
                                AbstractInflowEndpoint endpoint,
                                TransactionManager tm,
                                XAResource xaResource,
                                ClassLoader appLoader) {
        _messageEndpointFactory = factory;
        _delegate = endpoint;
        _transactionManager = tm;
        _xaResource = xaResource;
        _appClassLoader = appLoader;
    }
    
    @Override
    public void beforeDelivery(Method method) throws NoSuchMethodException, ResourceException {
        if (_beforeDeliveryInvoked) {
           throw new IllegalStateException("Missing afterDelivery from the previous beforeDelivery for message endpoint " + _delegate);
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
           throw new IllegalStateException("afterDelivery without a previous beforeDelivery for message endpoint " + _delegate);
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
              _logger.warn("Error in release ", t);
           } finally {
               _beforeDeliveryInvoked = false;
               _waitAfterDeliveryInvoked = false;
           }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

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
            throw new IllegalStateException("Multiple message delivery between before and after delivery is not allowed for message endpoint " + delegate);
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
        Thread currentThread = Thread.currentThread();
        if (_inUseThread != null && !_inUseThread.equals(currentThread)) {
           throw new IllegalStateException("This message endpoint + " + _delegate + " is already in use by another thread " + _inUseThread);
        }
        _inUseThread = currentThread;
    }

    private void releaseThreadLock() {
        _inUseThread = null;
    }
    
    private void startTransaction(Method method) throws Exception {
        boolean endpointRequiresTx = _messageEndpointFactory.isDeliveryTransacted(method);
        boolean hasSourceManagedTx;
        int txStatus = _transactionManager.getStatus();
        switch (txStatus) {
        case Status.STATUS_ACTIVE:
            hasSourceManagedTx = true;
            break;
        case Status.STATUS_NO_TRANSACTION:
            hasSourceManagedTx = false;
            break;
        default:
            throw new IllegalStateException(method
                    + ": New transaction couldn't be started due to the status of existing transaction. Status code="
                    + txStatus + ". See javax.transaction.Status");
        }
        
        // JCA1.6 SPEC 13.5.9
        if (endpointRequiresTx && !hasSourceManagedTx) {
            _transactionManager.begin();
            _startedTx = _transactionManager.getTransaction();
            _startedTx.enlistResource(_xaResource);
        } else if (!endpointRequiresTx && hasSourceManagedTx) {
            _suspendedTx = _transactionManager.suspend();
        }
    }
    
    private void endTransaction(boolean commit) throws Exception {
        Transaction currentTx = null;
        try {
            // If we started the transaction, commit it
            if (_startedTx != null) {
                // Suspend any bad transaction - there is bug somewhere, but we will try to tidy things up
                currentTx = _transactionManager.getTransaction();
                if (currentTx == null || !currentTx.equals(_startedTx)) {
                    _logger.warn("Current transaction " + currentTx + " is not same as the " + _startedTx + " I have started. Replacing it.");
                    _transactionManager.suspend();
                    _transactionManager.resume(_startedTx);
                } else {
                    currentTx = null;
                }

                // Commit or rollback depending on the status
                if (!commit || _startedTx.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
                    _transactionManager.rollback();
                } else {
                    _transactionManager.commit();
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
                    _logger.warn("MessageEndpoint " + _delegate + " failed to resume old transaction " + currentTx);
                }
            }
        }
    }
        
}
