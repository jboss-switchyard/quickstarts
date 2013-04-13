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
import java.util.Map;

import javax.xml.namespace.QName;

import org.hornetq.api.core.DiscoveryGroupConfiguration;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.UDPBroadcastGroupConfiguration;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.api.core.client.ServerLocator;
import org.switchyard.component.hornetq.ServerLocatorBuilder;
import org.switchyard.component.hornetq.config.model.HornetQBindingModel;
import org.switchyard.component.hornetq.config.model.HornetQConfigModel;
import org.switchyard.component.hornetq.config.model.HornetQConnectorConfigModel;
import org.switchyard.component.hornetq.config.model.HornetQDiscoveryGroupConfigModel;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.property.PropertiesModel;
import org.switchyard.config.model.property.PropertyModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.ServiceHandler;

/**
 * Activates HornetQ bindings in SwitchYard.
 * 
 * @author Daniel Bevenius
 *
 */
public class HornetQActivator extends BaseActivator {
    
    /**
     * HornetQ component activator type name.
     */
    public static final String HORNETQ_TYPE = "hornetq";
    
    /**
     * No-args constructor to support service activation.
     */
    public HornetQActivator() {
        super(HORNETQ_TYPE);
    }
    
    @Override
    public ServiceHandler activateBinding(QName name, BindingModel config) {
        if (config.isServiceBinding()) {
            return handleServiceBinding((HornetQBindingModel)config, name);
        } else {
            return handleReferenceBinding((HornetQBindingModel)config, name);
        }
    }

    @Override
    public void deactivateBinding(QName name, ServiceHandler handler) {
        // Nothing to do here
    }


    private ServiceHandler handleServiceBinding(final HornetQBindingModel hbm, final QName serviceName) {
        final ServerLocator serverLocator = buildServerLocator(hbm.getHornetQConfig());
        return new InboundHandler(hbm, serverLocator, getServiceDomain());
    }
    
    private ServiceHandler handleReferenceBinding(final HornetQBindingModel hbm, final QName serviceName) {
        final ServerLocator serverLocator = buildServerLocator(hbm.getHornetQConfig());
        return new OutboundHandler(hbm, serverLocator);
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

        Long refreshTimeout = config.getRefreshTimeout();
        if (refreshTimeout == null) {
            refreshTimeout = HornetQClient.DEFAULT_DISCOVERY_REFRESH_TIMEOUT;
        }

        Long initialWaitTimeout = config.getInitialWaitTimeout();
        if (initialWaitTimeout == null) {
            initialWaitTimeout = HornetQClient.DEFAULT_DISCOVERY_INITIAL_WAIT_TIMEOUT;
        }

        final String localBindAddress = config.getLocalBindAddress();

        UDPBroadcastGroupConfiguration udpCfg = new UDPBroadcastGroupConfiguration(config.getGroupAddress(), config.getGroupPort(), localBindAddress, -1);
        final DiscoveryGroupConfiguration discoveryGroupConfiguration = new DiscoveryGroupConfiguration(refreshTimeout, initialWaitTimeout, udpCfg);

        return discoveryGroupConfiguration;
    }
}
