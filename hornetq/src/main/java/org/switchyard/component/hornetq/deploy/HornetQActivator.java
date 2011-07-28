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
package org.switchyard.component.hornetq.deploy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.hornetq.api.core.DiscoveryGroupConfiguration;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ServerLocator;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceReference;
import org.switchyard.component.hornetq.ServerLocatorBuilder;
import org.switchyard.component.hornetq.config.model.HornetQBindingModel;
import org.switchyard.component.hornetq.config.model.HornetQConfigModel;
import org.switchyard.component.hornetq.config.model.HornetQConnectorConfigModel;
import org.switchyard.component.hornetq.config.model.HornetQDiscoveryGroupConfigModel;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.domain.PropertiesModel;
import org.switchyard.config.model.domain.PropertyModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.exception.SwitchYardException;

/**
 * Activates HornetQ bindings in SwitchYard.
 * 
 * @author Daniel Bevenius
 *
 */
public class HornetQActivator extends BaseActivator {
    
    private Map<QName, Set<InboundHandler>> _bindings = new HashMap<QName, Set<InboundHandler>>();
    private Map<QName, Set<OutboundHandler>> _refBindings = new HashMap<QName, Set<OutboundHandler>>();

    /**
     * No-args constructor to support service activation.
     */
    public HornetQActivator() {
        super("hornetq");
    }

    @Override
    public ExchangeHandler init(final QName serviceName, final Model config) {
        if (isServiceBinding(config)) {
            return handleServiceBindings((CompositeServiceModel) config, serviceName);
        }
        if (isReferenceBinding(config)) {
            return handleReferenceBinding(config, serviceName);
        }
        
        throw new SwitchYardException("No HornetQ bindings, references, or implementations found for [" 
                + serviceName + "] in config [" + config + "]");
    }
    
    private boolean isReferenceBinding(final Model config) {
        return config instanceof CompositeReferenceModel;
    }
    
    private boolean isServiceBinding(final Model config) {
        return config instanceof CompositeServiceModel;
    }
    
    private ExchangeHandler handleServiceBindings(final CompositeServiceModel serviceModel, final QName serviceName) {
        final List<BindingModel> bindings = serviceModel.getBindings();
        for (final BindingModel bindingModel : bindings) {
            if (bindingModel instanceof HornetQBindingModel) {
                final HornetQBindingModel hbm = (HornetQBindingModel) bindingModel;
                final ServerLocator serverLocator = buildServerLocator(hbm.getHornetQConfig());
                final InboundHandler inboundHandler = new InboundHandler(hbm, serverLocator);
                final Set<InboundHandler> inboundHandlers = getInboundHandlersForService(serviceName);
                if (!inboundHandlers.contains(inboundHandler)) {
                    inboundHandlers.add(inboundHandler);
                    _bindings.put(serviceName, inboundHandlers);
                    return inboundHandler;
                }
            }
        }
        return null;
    }
    
    private ExchangeHandler handleReferenceBinding(final Model config, final QName serviceName) {
        final CompositeReferenceModel refModel = (CompositeReferenceModel) config;
        final List<BindingModel> bindings = refModel.getBindings();
        for (BindingModel bindingModel : bindings) {
            if (bindingModel instanceof HornetQBindingModel) {
                final HornetQBindingModel hbm = (HornetQBindingModel) bindingModel;
                final ServerLocator serverLocator = buildServerLocator(hbm.getHornetQConfig());
                final OutboundHandler outboundHandler = new OutboundHandler(hbm, serverLocator);
                final Set<OutboundHandler> outboundHandlersForService = getOutboundHandlersForService(serviceName);
                if (!outboundHandlersForService.contains(outboundHandler)) {
                    outboundHandlersForService.add(outboundHandler);
                    _refBindings.put(serviceName, outboundHandlersForService);
                    return outboundHandler;
                }
            }
        }
        return null;
    }
    
    private Set<InboundHandler> getInboundHandlersForService(final QName serviceName) {
        Set<InboundHandler> handlers = _bindings.get(serviceName);
        if (handlers == null) {
            handlers = new HashSet<InboundHandler>();
        }
        return handlers;
    }
    
    private Set<OutboundHandler> getOutboundHandlersForService(final QName serviceName) {
        Set<OutboundHandler> handlers = _refBindings.get(serviceName);
        if (handlers == null) {
            handlers = new HashSet<OutboundHandler>();
        }
        return handlers;
    }
    
    private ServerLocator buildServerLocator(final HornetQConfigModel configModel) {
        final ServerLocatorBuilder slb = new ServerLocatorBuilder();
        slb.ackBatchSize(configModel.getAckBatchSize());
        slb.autoGroup(configModel.isAutoGroup());
        slb.blockOnAcknowledge(configModel.isBlockOnAcknowledge());
        slb.blockOnDurableSend(configModel.isBlockOnDurableSend());
        slb.blockOnNonDurableSend(configModel.isBlockOnNonDurableSend());
        slb.cacheLargeMessagesOnConsumers(configModel.isCacheLargeMessagesClient());
        slb.callTimeout(configModel.getCallTimeout());
        slb.clientFailureCheckPeriod(configModel.getClientFailureCheckPeriod());
        slb.compressLargeMessage(configModel.isCompressLargeMessage());
        slb.confirmationWindowSize(configModel.getConfirmationWindowSize());
        slb.connectionLoadBalancingPolicyClassName(configModel.getConnectionLoadBalancingPolicyClassName());
        slb.connectionTTL(configModel.getConnectionTTL());
        slb.consumerMaxRate(configModel.getConsumerMaxRate());
        slb.consumerWindowSize(configModel.getConsumerWindowSize());
        slb.disableFinalizedCheck(configModel.isDisableFinalizeCheck());
        slb.discoveryGroupConfiguration(getDiscoveryGroupConfiguration(configModel.getDiscoveryGroup()));
        slb.failoverOnInitialConnection(configModel.isFailoverOnInitialConnection());
        slb.groupID(configModel.getGroupID());
        slb.initialMessagePacketSize(configModel.getInitialMessagePacketSize());
        slb.initialReconnectAttempts(configModel.getInitialConnectAttempts());
        slb.maxRetryInterval(configModel.getMaxRetryInterval());
        slb.minLargeMessageSize(configModel.getMinLargeMessageSize());
        slb.preAcknowledge(configModel.isPreAcknowledge());
        slb.producerMaxRate(configModel.getProducerMaxRate());
        slb.producerWindowSize(configModel.getProducerWindowSize());
        slb.reconnectAttempts(configModel.getReconnectAttempts());
        slb.retryInterval(configModel.getRetryInterval());
        slb.retryIntervalMultiplier(configModel.getRetryIntervalMultiplier());
        slb.scheduledThreadPoolMaxSize(configModel.getScheduledThreadPoolMaxSize());
        slb.threadPoolMaxSize(configModel.getThreadPoolMaxSize());
        slb.transportConfigurations(getTransportConfiguration(configModel.getConnectorConfiguration()));
        slb.useGlobalPools(configModel.isUseGlobalPools());
        slb.useHA(configModel.isUseHA());
        return slb.build();
    }
    
    private TransportConfiguration getTransportConfiguration(final HornetQConnectorConfigModel config) {
        if (config == null) {
            return null;
        }
        
        final PropertiesModel propertiesModel = config.getProperties();
        final Map<String, Object> connectorProperties = new HashMap<String, Object>();
        if (propertiesModel != null) {
            for (PropertyModel propertyModel : propertiesModel.getProperties()) {
                connectorProperties.put(propertyModel.getName(), propertyModel.getValue());
                }
        }
        return new TransportConfiguration(config.getConnectorClassName(), connectorProperties);
    }

    private DiscoveryGroupConfiguration getDiscoveryGroupConfiguration(final HornetQDiscoveryGroupConfigModel config) {
        if (config == null) {
            return null;
        }
        final DiscoveryGroupConfiguration discoveryGroupConfiguration = new DiscoveryGroupConfiguration(
                config.getGroupAddress(),
                config.getGroupPort());
        
        final String localBindAddress = config.getLocalBindAddress();
        if (localBindAddress != null) {
            discoveryGroupConfiguration.setLocalBindAdress(localBindAddress);
        }
        
        final Long refreshTimeout = config.getRefreshTimeout();
        if (refreshTimeout != null) {
            discoveryGroupConfiguration.setRefreshTimeout(refreshTimeout);
        }
        
        final Long initialWaitTimeout = config.getInitialWaitTimeout();
        if (initialWaitTimeout != null) {
            discoveryGroupConfiguration.setDiscoveryInitialWaitTimeout(initialWaitTimeout);
        }
        
        return discoveryGroupConfiguration;
    }

    @Override
    public void start(final ServiceReference serviceReference) {
        final Set<InboundHandler> handlers = _bindings.get(serviceReference.getName());
        if (handlers != null) {
            for (InboundHandler inboundHandler : handlers) {
                try {
                    inboundHandler.start(serviceReference);
                } catch (Exception e) {
                    throw new SwitchYardException(e);
                }
            }
        }
        final Set<OutboundHandler> outboundHandlers = _refBindings.get(serviceReference.getName());
        if (outboundHandlers != null) {
            for (OutboundHandler outboundHandler : outboundHandlers) {
                outboundHandler.start();
            }
        }
    }

    @Override
    public void stop(final ServiceReference serviceReference) {
        final Set<InboundHandler> handlers = _bindings.get(serviceReference.getName());
        if (handlers != null) {
            for (InboundHandler inboundHandler : handlers) {
                try {
                    inboundHandler.stop(serviceReference);
                } catch (Exception e) {
                    throw new SwitchYardException(e);
                }
            }
        }
        final Set<OutboundHandler> outboundHandlers = _refBindings.get(serviceReference.getName());
        if (outboundHandlers != null) {
            for (OutboundHandler outboundHandler : outboundHandlers) {
                outboundHandler.stop();
            }
        }
    }

    @Override
    public void destroy(final ServiceReference service) {
    }

}
