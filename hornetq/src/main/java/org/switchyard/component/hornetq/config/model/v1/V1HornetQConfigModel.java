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
package org.switchyard.component.hornetq.config.model.v1;

import static org.switchyard.component.hornetq.ConfigProperties.ACK_BATCH_SIZE;
import static org.switchyard.component.hornetq.ConfigProperties.AUTO_GROUP;
import static org.switchyard.component.hornetq.ConfigProperties.BLOCK_ON_ACKNOWLEDGE;
import static org.switchyard.component.hornetq.ConfigProperties.BLOCK_ON_DURABLE_SEND;
import static org.switchyard.component.hornetq.ConfigProperties.BLOCK_ON_NON_DURABLE_SEND;
import static org.switchyard.component.hornetq.ConfigProperties.CACHE_LARGE_MESSAGES_ON_CONSUMER;
import static org.switchyard.component.hornetq.ConfigProperties.CALL_TIMEOUT;
import static org.switchyard.component.hornetq.ConfigProperties.CLIENT_FAILURE_CHECK_PERIOD;
import static org.switchyard.component.hornetq.ConfigProperties.COMPRESS_LARGE_MESSAGES;
import static org.switchyard.component.hornetq.ConfigProperties.CONFIRMATION_WINDOW_SIZE;
import static org.switchyard.component.hornetq.ConfigProperties.CONNECTION_LOAD_BALANCING_POLICY_CLASS_NAME;
import static org.switchyard.component.hornetq.ConfigProperties.CONNECTION_TTL;
import static org.switchyard.component.hornetq.ConfigProperties.CONSUMER_MAX_RATE;
import static org.switchyard.component.hornetq.ConfigProperties.CONSUMER_WINDOW_SIZE;
import static org.switchyard.component.hornetq.ConfigProperties.QUEUE;
import static org.switchyard.component.hornetq.ConfigProperties.DISABLE_FINALIZED_CHECK;
import static org.switchyard.component.hornetq.ConfigProperties.FAILOVER_ON_INITIAL_CONNECTION;
import static org.switchyard.component.hornetq.ConfigProperties.GROUP_ID;
import static org.switchyard.component.hornetq.ConfigProperties.INITIAL_MESSAGE_PACKET_SIZE;
import static org.switchyard.component.hornetq.ConfigProperties.INITIAL_RECONNECT_ATTEMPTS;
import static org.switchyard.component.hornetq.ConfigProperties.MAX_RETRY_INTERVAL;
import static org.switchyard.component.hornetq.ConfigProperties.MIN_LARGE_MESSAGE_SIZE;
import static org.switchyard.component.hornetq.ConfigProperties.PRE_ACKNOWLEDGE;
import static org.switchyard.component.hornetq.ConfigProperties.PRODUCER_MAX_RATE;
import static org.switchyard.component.hornetq.ConfigProperties.PRODUCER_WINDOW_SIZE;
import static org.switchyard.component.hornetq.ConfigProperties.RECONNECT_ATTEMPTS;
import static org.switchyard.component.hornetq.ConfigProperties.RETRY_INTERVAL_MULTIPLIER;
import static org.switchyard.component.hornetq.ConfigProperties.RETRY_INTERVAL;
import static org.switchyard.component.hornetq.ConfigProperties.SCHEDULED_THREAD_POOL_MAX_SIZE;
import static org.switchyard.component.hornetq.ConfigProperties.THREAD_POOL_MAX_SIZE;
import static org.switchyard.component.hornetq.ConfigProperties.XA_SESSION;
import static org.switchyard.component.hornetq.ConfigProperties.USE_GLOBAL_POOLS;
import static org.switchyard.component.hornetq.ConfigProperties.USE_HA;
import static org.switchyard.component.hornetq.config.model.HornetQDiscoveryGroupConfigModel.DISCOVERY_GROUP;

import javax.xml.namespace.QName;

import org.switchyard.component.hornetq.config.model.HornetQConfigModel;
import org.switchyard.component.hornetq.config.model.HornetQConnectorConfigModel;
import org.switchyard.component.hornetq.config.model.HornetQConstants;
import org.switchyard.component.hornetq.config.model.HornetQDiscoveryGroupConfigModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 1.0 of {@link HornetQConfigModel}.
 * 
 * @author Daniel Bevenius
 *
 */
public class V1HornetQConfigModel extends BaseModel implements HornetQConfigModel {

    /**
     * Constructs a HornetQConfigModel using the default {@link HornetQConstants#DEFAULT_NAMESPACE}.
     */
    public V1HornetQConfigModel() {
        super(new QName(HornetQConstants.DEFAULT_NAMESPACE, CONFIG));
        setModelChildrenOrder(
                ACK_BATCH_SIZE,
                AUTO_GROUP,
                BLOCK_ON_ACKNOWLEDGE,
                BLOCK_ON_DURABLE_SEND,
                BLOCK_ON_NON_DURABLE_SEND,
                CACHE_LARGE_MESSAGES_ON_CONSUMER,
                CALL_TIMEOUT,
                CLIENT_FAILURE_CHECK_PERIOD,
                COMPRESS_LARGE_MESSAGES,
                CONNECTION_LOAD_BALANCING_POLICY_CLASS_NAME,
                CONNECTION_TTL,
                CONFIRMATION_WINDOW_SIZE,
                QUEUE,
                DISABLE_FINALIZED_CHECK,
                DISCOVERY_GROUP,
                FAILOVER_ON_INITIAL_CONNECTION, 
                GROUP_ID,
                INITIAL_MESSAGE_PACKET_SIZE,
                INITIAL_RECONNECT_ATTEMPTS,
                MAX_RETRY_INTERVAL,
                MIN_LARGE_MESSAGE_SIZE,
                PRE_ACKNOWLEDGE, 
                PRODUCER_MAX_RATE,
                PRODUCER_WINDOW_SIZE,
                RECONNECT_ATTEMPTS, 
                RETRY_INTERVAL,
                RETRY_INTERVAL_MULTIPLIER,
                SCHEDULED_THREAD_POOL_MAX_SIZE, 
                THREAD_POOL_MAX_SIZE, 
                USE_GLOBAL_POOLS,
                USE_HA);
    }

    /**
     * Constructs a HornetQConfigModel using the passed-in Configuration and 
     * Descriptor.
     * 
     * @param config the SwitchYard configuration.
     * @param desc the {@link Descriptor}.
     */
    public V1HornetQConfigModel(final Configuration config, final Descriptor desc) {
        super(config, desc);
    }
    
    @Override
    public HornetQConfigModel setQueue(final String destination) {
        setConfigValue(destination, QUEUE);
        return this;
    }
    
    @Override
    public String getQueue() {
        return getConfigValue(QUEUE);
    }
    
    @Override
    public HornetQConnectorConfigModel getConnectorConfiguration() {
        return (HornetQConnectorConfigModel) getFirstChildModel(HornetQConnectorConfigModel.CONNECTOR);
    }
    
    @Override
    public V1HornetQConfigModel setConnectorConfiguration(final HornetQConnectorConfigModel config) {
        setChildModel(config);
        return this;
    }
    
    @Override
    public V1HornetQConfigModel setDiscoveryGroup(final HornetQDiscoveryGroupConfigModel config) {
        setChildModel(config);
        return this;
    }
    
    @Override
    public HornetQDiscoveryGroupConfigModel getDiscoveryGroup() {
        return (HornetQDiscoveryGroupConfigModel) getFirstChildModel(HornetQDiscoveryGroupConfigModel.DISCOVERY_GROUP);
    }
    
    @Override
    public V1HornetQConfigModel disableFinalizeCheck() {
        setConfigValue("true", DISABLE_FINALIZED_CHECK);
        return this;
    }
    
    @Override
    public Boolean isDisableFinalizeCheck() {
        return getBooleanConfigValue(DISABLE_FINALIZED_CHECK);
    }
    
    @Override
    public V1HornetQConfigModel setClientFailureCheckPeriod(final Long clientFailureCheckPeriod) {
        setConfigValue(String.valueOf(clientFailureCheckPeriod), CLIENT_FAILURE_CHECK_PERIOD);
        return this;
    }

    @Override
    public Long getClientFailureCheckPeriod() {
        return getLongConfigValue(CLIENT_FAILURE_CHECK_PERIOD);
    }
    
    @Override
    public V1HornetQConfigModel setCacheLargeMessagesClient(final Boolean cached) {
        setConfigValue(String.valueOf(cached), CACHE_LARGE_MESSAGES_ON_CONSUMER);
        return this;
    }

    @Override
    public Boolean isCacheLargeMessagesClient() {
        return getBooleanConfigValue(CACHE_LARGE_MESSAGES_ON_CONSUMER);
    }
    
    @Override
    public Long getConnectionTTL() {
        return getLongConfigValue(CONNECTION_TTL);
    }

    @Override
    public V1HornetQConfigModel setConnectionTTL(final Long connectionTTL) {
        setConfigValue(String.valueOf(connectionTTL), CONNECTION_TTL);
        return this;
    }

    @Override
    public Long getCallTimeout() {
        return getLongConfigValue(CALL_TIMEOUT);
    }

    @Override
    public V1HornetQConfigModel setCallTimeout(final Long callTimeout) {
        setConfigValue(String.valueOf(callTimeout), CALL_TIMEOUT);
        return this;
    }

    @Override
    public V1HornetQConfigModel setMinLargeMessageSize(final Integer minLargeMessageSize) {
        setConfigValue(String.valueOf(minLargeMessageSize), MIN_LARGE_MESSAGE_SIZE);
        return this;
    }
    
    @Override
    public Integer getMinLargeMessageSize() {
        return getIntegerConfigValue(MIN_LARGE_MESSAGE_SIZE);
    }

    @Override
    public Integer getConsumerWindowSize() {
        return getIntegerConfigValue(CONSUMER_WINDOW_SIZE);
    }

    @Override
    public HornetQConfigModel setConsumerWindowSize(final Integer consumerWindowSize) {
        setConfigValue(String.valueOf(consumerWindowSize), CONSUMER_WINDOW_SIZE);
        return this;
    }
    
    @Override
    public Integer getConsumerMaxRate() {
        return getIntegerConfigValue(CONSUMER_MAX_RATE);
    }

    @Override
    public HornetQConfigModel setConsumerMaxRate(final Integer consumerMaxRate) {
        setConfigValue(String.valueOf(consumerMaxRate), CONSUMER_MAX_RATE);
        return this;
    }

    @Override
    public Integer getConfirmationWindowSize() {
        return getIntegerConfigValue(CONFIRMATION_WINDOW_SIZE);
    }

    @Override
    public HornetQConfigModel setConfirmationWindowSize(final Integer confirmationWindowSize) {
        setConfigValue(String.valueOf(confirmationWindowSize), CONFIRMATION_WINDOW_SIZE);
        return this;
    }

    @Override
    public Integer getProducerWindowSize() {
        return getIntegerConfigValue(PRODUCER_WINDOW_SIZE);
    }

    @Override
    public HornetQConfigModel setProducerWindowSize(final Integer producerWindowSize) {
        setConfigValue(String.valueOf(producerWindowSize), PRODUCER_WINDOW_SIZE);
        return this;
    }

    @Override
    public Integer getProducerMaxRate() {
        return getIntegerConfigValue(PRODUCER_MAX_RATE);
    }

    @Override
    public HornetQConfigModel setProducerMaxRate(final Integer producerMaxRate) {
        setConfigValue(String.valueOf(producerMaxRate), PRODUCER_MAX_RATE);
        return this;
    }

    @Override
    public Boolean isBlockOnAcknowledge() {
        return getBooleanConfigValue(BLOCK_ON_ACKNOWLEDGE);
    }

    @Override
    public HornetQConfigModel setBlockOnAcknowledge(final Boolean blockOnAcknowledge) {
        setConfigValue(String.valueOf(blockOnAcknowledge), BLOCK_ON_ACKNOWLEDGE);
        return this;
    }

    @Override
    public Boolean isBlockOnDurableSend() {
        return getBooleanConfigValue(BLOCK_ON_DURABLE_SEND);
    }

    @Override
    public HornetQConfigModel setBlockOnDurableSend(final Boolean blockOnDurableSend) {
        setConfigValue(String.valueOf(blockOnDurableSend), BLOCK_ON_DURABLE_SEND);
        return this;
    }

    @Override
    public Boolean isBlockOnNonDurableSend() {
        return getBooleanConfigValue(BLOCK_ON_NON_DURABLE_SEND);
    }

    @Override
    public HornetQConfigModel setBlockOnNonDurableSend(final Boolean blockOnNonDurableSend) {
        setConfigValue(String.valueOf(blockOnNonDurableSend), BLOCK_ON_NON_DURABLE_SEND);
        return this;
    }

    @Override
    public Boolean isAutoGroup() {
        return getBooleanConfigValue(AUTO_GROUP);
    }

    @Override
    public HornetQConfigModel setAutoGroup(final Boolean autoGroup) {
        setConfigValue(String.valueOf(autoGroup), AUTO_GROUP);
        return this;
    }

    @Override
    public String getGroupID() {
        return getConfigValue(GROUP_ID);
    }

    @Override
    public HornetQConfigModel setGroupID(final String groupID) {
        setConfigValue(groupID, GROUP_ID);
        return this;
    }

    @Override
    public Boolean isPreAcknowledge() {
        return getBooleanConfigValue(PRE_ACKNOWLEDGE);
    }

    @Override
    public HornetQConfigModel setPreAcknowledge(final Boolean preAcknowledge) {
        setConfigValue(String.valueOf(preAcknowledge), PRE_ACKNOWLEDGE);
        return this;
    }

    @Override
    public Integer getAckBatchSize() {
        return getIntegerConfigValue(ACK_BATCH_SIZE);
    }

    @Override
    public HornetQConfigModel setAckBatchSize(final Integer ackBatchSize) {
        setConfigValue(String.valueOf(ackBatchSize), ACK_BATCH_SIZE);
        return this;
    }

    @Override
    public Boolean isUseGlobalPools() {
        return getBooleanConfigValue(USE_GLOBAL_POOLS);
    }

    @Override
    public HornetQConfigModel setUseGlobalPools(final Boolean useGlobalPools) {
        setConfigValue(String.valueOf(useGlobalPools), USE_GLOBAL_POOLS);
        return this;
    }

    @Override
    public Integer getScheduledThreadPoolMaxSize() {
        return getIntegerConfigValue(SCHEDULED_THREAD_POOL_MAX_SIZE);
    }

    @Override
    public HornetQConfigModel setScheduledThreadPoolMaxSize(final Integer scheduledThreadPoolMaxSize) {
        setConfigValue(String.valueOf(scheduledThreadPoolMaxSize), SCHEDULED_THREAD_POOL_MAX_SIZE);
        return this;
    }

    @Override
    public Integer getThreadPoolMaxSize() {
        return getIntegerConfigValue(THREAD_POOL_MAX_SIZE);
    }

    @Override
    public HornetQConfigModel setThreadPoolMaxSize(final Integer threadPoolMaxSize) {
        setConfigValue(String.valueOf(threadPoolMaxSize), THREAD_POOL_MAX_SIZE);
        return this;
    }

    @Override
    public Long getRetryInterval() {
        return getLongConfigValue(RETRY_INTERVAL);
    }

    @Override
    public HornetQConfigModel setRetryInterval(final Long retryInterval) {
        setConfigValue(String.valueOf(retryInterval), RETRY_INTERVAL);
        return this;
    }

    @Override
    public Double getRetryIntervalMultiplier() {
        return getDoubleConfigValue(RETRY_INTERVAL_MULTIPLIER);
    }

    @Override
    public HornetQConfigModel setRetryIntervalMultiplier(final Double retryIntervalMultiplier) {
        setConfigValue(String.valueOf(retryIntervalMultiplier), RETRY_INTERVAL_MULTIPLIER);
        return this;
    }

    @Override
    public Long getMaxRetryInterval() {
        return getLongConfigValue(MAX_RETRY_INTERVAL);
    }

    @Override
    public HornetQConfigModel setMaxRetryInterval(final Long maxRetryInterval) {
        setConfigValue(String.valueOf(maxRetryInterval), MAX_RETRY_INTERVAL);
        return this;
    }

    @Override
    public Integer getReconnectAttempts() {
        return getIntegerConfigValue(RECONNECT_ATTEMPTS);
    }

    @Override
    public HornetQConfigModel setReconnectAttempts(final Integer reconnectAttempts) {
        setConfigValue(String.valueOf(reconnectAttempts), RECONNECT_ATTEMPTS);
        return this;
    }

    @Override
    public V1HornetQConfigModel setInitialConnectAttempts(final Integer reconnectAttempts) {
        setConfigValue(String.valueOf(reconnectAttempts), INITIAL_RECONNECT_ATTEMPTS);
        return this;
    }

    @Override
    public Integer getInitialConnectAttempts() {
        return getIntegerConfigValue(INITIAL_RECONNECT_ATTEMPTS);
    }

    @Override
    public Boolean isFailoverOnInitialConnection() {
        return getBooleanConfigValue(FAILOVER_ON_INITIAL_CONNECTION);
    }

    @Override
    public V1HornetQConfigModel setFailoverOnInitialConnection(final Boolean failover) {
        setConfigValue(String.valueOf(failover), FAILOVER_ON_INITIAL_CONNECTION);
        return this;
    }

    @Override
    public String getConnectionLoadBalancingPolicyClassName() {
        return getConfigValue(CONNECTION_LOAD_BALANCING_POLICY_CLASS_NAME);
    }

    @Override
    public V1HornetQConfigModel setConnectionLoadBalancingPolicyClassName(final String loadBalancingPolicyClassName) {
        setConfigValue(loadBalancingPolicyClassName, CONNECTION_LOAD_BALANCING_POLICY_CLASS_NAME);
        return this;
    }

    @Override
    public Integer getInitialMessagePacketSize() {
        return getIntegerConfigValue(INITIAL_MESSAGE_PACKET_SIZE);
    }

    @Override
    public V1HornetQConfigModel setInitialMessagePacketSize(final Integer size) {
        setConfigValue(String.valueOf(size), INITIAL_MESSAGE_PACKET_SIZE);
        return this;
    }
    
    @Override
    public V1HornetQConfigModel setUseHA(final Boolean useHA) {
        setConfigValue(String.valueOf(useHA), USE_HA);
        return this;
    }

    @Override
    public Boolean isUseHA() {
        return getBooleanConfigValue(USE_HA);
    }

    @Override
    public Boolean isCompressLargeMessage() {
        return getBooleanConfigValue(COMPRESS_LARGE_MESSAGES);
    }

    @Override
    public V1HornetQConfigModel setCompressLargeMessage(final Boolean compress) {
        setConfigValue(String.valueOf(compress), COMPRESS_LARGE_MESSAGES);
        return this;
    }
    
    @Override
    public boolean isXASession() {
        return getBooleanConfigValue(XA_SESSION, false);
    }

    @Override
    public HornetQConfigModel setXASession(boolean xa) {
        setConfigValue(String.valueOf(xa), XA_SESSION);
        return this;
    }
    
    private void setConfigValue(final String value, String propertyName) {
        final Configuration config = getModelConfiguration().getFirstChild(propertyName);
        if (config != null) {
            config.setValue(value);
        } else {
            V1NameValueModel model = new V1NameValueModel(propertyName);
            model.setValue(value);
            setChildModel(model);
        }
    }
    
    private String getConfigValue(final String propertyName) {
        final Configuration config = getModelConfiguration().getFirstChild(propertyName);
        return config != null ? config.getValue() : null;
    }
    
    private Long getLongConfigValue(final String propertyName) {
        final String value = getConfigValue(propertyName);
        if (value == null) {
            return null;
        }
        return Long.valueOf(value);
    }
    
    private Double getDoubleConfigValue(final String propertyName) {
        final String value = getConfigValue(propertyName);
        if (value == null) {
            return null;
        }
        return Double.valueOf(value);
    }
    
    private Boolean getBooleanConfigValue(final String propertyName) {
        final String value = getConfigValue(propertyName);
        if (value == null) {
            return null;
        }
        return Boolean.valueOf(value);
    }
    
    private Boolean getBooleanConfigValue(final String propertyName, final boolean defaultValue) {
        final String value = getConfigValue(propertyName);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.valueOf(value);
    }
    
    private Integer getIntegerConfigValue(final String propertyName) {
        final String value = getConfigValue(propertyName);
        if (value == null) {
            return null;
        }
        return Integer.valueOf(value);
    }


}
