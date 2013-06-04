/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.sca;

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.ServiceReference;
import org.switchyard.SynchronousInOutHandler;
import org.switchyard.config.model.composite.SCABindingModel;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.RemoteRegistry;
import org.switchyard.remote.cluster.ClusteredInvoker;
import org.switchyard.remote.cluster.LoadBalanceStrategy;
import org.switchyard.remote.cluster.RandomStrategy;
import org.switchyard.remote.cluster.RoundRobinStrategy;

/**
 * Handles outbound communication to an SCA service endpoint.
 */
public class SCAInvoker extends BaseServiceHandler {
    
    private SCABindingModel _config;
    private ClusteredInvoker _invoker;
    
    /**
     * Create a new SCAInvoker for invoking local endpoints.
     * @param config binding configuration model
     */
    public SCAInvoker(SCABindingModel config) {
        _config = config;
    }
    
    /**
     * Create a new SCAInvoker capable of invoking remote service endpoints.
     * @param config binding configuration model
     * @param registry registry of remote services
     */
    public SCAInvoker(SCABindingModel config, RemoteRegistry registry) {
        _config = config;
        if (config.isLoadBalanced()) {
            LoadBalanceStrategy loadBalancer = createLoadBalancer(config.getLoadBalance());
            _invoker = new ClusteredInvoker(registry, loadBalancer);
        } else {
            _invoker = new ClusteredInvoker(registry);
        }
    }
    
    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        try {
            if (_config.isClustered()) {
                invokeRemote(exchange);
            } else {
                invokeLocal(exchange);
            }
        } catch (SwitchYardException syEx) {
            throw new HandlerException(syEx.getMessage());
        }
    }
    
    private void invokeLocal(Exchange exchange) throws HandlerException {
        // Figure out the QName for the service were invoking
        QName serviceName = getTargetServiceName(exchange);
        // Get a handle for the reference and use a copy of the exchange to invoke it
        ServiceReference ref = exchange.getProvider().getDomain().getServiceReference(serviceName);
        if (ref == null) {
            throw new HandlerException("Service reference " + serviceName + " not found in domain " + exchange.getProvider().getDomain().getName());
        }
        SynchronousInOutHandler replyHandler = new SynchronousInOutHandler();
        Exchange ex = ref.createExchange(exchange.getContract().getProviderOperation().getName(), replyHandler);
        ex.send(exchange.getMessage());
        if (isInOut(ex)) {
            replyHandler.waitForOut();
            Message msg = ex.getMessage();
            if (ExchangeState.FAULT.equals(ex.getState())) {
                exchange.sendFault(msg);
            } else {
                exchange.send(msg);
            }
            
        }
    }
    
    private void invokeRemote(Exchange exchange) {
        // Figure out the QName for the service were invoking
        QName serviceName = getTargetServiceName(exchange);

        RemoteMessage request = new RemoteMessage()
            .setDomain(exchange.getProvider().getDomain().getName())
            .setService(serviceName)
            .setContent(exchange.getMessage().getContent());
        request.setContext(exchange.getContext());

        try {
            RemoteMessage reply = _invoker.invoke(request);
            if (isInOut(exchange) && reply != null) {
                Message msg = exchange.getMessage().setContent(reply.getContent());
                if (reply.isFault()) {
                    exchange.sendFault(msg);
                } else {
                    exchange.send(msg);
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
        String targetName = _config.hasTarget() ? _config.getTarget() : service.getLocalPart();
        String targetNS = _config.hasTargetNamespace() ? _config.getTargetNamespace() : service.getNamespaceURI();
        return new QName(targetNS, targetName);
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
                    throw new SwitchYardException("Load balance class does not implement LoadBalanceStrategy: " + strategy);
                }
                return (LoadBalanceStrategy)strategyClass.newInstance();
            } catch (Exception ex) {
                throw new SwitchYardException("Unable to instantiate strategy class: " + strategy, ex);
            }
        }
    }
    
    private boolean isInOut(Exchange exchange) {
        return ExchangePattern.IN_OUT.equals(
                exchange.getContract().getConsumerOperation().getExchangePattern());
    }
}
