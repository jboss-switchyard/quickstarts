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
package org.switchyard.component.hornetq;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.hornetq.api.core.DiscoveryGroupConfiguration;
import org.hornetq.api.core.TransportConfiguration;

/**
 * An Apache Camel Component capable of creating consumers and producers to 
 * <a href="http://www.jboss.org/hornetq">HornetQ</a> destinations.
 * 
 * @author Daniel Bevenius
 */
public class HornetQComponent extends DefaultComponent {
    
    private ServerLocatorBuilder _serverLocatorBuilder = new ServerLocatorBuilder();
    
    @Override
    protected Endpoint createEndpoint(final String uri, final String path, final Map<String, Object> parameters) throws Exception {
        final Endpoint endpoint = new HornetQEndpoint(path, uri, this, _serverLocatorBuilder);
        setProperties(endpoint, parameters);
        return endpoint;
    }
    
    /**
     * Sets the {@link TransportConfiguration}s to be used to locate servers. This is a static configuration
     * of servers as opposed to using discovery.
     * 
     * @param config the {@link TransportConfiguration} to use.
     */
    public void setTransportConfiguration(final TransportConfiguration config) {
        _serverLocatorBuilder.transportConfigurations(config);
    }
    
    /**
     * Sets the {@link DiscoveryGroupConfiguration} to be used for UDP discovery of servers.
     * 
     * @param config the {@link DiscoveryGroupConfiguration} to use.
     */
    public void setDiscoveryGroupConfiguration(final DiscoveryGroupConfiguration config) {
        _serverLocatorBuilder.discoveryGroupConfiguration(config);
    }
    
    /**
     * Determines whether High Availablility (clustering) is to be enabled.
     * 
     * @param useHA true if HA should be enabled for the ServerLocator to be built.
     */
    public void setUseHA(final Boolean useHA) {
        _serverLocatorBuilder.useHA(useHA);
    }
    
    /**
     * Disables any checks when finalized method is called.
     * 
     * @param disable true if HA should be enabled for the ServerLocator to be built.
     */
    public void setDisableFinalizedCheck(final Boolean disable) {
        _serverLocatorBuilder.disableFinalizedCheck(disable);
    }
    
    /**
     * Configures the period in milliseconds. If the client has not received a ping from the server within
     * this period it will consider the connection failed.
     * 
     * @param period in ms.
     */
    public void setClientFailureCheckPeriod(final Long period) {
        _serverLocatorBuilder.clientFailureCheckPeriod(period);
    }
    
    /**
     * Configures whether large messages should be cached by consumers.
     * 
     * @param cache if true large messages will be cached on the client.
     */
    public void setCacheLargeMessagesOnConsumer(final Boolean cache) {
        _serverLocatorBuilder.cacheLargeMessagesOnConsumers(cache);
    }
    
    /**
     * Configures the time the server will hold a connection open without receiving any data
     * from the client.
     * 
     * @param ttl time in miliseconds.
     */
    public void setConnectionTTL(final Long ttl) {
        _serverLocatorBuilder.connectionTTL(ttl);
    }
    
    /**
     * Will configure the blocking call timeout.
     * 
     * @param timeout blocking calltimout in ms.
     */
    public void setCallTimeout(final Long timeout) {
        _serverLocatorBuilder.callTimeout(timeout);
    }
    
    /**
     * Will configure the size of mesages that will be considered large messages. Any message 
     * larger then this size will be considered a large message to HornetQ.
     * 
     * @param size the min size of a message which is to be considered large
     */
    public void setMinLargeMessageSize(final Integer size) {
        _serverLocatorBuilder.minLargeMessageSize(size);
    }
    
    /**
     * Will configure the client side message buffer size.
     * For slow consumers you can disable any client side buffering by setting this
     * value to 0.
     * 
     * @param size the size of the client side buffer. A value of 0 will not buffer at all.
     */
    public void setConsumerWindowSize(final Integer size) {
        _serverLocatorBuilder.consumerWindowSize(size);
    }
    
    /**
     * Will configure the fastest rate a consumer may consume messages per second.
     * 
     * @param rate the rate of messages per second.
     */
    public void setConsumerMaxRate(final Integer rate) {
        _serverLocatorBuilder.consumerMaxRate(rate);
    }
    
    /**
     * Will configure the size of the confirmation buffer for consumers.
     * 
     * @param size the size in bytes of the confiramation buffer.
     */
    public void setConfirmationWindowSize(final Integer size) {
        _serverLocatorBuilder.confirmationWindowSize(size);
    }
    
    /**
     * Will configure the amount of credits, in bytes, a producer will request from the
     * server in one go.
     * Producers can only send messages to an address as long as they have sufficient 
     * credits to do so.
     * 
     * @param size the size in bytes of that a producer will request.
     */
    public void setProducerWindowSize(final Integer size) {
        _serverLocatorBuilder.producerWindowSize(size);
    }
    
    /**
     * Will configure the fastest rate a producer may send messages per second.
     * 
     * @param rate the rate of messages per second.
     */
    public void setProducerMaxRate(final Integer rate) {
        _serverLocatorBuilder.producerMaxRate(rate);
    }
    
    /**
     * Configures whether consumers should block while sending acknowledges or if the
     * consumer should send them asynchronously.
     * 
     * @param block true if consumers should block.
     */
    public void setBlockOnAcknowledge(final Boolean block) {
        _serverLocatorBuilder.blockOnAcknowledge(block);
    }
    
    /**
     * Configures whether producers should block while sending durable messages or if the
     * consumer should send them asynchronously.
     * 
     * @param block true if producers should block.
     */
    public void setBlockOnDurableSend(final Boolean block) {
        _serverLocatorBuilder.blockOnDurableSend(block);
    }
    
    /**
     * Configures whether producers should block while sending non-durable messages or if the
     * consumer should send them asynchronously.
     * 
     * @param block true if producers should block.
     */
    public void setBlockOnNonDurableSend(final Boolean block) {
        _serverLocatorBuilder.blockOnNonDurableSend(block);
    }
    
    /**
     * Configures whether producers will automatically assign a group ID
     * to messages the producer sends.
     * </p>
     * For more information, please see "28.1 Using Core API" of the HornetQ User Manual.
     * 
     * @param autoGroup true if producers will automatically assign a group ID to outgoing messages.
     */
    public void setAutoGroup(final Boolean autoGroup) {
        _serverLocatorBuilder.autoGroup(autoGroup);
    }
    
    /**
     * Configures the group ID that producers will automatically set on
     * messages the producer sends.
     * </p>
     * For more information, please see "28.2 Using JMS" of the HornetQ User Manual.
     * 
     * @param id the group ID producers should set on messages.
     */
    public void setGroupID(final String id) {
        _serverLocatorBuilder.groupID(id);
    }
    
    /**
     * Configures whether the server may acknowledge messages before deliverying them
     * to clients. This can be used when you can afford to lose messages in the event 
     * of a failure.
     * </p>
     * For more information, please see "Chapter 29. Pre-Acknowledge Mode" of the HornetQ 
     * User Manual.
     * 
     * @param preAck true if the server may acknowledge messages before delivering them to clients.
     */
    public void setPreAcknowledge(final Boolean preAck) {
        _serverLocatorBuilder.preAcknowledge(preAck);
    }
    
    /**
     * Configures the acknowledgement batch size for consumers. Consumers will then send
     * acknowledges in batches instead of sending them one-by-one.
     * 
     * @param size the batch size in bytes.
     */
    public void setAckBatchSize(final Integer size) {
        _serverLocatorBuilder.ackBatchSize(size);
    }
    
    /**
     * Configures whether clients should use a globally shared thread pool, a pool which 
     * is shared with others in the same JVM, or have its own thread pool.
     * 
     * @param useGlobalPools true if global thread pools should be use.
     */
    public void setUseGlobalPools(final Boolean useGlobalPools) {
        _serverLocatorBuilder.useGlobalPools(useGlobalPools);
    }
    
    /**
     * Configures the size of the scheduled thread pool for this service locator. 
     * 
     * @param size the size of the scheduled thread pool.
     */
    public void setScheduledThreadPoolMaxSize(final Integer size) {
        _serverLocatorBuilder.scheduledThreadPoolMaxSize(size);
    }
    
    /**
     * Configures the size of the general thread pool for this service locator. 
     * 
     * @param size the size of the general thread pool.
     */
    public void setThreadPoolMaxSize(final Integer size) {
        _serverLocatorBuilder.threadPoolMaxSize(size);
    }
    
    /**
     * Configures the time in ms to retry a connection after failing.
     * 
     * @param interval the interval in milliseconds.
     */
    public void setRetryInterval(final Long interval) {
        _serverLocatorBuilder.retryInterval(interval);
    }
    
    /**
     * Configures the retry multiplier to apply to successive retry intervals.
     * 
     * @param multiplier the multiplier to apply.
     */
    public void setRetryIntervalMultiplier(final Double multiplier) {
        _serverLocatorBuilder.retryIntervalMultiplier(multiplier);
    }
    
    /**
     * Configures the maimum retry interval.
     * 
     * @param max the maximum retry interval
     */
    public void setMaxRetryInterval(final Long max) {
        _serverLocatorBuilder.maxRetryInterval(max);
    }
    
    /**
     * Configures the max number of times to retry a connection upon failure.
     * 
     * @param attempts the number of times to retry.
     */
    public void setReconnectAttempts(final Integer attempts) {
        _serverLocatorBuilder.reconnectAttempts(attempts);
    }
    
    /**
     * Configures the max number of times to retry an initial connection to the live server.
     * 
     * @param attempts the number of times to retry.
     */
    public void setInitialReconnectAttempts(final Integer attempts) {
        _serverLocatorBuilder.initialReconnectAttempts(attempts);
    }
    
    /**
     * Configures whether the client will automatically attempt to connect to the backup
     * server if the initial connection attempt fails.
     * 
     * @param failover true if failover to the backup server should be performed.
     */
    public void setFailoverOnInitialConnection(final Boolean failover) {
        _serverLocatorBuilder.failoverOnInitialConnection(failover);
    }
    
    /**
     * Configures connection load balancing policy to use. The class must implement
     * {@link ConnectionLoadBalancingPolicy}.
     * 
     * @param className the fully qualified name of the connection load balaning policy implementation.
     */
    public void setConnectionLoadBalancingPolicyClassName(final String className) {
        _serverLocatorBuilder.connectionLoadBalancingPolicyClassName(className);
    }
    
    /**
     * Sets the initial size of messages created through this factory.
     * 
     * @param packetSize initial size of messages created through this factory.
     */
    public void setInitialMessagePacketSize(final Integer packetSize) {
        _serverLocatorBuilder.initialMessagePacketSize(packetSize);
    }
    
    /**
     * Configures is compression of large messages should be performed.
     * 
     * @param compress if true, large messages will be compressed.
     */
    public void setCompessLargeMessage(final Boolean compress) {
        _serverLocatorBuilder.compressLargeMessage(compress);
    }
    
}
