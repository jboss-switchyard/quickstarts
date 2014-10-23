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
package org.switchyard.component.sca;

import javax.transaction.Transaction;
import javax.xml.namespace.QName;

import org.jboss.jbossts.txbridge.outbound.OutboundBridge;
import org.jboss.jbossts.txbridge.outbound.OutboundBridgeManager;
import org.jboss.logging.Logger;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContextType;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.ServiceReference;
import org.switchyard.SwitchYardException;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.SynchronousInOutHandler;
import org.switchyard.config.model.composite.SCABindingModel;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.deploy.internal.Deployment;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.RemoteRegistry;
import org.switchyard.remote.cluster.ClusteredInvoker;
import org.switchyard.remote.cluster.LoadBalanceStrategy;
import org.switchyard.remote.cluster.RandomStrategy;
import org.switchyard.remote.cluster.RoundRobinStrategy;
import org.switchyard.remote.http.HttpInvokerLabel;
import org.switchyard.runtime.event.ExchangeCompletionEvent;

import com.arjuna.mw.wst11.TransactionManagerFactory;
import com.arjuna.mwlabs.wst11.at.context.TxContextImple;

/**
 * Handles outbound communication to an SCA service endpoint.
 */
public class SCAInvoker extends BaseServiceHandler {
    
    private static Logger _log = Logger.getLogger(SCAInvoker.class);
    
    private final String _bindingName;
    private final String _referenceName;
    private final String _targetService;
    private final String _targetNamespace;
    private final boolean _clustered;
    private ClusteredInvoker _invoker;
    private boolean _preferLocal;
    private boolean _disableRemoteTransaction = false;
    private TransactionContextSerializer _txSerializer = new TransactionContextSerializer();
    
    /**
     * Create a new SCAInvoker for invoking local endpoints.
     * @param config binding configuration model
     */
    public SCAInvoker(SCABindingModel config) {
        _bindingName = config.getName();
        _referenceName = config.getReference().getName();
        _targetService = config.getTarget();
        _targetNamespace = config.getTargetNamespace();
        _clustered = config.isClustered();
        _preferLocal = config.isPreferLocal();
    }
    
    /**
     * Create a new SCAInvoker capable of invoking remote service endpoints.
     * @param config binding configuration model
     * @param registry registry of remote services
     */
    public SCAInvoker(SCABindingModel config, RemoteRegistry registry) {
        this(config);
        if (config.isLoadBalanced()) {
            LoadBalanceStrategy loadBalancer = createLoadBalancer(config.getLoadBalance());
            _invoker = new ClusteredInvoker(registry, loadBalancer);
        } else {
            _invoker = new ClusteredInvoker(registry);
        }
    }
    
    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        // identify ourselves
        exchange.getContext().setProperty(ExchangeCompletionEvent.GATEWAY_NAME, _bindingName, Scope.EXCHANGE)
                .addLabels(BehaviorLabel.TRANSIENT.label());

        if (getState() != State.STARTED) {
            throw SCAMessages.MESSAGES.referenceBindingNotStarted(_referenceName, _bindingName);
        }
        try {
            // Figure out the QName for the service were invoking
            QName serviceName = getTargetServiceName(exchange);
            // Get a handle for the reference and use a copy of the exchange to invoke it
            ServiceReference ref = exchange.getProvider().getDomain().getServiceReference(serviceName);
            
            if (_clustered) {
                // check to see if local is preferred and available
                if (_preferLocal && ref != null) {
                    invokeLocal(exchange, ref);
                } else {
                    invokeRemote(exchange, serviceName);
                }
            } else {
                if (ref == null) {
                    throw SCAMessages.MESSAGES.serviceReferenceNotFoundInDomain(serviceName.toString(), exchange.getProvider().getDomain().getName().toString());
                }
                invokeLocal(exchange, ref);
            }
        } catch (SwitchYardException syEx) {
            throw new HandlerException(syEx.getMessage());
        }
    }
    
    /**
     * Set if remote transaction bridging should be disabled.
     * @param disable true if it disables remote transaction
     * @return this SCAInvoker instance (useful for method chaining)
     */
    public SCAInvoker setDisableRemoteTransaction(boolean disable) {
        _disableRemoteTransaction = disable;
        return this;
    }
    
    // This method exists for test purposes and should not be used at runtime.  Initialization
    // of the invoker instance occurs in the constructor for SCAInvoker.
    void setInvoker(ClusteredInvoker invoker) {
        _invoker = invoker;
    }
    
    private void invokeLocal(Exchange exchange, ServiceReference targetRef) throws HandlerException {
        SynchronousInOutHandler replyHandler = new SynchronousInOutHandler();
        Exchange ex = targetRef.createExchange(exchange.getContract().getProviderOperation().getName(), replyHandler);
        
        // Can't send same message twice, so make a copy
        Message invokeMsg = exchange.getMessage().copy();
        exchange.getContext().mergeInto(invokeMsg.getContext());
        
        // Since this invocation may cross application boundaries, we need to set the TCCL 
        // based on the target service's application class loader
        ClassLoader origCL = null;
        try {
            ClassLoader targetCL = (ClassLoader) 
                    targetRef.getDomain().getProperty(Deployment.CLASSLOADER_PROPERTY);
            origCL = Classes.setTCCL(targetCL);
            ex.send(invokeMsg);
        } finally {
            if (origCL != null) {
                Classes.setTCCL(origCL);
            }
        }
        
        if (ExchangePattern.IN_OUT.equals(ex.getPattern())) {
            replyHandler.waitForOut();
            if (ex.getMessage() != null) {
                Message replyMsg = ex.getMessage().copy();
                ex.getContext().mergeInto(replyMsg.getContext());
                if (ExchangeState.FAULT.equals(ex.getState())) {
                    exchange.sendFault(replyMsg);
                } else {
                    exchange.send(replyMsg);
                }
            }
        } else if (ExchangeState.FAULT.equals(ex.getState())) {
            // Even though this is in-only, we need to report a runtime fault on send
            throw createHandlerException(ex.getMessage());
        }
    }
    
    private void invokeRemote(Exchange exchange, QName serviceName) throws HandlerException {
        
        RemoteMessage request = new RemoteMessage()
            .setDomain(exchange.getProvider().getDomain().getName())
            .setService(serviceName)
            .setOperation(exchange.getContract().getConsumerOperation().getName())
            .setContent(exchange.getMessage().getContent());
        exchange.getContext().mergeInto(request.getContext());
        boolean transactionPropagated = bridgeOutgoingTransaction(request);

        try {
            RemoteMessage reply = _invoker.invoke(request);
            if (transactionPropagated) {
                bridgeIncomingTransaction();
            }
            if (reply == null) {
                return;
            }
            
            if (ExchangePattern.IN_OUT.equals(exchange.getPattern())) {
                Message msg = exchange.createMessage();
                msg.setContent(reply.getContent());
                Context replyCtx = reply.getContext();
                if (replyCtx != null) {
                    replyCtx.mergeInto(exchange.getContext());
                }
                if (reply.isFault()) {
                    exchange.sendFault(msg);
                } else {
                    exchange.send(msg);
                }
            } else {
                // still need to account for runtime exceptions on in-only
                if (reply.isFault()) {
                    throw createHandlerException(reply.getContent());
                }
            }
        } catch (java.io.IOException ioEx) {
            ioEx.printStackTrace();
            exchange.sendFault(exchange.createMessage().setContent(ioEx));
        }
    }
    
    private QName getTargetServiceName(Exchange exchange) {
        // Figure out the QName for the service were invoking
        QName service = exchange.getProvider().getName();
        String targetName = _targetService != null ? _targetService : service.getLocalPart();
        String targetNS = _targetNamespace != null ? _targetNamespace : service.getNamespaceURI();
        return new QName(targetNS, targetName);
    }
    
    private boolean bridgeOutgoingTransaction(RemoteMessage request) throws HandlerException {
        if (_disableRemoteTransaction) {
            return false;
        }
        
        Transaction currentTransaction = null;
        try {
            currentTransaction = com.arjuna.ats.jta.TransactionManager.transactionManager().getTransaction();
        } catch (Throwable t) {
            if (_log.isDebugEnabled()) {
                _log.debug(t);
            }
        }
        if (currentTransaction == null) {
            return false;
        }
        
        try {
            // create/resume subordinate WS-AT transaction
            OutboundBridge txOutboundBridge = OutboundBridgeManager.getOutboundBridge();
            if (txOutboundBridge == null) {
                return false;
            }
            txOutboundBridge.start();
            
            // embed WS-AT transaction context into request header 
            final com.arjuna.mw.wst11.TransactionManager wsatManager = TransactionManagerFactory.transactionManager();
            CoordinationContextType coordinationContext = null;
            if (wsatManager != null) {
                final TxContextImple txContext = (TxContextImple)wsatManager.currentTransaction();
                if (txContext != null) {
                    coordinationContext = txContext.context().getCoordinationContext();
                }
            }

            if (coordinationContext != null) {
                String txContextString = _txSerializer.serialise(coordinationContext);
                if (_log.isDebugEnabled()) {
                    _log.debug("Embedding transaction context into request header: " + txContextString);
                }
                request.getContext()
                       .setProperty(TransactionContextSerializer.HEADER_TXCONTEXT, txContextString)
                       .addLabels(BehaviorLabel.TRANSIENT.label(), HttpInvokerLabel.HEADER.label());
            }
            return true;
        } catch (final Throwable th) {
            throw createHandlerException(th);
        }
    }
    
    private void bridgeIncomingTransaction() throws HandlerException {
        // disassociate subordinate WS-AT transaction
        OutboundBridge txOutboundBridge = OutboundBridgeManager.getOutboundBridge();
        if (txOutboundBridge != null) {
            try {
                txOutboundBridge.stop();
            } catch (Exception e) {
                throw createHandlerException(e);
            }
        }
    }
    
    private HandlerException createHandlerException(Message message) {
        return createHandlerException(message == null ? null : message.getContent());
    }
    
    private HandlerException createHandlerException(Object content) {
        HandlerException ex;
        if (content == null) {
            ex = SCAMessages.MESSAGES.runtimeFaultOccurredWithoutExceptionDetails();
        } else if (content instanceof HandlerException) {
            ex = (HandlerException)content;
        } else if (content instanceof Throwable) {
            ex = new HandlerException((Throwable)content);
        } else {
            ex = new HandlerException(content.toString());
        }
        return ex;
    }
    
    
    LoadBalanceStrategy createLoadBalancer(String strategy) {
        if (RoundRobinStrategy.class.getSimpleName().equals(strategy)) {
            return new RoundRobinStrategy();
        } else if (RandomStrategy.class.getSimpleName().equals(strategy)) {
            return new RandomStrategy();
        } else {
            try {
                Class<?> strategyClass = Class.forName(strategy);
                if (!LoadBalanceStrategy.class.isAssignableFrom(strategyClass)) {
                    throw SCAMessages.MESSAGES.loadBalanceClassDoesNotImplementLoadBalanceStrategy(strategy);
                }
                return (LoadBalanceStrategy)strategyClass.newInstance();
            } catch (Exception ex) {
                throw SCAMessages.MESSAGES.unableToInstantiateStrategyClass(strategy, ex);
            }
        }
    }
}
