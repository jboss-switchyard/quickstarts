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
package org.switchyard.component.hornetq.config.model;

import org.switchyard.config.model.Model;

/**
 * A configuration model containing the configuration options for HornetQ 
 * that are available in SwitchYard.
 * 
 * @author Daniel Bevenius
 *
 */
public interface HornetQConfigModel extends Model {
    
    /**
     * The name of this configuration element.
     */
    String CONFIG = "config";
    
    /**
     * Sets the HornetQ destination.
     * 
     * @param queue the destination in HornetQ.
     * @return {@link HornetQConfigModel} to support method chaining. Or null if not set.
     */
    HornetQConfigModel setQueue(String queue);
    
    /**
     * Gets the HornetQ destination.
     * 
     * @return String the HornetQ destination. Or null if not set.
     */
    String getQueue();
    
    /**
     * Sets the {@link HornetQConfigModel} that this HornetQConfigModel should use.
     * 
     * @param config the {@link HornetQConfigModel}.
     * @return {@link HornetQConfigModel} to support method chaining. Or null if not set.
     */
    HornetQConfigModel setConnectorConfiguration(HornetQConnectorConfigModel config);
    
    /**
     * Gets the {@link HornetQConfigModel} that this HornetQConfigModel is using.
     * 
     * @return {@link HornetQConfigModel} to support method chaining. Or null if not set.
     */
    HornetQConnectorConfigModel getConnectorConfiguration();
    
    /**
     * Sets the {@link HornetQDiscoveryGroupConfigModel} that this {@link HornetQConfigModel}
     * should use.
     * 
     * @param groupModel the {@link HornetQDiscoveryGroupConfigModel}.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setDiscoveryGroup(HornetQDiscoveryGroupConfigModel groupModel);
    
    /**
     * Gets the {@link HornetQDiscoveryGroupConfigModel} that this HornetQConfigModel is using.
     * 
     * @return {@link HornetQDiscoveryGroupConfigModel} the discovery group model. Or null if not set.
     */
    HornetQDiscoveryGroupConfigModel getDiscoveryGroup();
    
    /**
     * See {@link ServerLocatorBuilder#disableFinalizedCheck(Boolean)}.
     * 
     * @return true if finalized checks are disabled. Or null if not set.
     */
    Boolean isDisableFinalizeCheck();
    
    /**
     * See {@link ServerLocatorBuilder#disableFinalizedCheck(Boolean)}.
     * 
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel disableFinalizeCheck();
    
    /**
     * See {@link ServerLocatorBuilder#clientFailureCheckPeriod(Long)}.
     * 
     * @return Long the period in ms. Or null if not set.
     */
    Long getClientFailureCheckPeriod();
    
    /**
     * See {@link ServerLocatorBuilder#clientFailureCheckPeriod(Long)}.
     * 
     * @param clientFailureCheckPeriod period in ms.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setClientFailureCheckPeriod(Long clientFailureCheckPeriod);
    
    /**
     * See {@link ServerLocatorBuilder#cacheLargeMessagesOnConsumers(Boolean)}.
     * 
     * @return Boolean true if large messages should be cached. Or null if not set.
     */
    Boolean isCacheLargeMessagesClient();
    
    /**
     * See {@link ServerLocatorBuilder#cacheLargeMessagesOnConsumers(Boolean)}.
     * 
     * @param cached true if large messages should be cached.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setCacheLargeMessagesClient(Boolean cached);
    
    /**
     * See {@link ServerLocatorBuilder#connectionTTL(Long)}.
     * 
     * @return Long the connection time to live. Or null if not set.
     */
    Long getConnectionTTL();
    
    /**
     * See {@link ServerLocatorBuilder#connectionTTL(Long)}.
     * 
     * @param connectionTTL the connection time to live.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setConnectionTTL(Long connectionTTL);
    
    /**
     * See {@link ServerLocatorBuilder#callTimeout(Long)}.
     * 
     * @return Long the call time out. Or null is not set.
     */
    Long getCallTimeout();
    
    /**
     * See {@link ServerLocatorBuilder#callTimeout(Long)}.
     * 
     * @param callTimeout blocking calltimout in ms.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setCallTimeout(Long callTimeout);
    
    /**
     * See {@link ServerLocatorBuilder#minLargeMessageSize(Integer)}.
     * 
     * @return Integer the min size of a message which is to be considered large. Or null if not set.
     */
    Integer getMinLargeMessageSize();
    
    /**
     * See {@link ServerLocatorBuilder#minLargeMessageSize(Integer)}.
     * 
     * @param minLargeMessageSize the min size of a message which is to be considered large
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setMinLargeMessageSize(Integer minLargeMessageSize);
    
    /**
     * See {@link ServerLocatorBuilder#consumerWindowSize(Integer)}.
     * 
     * @return Integer the size of the client side buffer. A value of 0 will not buffer at all. Or null if not set.
     */
    Integer getConsumerWindowSize();
    
    /**
     * See {@link ServerLocatorBuilder#consumerWindowSize(Integer)}.
     * 
     * @param consumerWindowSize the size of the client side buffer. A value of 0 will not buffer at all.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setConsumerWindowSize(Integer consumerWindowSize);
    
    /**
     * See {@link ServerLocatorBuilder#consumerMaxRate(Integer)}.
     * 
     * @return Integer the rate of messages per second. Or null if not set.
     */
    Integer getConsumerMaxRate();
    
    /**
     * See {@link ServerLocatorBuilder#consumerMaxRate(Integer)}.
     * 
     * @param consumerMaxRate the rate of messages per second.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setConsumerMaxRate(Integer consumerMaxRate);
    
    /**
     * See {@link ServerLocatorBuilder#confirmationWindowSize(Integer)}.
     * 
     * @return Integer the size in bytes of the confiramation buffer.
     */
    Integer getConfirmationWindowSize();
    
    /**
     * See {@link ServerLocatorBuilder#confirmationWindowSize(Integer)}.
     * 
     * @param confirmationWindowSize the size in bytes of the confiramation buffer.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setConfirmationWindowSize(Integer confirmationWindowSize);
    
    /**
     * See {@link ServerLocatorBuilder#producerWindowSize(Integer)}.
     * 
     * @return Integer the size in bytes of that a producer will request. Or null if not set.
     */
    Integer getProducerWindowSize();
    
    /**
     * See {@link ServerLocatorBuilder#producerWindowSize(Integer)}.
     * 
     * @param producerWindowSize the size in bytes of that a producer will request.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setProducerWindowSize(Integer producerWindowSize);
    
    /**
     * See {@link ServerLocatorBuilder#producerMaxRate(Integer)}.
     * 
     * @return Integer the rate of messages per second.
     */
    Integer getProducerMaxRate();
    
    /**
     * See {@link ServerLocatorBuilder#producerMaxRate(Integer)}.
     * 
     * @param producerMaxRate the rate of messages per second. Or null if not set.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setProducerMaxRate(Integer producerMaxRate);
    
    /**
     * See {@link ServerLocatorBuilder#blockOnAcknowledge(Boolean)}.
     * 
     * @return Boolean true if consumers should block. Or null if not set.
     */
    Boolean isBlockOnAcknowledge();
    
    /**
     * See {@link ServerLocatorBuilder#blockOnAcknowledge(Boolean)}.
     * 
     * @param blockOnAcknowledge true if consumers should block.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setBlockOnAcknowledge(Boolean blockOnAcknowledge);

    /**
     * See {@link ServerLocatorBuilder#blockOnDurableSend(Boolean)}.
     * 
     * @return Boolean true is producers should block or null if not set.
     */
    Boolean isBlockOnDurableSend();
    
    /**
     * See {@link ServerLocatorBuilder#blockOnDurableSend(Boolean)}.
     * 
     * @param blockOnDurableSend true if producers should block.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setBlockOnDurableSend(Boolean blockOnDurableSend);

    /**
     * See {@link ServerLocatorBuilder#blockOnNonDurableSend(Boolean)}.
     * 
     * @return Boolean true if producers should block or null if not set.
     */
    Boolean isBlockOnNonDurableSend();
    
    /**
     * See {@link ServerLocatorBuilder#blockOnNonDurableSend(Boolean)}.
     * 
     * @param blockOnNonDurableSend true if producers should block.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setBlockOnNonDurableSend(Boolean blockOnNonDurableSend);

    /**
     * See {@link ServerLocatorBuilder#autoGroup(Boolean)}.
     * 
     * @return Boolean true if producers will automatically assign a group ID to outgoing messages.
     */
    Boolean isAutoGroup();
    
    /**
     * See {@link ServerLocatorBuilder#autoGroup(Boolean)}.
     * 
     * @param autoGroup true if producers will automatically assign a group ID to outgoing messages.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setAutoGroup(Boolean autoGroup);

    /**
     * See {@link ServerLocatorBuilder#groupID(String)}.
     * 
     * @return String the group ID producers should set on messages. Or null if not set.
     */
    String getGroupID();
    
    /**
     * See {@link ServerLocatorBuilder#groupID(String)}.
     * 
     * @param groupID the group ID producers should set on messages.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setGroupID(String groupID);

    /**
     * See {@link ServerLocatorBuilder#preAcknowledge(Boolean)}.
     * 
     * @return Boolean true if the server may acknowledge messages before delivering them to clients. Or null if not set.
     */
    Boolean isPreAcknowledge();
    
    /**
     * See {@link ServerLocatorBuilder#preAcknowledge(Boolean)}.
     * 
     * @param preAcknowledge true if the server may acknowledge messages before delivering them to clients.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setPreAcknowledge(Boolean preAcknowledge);

    /**
     * See {@link ServerLocatorBuilder#ackBatchSize(Integer)}.
     * 
     * @return Integer the batch size in bytes. Or null if not set.
     */
    Integer getAckBatchSize();
    
    /**
     * See {@link ServerLocatorBuilder#ackBatchSize(Integer)}.
     * 
     * @param ackBatchSize the batch size in bytes.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setAckBatchSize(Integer ackBatchSize);

    /**
     * See {@link ServerLocatorBuilder#useGlobalPools(Boolean)}.
     * 
     * @return Boolean true if global thread pools should be use. Or null if not set.
     */
    Boolean isUseGlobalPools();
    
    /**
     * See {@link ServerLocatorBuilder#useGlobalPools(Boolean)}.
     * 
     * @param useGlobalPools true if global thread pools should be use.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setUseGlobalPools(Boolean useGlobalPools);

    /**
     * See {@link ServerLocatorBuilder#scheduledThreadPoolMaxSize(Integer)}.
     * 
     * @return Integer the size of the scheduled thread pool. Or null if not set.
     */
    Integer getScheduledThreadPoolMaxSize();
    
    /**
     * See {@link ServerLocatorBuilder#scheduledThreadPoolMaxSize(Integer)}.
     * 
     * @param scheduledThreadPoolMaxSize the size of the scheduled thread pool.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setScheduledThreadPoolMaxSize(Integer scheduledThreadPoolMaxSize);

    /**
     * See {@link ServerLocatorBuilder#threadPoolMaxSize(Integer)}.
     * 
     * @return Integer threadPoolMaxSize the size of the general thread pool. Or null if not set.
     */
    Integer getThreadPoolMaxSize();
    
    /**
     * See {@link ServerLocatorBuilder#threadPoolMaxSize(Integer)}.
     * 
     * @param threadPoolMaxSize the size of the general thread pool.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setThreadPoolMaxSize(Integer threadPoolMaxSize);

    /**
     * See {@link ServerLocatorBuilder#retryInterval(Long)}.
     * 
     * @return Long the interval in milliseconds. Or null if not set.
     */
    Long getRetryInterval();
    
    /**
     * See {@link ServerLocatorBuilder#retryInterval(Long)}.
     * 
     * @param retryInterval the interval in milliseconds.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setRetryInterval(Long retryInterval);

    /**
     * See {@link ServerLocatorBuilder#retryIntervalMultiplier(Double)}.
     * 
     * @return Double the multiplier to apply.
     */
    Double getRetryIntervalMultiplier();
    
    /**
     * See {@link ServerLocatorBuilder#retryIntervalMultiplier(Double)}.
     * 
     * @param retryIntervalMultiplier the multiplier to apply.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setRetryIntervalMultiplier(Double retryIntervalMultiplier);

    /**
     * See {@link ServerLocatorBuilder#maxRetryInterval(Long)}.
     * 
     * @return maxRetryInterval the maximum retry interval. Or null if not set.
     */
    Long getMaxRetryInterval();
    
    /**
     * See {@link ServerLocatorBuilder#maxRetryInterval(Long)}.
     * 
     * @param maxRetryInterval the maximum retry interval
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setMaxRetryInterval(Long maxRetryInterval);

    /**
     * See {@link ServerLocatorBuilder#reconnectAttempts(Integer)}.
     * 
     * @return Integer the number of times to retry. Or null if not set.
     */
    Integer getReconnectAttempts();
    
    /**
     * See {@link ServerLocatorBuilder#reconnectAttempts(Integer)}.
     * 
     * @param reconnectAttempts the number of times to retry.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setReconnectAttempts(Integer reconnectAttempts);

    /**
     * See {@link ServerLocatorBuilder#initialReconnectAttempts(Integer)}.
     * 
     * @param reconnectAttempts the number of times to retry.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setInitialConnectAttempts(Integer reconnectAttempts);
    
    /**
     * See {@link ServerLocatorBuilder#reconnectAttempts(Integer)}.
     * 
     * @return Integer the number of times to retry.
     */
    Integer getInitialConnectAttempts();
    
    /**
     * See {@link ServerLocatorBuilder#failoverOnInitialConnection(Boolean)}.
     * 
     * @return Boolean true if failover to the backup server should be performed. Or null if not set.
     */
    Boolean isFailoverOnInitialConnection();
    
    /**
     * See {@link ServerLocatorBuilder#failoverOnInitialConnection(Boolean)}.
     * 
     * @param failover true if failover to the backup server should be performed.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setFailoverOnInitialConnection(Boolean failover);

    /**
     * See {@link ServerLocatorBuilder#connectionLoadBalancingPolicyClassName(String)}.
     * 
     * @return String the fully qualified name of the connection load balaning policy implementation.
     */
    String getConnectionLoadBalancingPolicyClassName();
    
    /**
     * See {@link ServerLocatorBuilder#connectionLoadBalancingPolicyClassName(String)}.
     * 
     * @param className the fully qualified name of the connection load balaning policy implementation.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setConnectionLoadBalancingPolicyClassName(String className);

    /**
     * See {@link ServerLocatorBuilder#initialMessagePacketSize(Integer)}.
     * 
     * @return Integer initial size of messages created through this factory.
     */
    Integer getInitialMessagePacketSize();
    
    /**
     * See {@link ServerLocatorBuilder#initialMessagePacketSize(Integer)}.
     * 
     * @param packetSize initial size of messages created through this factory.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setInitialMessagePacketSize(Integer packetSize);
    
    /**
     * See {@link ServerLocatorBuilder#useHA(Boolean)}.
     * 
     * @param useHA true if HA should be enabled for the ServerLocator to be built.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setUseHA(Boolean useHA);
    
    /**
     * See {@link ServerLocatorBuilder#useHA(Boolean)}.
     * 
     * @return Boolean true if HA should be enabled. Or null if not set.
     */
    Boolean isUseHA();
    
    /**
     * See {@link ServerLocatorBuilder#compressLargeMessage(Boolean)}.
     * 
     * @return Boolean if true, large messages will be compressed. Or null if not set.
     */
    Boolean isCompressLargeMessage();
    
    /**
     * See {@link ServerLocatorBuilder#compressLargeMessage(Boolean)}.
     * 
     * @param compress if true, large messages will be compressed.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setCompressLargeMessage(Boolean compress);
    
    /**
     * Whether the ClientSession created should support XA semantics.
     * 
     * @return boolean if true the HornetQ ClientSession should support XA semantics.
     */
    boolean isXASession();
    
    /**
     * Configures if the ClientSession created should support XA semantics.
     * 
     * @param xa if true the HornetQ ClientSession that supports XA semantics.
     * @return {@link HornetQConfigModel} to support method chaining.
     */
    HornetQConfigModel setXASession(boolean xa);
    
}
