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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hornetq.api.core.DiscoveryGroupConfiguration;
import org.hornetq.api.core.Interceptor;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClusterTopologyListener;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.client.impl.ServerLocatorImpl;

/**
 * A builder that enables the configuration and creation of a HornetQ {@link ServerLocator}
 * in a step by step manner. 
 * </p>
 * 
 * @author Daniel Bevenius
 *
 */
public class ServerLocatorBuilder {
    
    private boolean _useHA;
    private DiscoveryGroupConfiguration _discoveryGroupConf;
    private TransportConfiguration[] _transportConfigs;
    private Boolean _disableFinalizedCheck = true;
    private Long _clientFailureCheckPeriod;
    private Boolean _cacheLargeMessagesOnConsumers;
    private Long _connectionTTL;
    private Long _callTimeout;
    private Integer _minLargeMessageSize;
    private Integer _consumerWindowSize;
    private Integer _consumerMaxRate;
    private Integer _confirmationWindowSize;
    private Integer _producerWindowSize;
    private Integer _producerMaxRate;
    private Boolean _blockOnAcknowledge;
    private Boolean _blockOnDurableSend;
    private Boolean _blockOnNonDurableSend;
    private Boolean _autoGroup;
    private String _groupID;
    private Boolean _preAcknowledge;
    private Integer _ackBatchSize;
    private Boolean _useGlobalPools;
    private Integer _scheduledThreadPoolMaxSize;
    private Integer _threadPoolMaxSize;
    private Long _retryInterval;
    private Double _retryIntervalMultiplier;
    private Long _maxRetryInterval;
    private Integer _reconnectAttempts;
    private Integer _initialConnectAttempts;
    private Boolean _failoverOnInitialConnection;
    private String _connectionLoadBalancingPolicyClassName;
    private Integer _initialMessagePacketSize;
    private List<Interceptor> _interceptors = new ArrayList<Interceptor>();
    private Boolean _compressLargeMessage;
    private List<ClusterTopologyListener> _clusterListeners = new ArrayList<ClusterTopologyListener>();

    /**
     * Sets the {@link DiscoveryGroupConfiguration} to be used for UDP discovery of servers.
     * 
     * @param discoveryGroupConf the {@link DiscoveryGroupConfiguration} to use.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder discoveryGroupConfiguration(final DiscoveryGroupConfiguration discoveryGroupConf) {
        this._discoveryGroupConf = discoveryGroupConf;
        return this;
    }
    
    /**
     * Sets the {@link TransportConfiguration}s to be used to locate servers. This is a static configuration
     * of servers as opposed to using discovery.
     * 
     * @param configs the {@link TransportConfiguration} to use.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder transportConfigurations(final TransportConfiguration... configs) {
        this._transportConfigs = configs;
        return this;
    }
    
    /**
     * Determines whether High Availablility (clustering) is to be enabled.
     * 
     * @param useHA true if HA should be enabled for the ServerLocator to be built.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder useHA(final Boolean useHA) {
        if (useHA == null) {
            this._useHA = false;
        } else {
            this._useHA = useHA;
        }
        return this;
    }
    
    /**
     * Disables any checks when finalized method is called.
     * 
     * @param disable true if HA should be enabled for the ServerLocator to be built.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder disableFinalizedCheck(final Boolean disable) {
        this._disableFinalizedCheck = disable;
        return this;
    }
    
    /**
     * Configures the period in milliseconds. If the client has not received a ping from the server within
     * this period it will consider the connection failed.
     * 
     * @param period in ms.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder clientFailureCheckPeriod(final Long period) {
        this._clientFailureCheckPeriod = period;
        return this;
    }
    
    /**
     * Configures whether large messages should be cached by consumers.
     * 
     * @param cache if true large messages will be cached on the client.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder cacheLargeMessagesOnConsumers(final Boolean cache) {
        this._cacheLargeMessagesOnConsumers = cache;
        return this;
    }
    
    /**
     * Configures the time the server will hold a connection open without receiving any data
     * from the client.
     * 
     * @param connectionTTL time in miliseconds.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder connectionTTL(final Long connectionTTL) {
        this._connectionTTL = connectionTTL;
        return this;
    }

    /**
     * Will configure the blocking call timeout.
     * 
     * @param callTimeout blocking calltimout in ms.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder callTimeout(final Long callTimeout) {
        this._callTimeout = callTimeout;
        return this;
    }
    
    /**
     * Will configure the size of mesages that will be considered large messages. Any message 
     * larger then this size will be considered a large message to HornetQ.
     * 
     * @param size the min size of a message which is to be considered large
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder minLargeMessageSize(final Integer size) {
        this._minLargeMessageSize = size;
        return this;
    }
    
    /**
     * Will configure the client side message buffer size.
     * For slow consumers you can disable any client side buffering by setting this
     * value to 0.
     * 
     * @param size the size of the client side buffer. A value of 0 will not buffer at all.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder consumerWindowSize(final Integer size) {
        this._consumerWindowSize = size;
        return this;
    }
    
    /**
     * Will configure the fastest rate a consumer may consume messages per second.
     * 
     * @param rate the rate of messages per second.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder consumerMaxRate(final Integer rate) {
        this._consumerMaxRate = rate;
        return this;
    }
    
    /**
     * Will configure the size of the confirmation buffer for consumers.
     * 
     * @param size the size in bytes of the confiramation buffer.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder confirmationWindowSize(final Integer size) {
        this._confirmationWindowSize = size;
        return this;
    }
    
    /**
     * Will configure the amount of credits, in bytes, a producer will request from the
     * server in one go.
     * Producers can only send messages to an address as long as they have sufficient 
     * credits to do so.
     * 
     * @param size the size in bytes of that a producer will request.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder producerWindowSize(final Integer size) {
        this._producerWindowSize = size;
        return this;
    }
    
    /**
     * Will configure the fastest rate a producer may send messages per second.
     * 
     * @param rate the rate of messages per second.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder producerMaxRate(final Integer rate) {
        this._producerMaxRate = rate;
        return this;
    }
    
    /**
     * Configures whether consumers should block while sending acknowledges or if the
     * consumer should send them asynchronously.
     * 
     * @param block true if consumers should block.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder blockOnAcknowledge(final Boolean block) {
        this._blockOnAcknowledge = block;
        return this;
    }
    
    /**
     * Configures whether producers should block while sending durable messages or if the
     * consumer should send them asynchronously.
     * 
     * @param block true if producers should block.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder blockOnDurableSend(final Boolean block) {
        _blockOnDurableSend = block;
        return this;
    }
    
    /**
     * Configures whether producers should block while sending non-durable messages or if the
     * consumer should send them asynchronously.
     * 
     * @param block true if producers should block.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder blockOnNonDurableSend(final Boolean block) {
        _blockOnNonDurableSend = block;
        return this;
    }
    
    /**
     * Configures whether producers will automatically assign a group ID
     * to messages the producer sends.
     * </p>
     * For more information, please see "28.1 Using Core API" of the HornetQ User Manual.
     * 
     * @param autoGroup true if producers will automatically assign a group ID to outgoing messages.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder autoGroup(final Boolean autoGroup) {
        this._autoGroup = autoGroup;
        return this;
    }
    
    /**
     * Configures the group ID that producers will automatically set on
     * messages the producer sends.
     * </p>
     * For more information, please see "28.2 Using JMS" of the HornetQ User Manual.
     * 
     * @param id the group ID producers should set on messages.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder groupID(final String id) {
        _groupID = id;
        return this;
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
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder preAcknowledge(final Boolean preAck) {
        _preAcknowledge = preAck;
        return this;
    }
    
    /**
     * Configures the acknowledgement batch size for consumers. Consumers will then send
     * acknowledges in batches instead of sending them one-by-one.
     * 
     * @param size the batch size in bytes.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder ackBatchSize(final Integer size) {
        _ackBatchSize = size;
        return this;
    }
    
    /**
     * Configures whether clients should use a globally shared thread pool, a pool which 
     * is shared with others in the same JVM, or have its own thread pool.
     * 
     * @param useGlobalPools true if global thread pools should be use.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder useGlobalPools(final Boolean useGlobalPools) {
        _useGlobalPools = useGlobalPools;
        return this;
    }
    
    /**
     * Configures the size of the scheduled thread pool for this service locator. 
     * 
     * @param size the size of the scheduled thread pool.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder scheduledThreadPoolMaxSize(final Integer size) {
        _scheduledThreadPoolMaxSize = size;
        return this;
    }
    
    /**
     * Configures the size of the general thread pool for this service locator. 
     * 
     * @param size the size of the general thread pool.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder threadPoolMaxSize(final Integer size) {
        _threadPoolMaxSize = size;
        return this;
    }
    
    /**
     * Configures the time in ms to retry a connection after failing.
     * 
     * @param interval the interval in milliseconds.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder retryInterval(final Long interval) {
        _retryInterval = interval;
        return this;
    }
    
    /**
     * Configures the retry multiplier to apply to successive retry intervals.
     * 
     * @param multiplier the multiplier to apply.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder retryIntervalMultiplier(final Double multiplier) {
        _retryIntervalMultiplier = multiplier;
        return this;
    }
    
    /**
     * Configures the maimum retry interval.
     * 
     * @param max the maximum retry interval
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder maxRetryInterval(final Long max) {
        _maxRetryInterval = max;
        return this;
    }
    
    /**
     * Configures the max number of times to retry a connection upon failure.
     * 
     * @param attempts the number of times to retry.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder reconnectAttempts(final Integer attempts) {
        _reconnectAttempts = attempts;
        return this;
    }
    
    /**
     * Configures the max number of times to retry an initial connection to the live server.
     * 
     * @param attempts the number of times to retry.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder initialReconnectAttempts(final Integer attempts) {
        _initialConnectAttempts = attempts;
        return this;
    }
    
    /**
     * Configures whether the client will automatically attempt to connect to the backup
     * server if the initial connection attempt fails.
     * 
     * @param failover true if failover to the backup server should be performed.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder failoverOnInitialConnection(final Boolean failover) {
        _failoverOnInitialConnection = failover;
        return this;
    }
    
    /**
     * Configures connection load balancing policy to use. The class must implement
     * {@link ConnectionLoadBalancingPolicy}.
     * 
     * @param className the fully qualified name of the connection load balaning policy implementation.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder connectionLoadBalancingPolicyClassName(final String className) {
        _connectionLoadBalancingPolicyClassName = className;
        return this;
    }
    
    /**
     * Sets the initial size of messages created through this factory.
     * 
     * @param packetSize initial size of messages created through this factory.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder initialMessagePacketSize(final Integer packetSize) {
        _initialMessagePacketSize = packetSize;
        return this;
    }
    
    /**
     * Configures interceptors for this server locator.
     * 
     * @param interceptors one or more interceptors to add to this ServerLocator
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder addInterceptors(final Interceptor... interceptors) {
        _interceptors.addAll(Arrays.asList(interceptors));
        return this;
    }
    
    /**
     * Configures is compression of large messages should be performed.
     * 
     * @param compress if true, large messages will be compressed.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder compressLargeMessage(final Boolean compress) {
        _compressLargeMessage = compress;
        return this;
    }
    
    /**
     * Configures is {@link ClusterTopologyListener} to be added.
     * 
     * @param listeners the listeners to be added.
     * @return ServerLocatorBuilder to support method chaining.
     */
    public ServerLocatorBuilder addClusterTopologyListener(final ClusterTopologyListener... listeners) {
        _clusterListeners.addAll(Arrays.asList(listeners));
        return this;
    }
    
    /**
     * Configures and builds the ServerLocator.
     * 
     * @return ServerLocator which as all its properties set.
     */
    public ServerLocator build() {
        final ServerLocator serverLocator;
        
        if (_discoveryGroupConf != null) {
            serverLocator = new ServerLocatorImpl(_useHA, _discoveryGroupConf);
        } else if (_transportConfigs != null) {
            serverLocator = new ServerLocatorImpl(_useHA, _transportConfigs);
        } else {
            throw new IllegalArgumentException(
                    "To bulid a ServerLocator you must configure either a discoverygroup, which will use UDP " 
                    + "broadcast to discover the servers, or configure a static transport configuration." 
                    + "This can be done by calling builder.discoveryGroup() or builder.transportConfigurations()"
                    );
        }
        
        if (_disableFinalizedCheck != null && _disableFinalizedCheck) {
            serverLocator.disableFinalizeCheck();
        }
        if (_clientFailureCheckPeriod != null) {
            serverLocator.setClientFailureCheckPeriod(_clientFailureCheckPeriod);
        }
        if (_cacheLargeMessagesOnConsumers != null) {
            serverLocator.setCacheLargeMessagesClient(_cacheLargeMessagesOnConsumers);
        }
        if (_connectionTTL != null) {
            serverLocator.setConnectionTTL(_connectionTTL);
        }
        if (_callTimeout != null) {
            serverLocator.setCallTimeout(_callTimeout);
        }
        if (_minLargeMessageSize != null) {
            serverLocator.setMinLargeMessageSize(_minLargeMessageSize);
        }
        if (_consumerWindowSize != null) {
            serverLocator.setConsumerWindowSize(_consumerWindowSize);
        }
        if (_consumerMaxRate != null) {
            serverLocator.setConsumerMaxRate(_consumerMaxRate);
        }
        if (_confirmationWindowSize != null) {
            serverLocator.setConfirmationWindowSize(_confirmationWindowSize);
        }
        if (_producerWindowSize != null) {
            serverLocator.setProducerWindowSize(_producerWindowSize);
        }
        if (_producerMaxRate != null) {
            serverLocator.setProducerMaxRate(_producerMaxRate);
        }
        if (_blockOnAcknowledge != null) {
            serverLocator.setBlockOnAcknowledge(_blockOnAcknowledge);
        }
        if (_blockOnDurableSend != null) {
            serverLocator.setBlockOnDurableSend(_blockOnDurableSend);
        }
        if (_blockOnNonDurableSend != null) {
            serverLocator.setBlockOnNonDurableSend(_blockOnNonDurableSend);
        }
        if (_autoGroup != null) {
            serverLocator.setAutoGroup(_autoGroup);
        }
        if (_groupID != null) {
            serverLocator.setGroupID(_groupID);
        }
        if (_preAcknowledge != null) {
            serverLocator.setPreAcknowledge(_preAcknowledge);
        }
        if (_ackBatchSize != null) {
            serverLocator.setAckBatchSize(_ackBatchSize);
        }
        if (_useGlobalPools != null) {
            serverLocator.setUseGlobalPools(_useGlobalPools);
        }
        if (_scheduledThreadPoolMaxSize != null) {
            checkUseGlobalsIsTrue("scheduledThreadPoolMaxSize");
            serverLocator.setScheduledThreadPoolMaxSize(_scheduledThreadPoolMaxSize);
        }
        if (_threadPoolMaxSize != null) {
            checkUseGlobalsIsTrue("threadPoolMaxSize");
            serverLocator.setThreadPoolMaxSize(_threadPoolMaxSize);
        }
        if (_retryInterval != null) {
            serverLocator.setRetryInterval(_retryInterval);
        }
        if (_retryIntervalMultiplier != null) {
            serverLocator.setRetryIntervalMultiplier(_retryIntervalMultiplier);
        }
        if (_maxRetryInterval != null) {
            serverLocator.setMaxRetryInterval(_maxRetryInterval);
        }
        if (_reconnectAttempts != null) {
            serverLocator.setReconnectAttempts(_reconnectAttempts);
        }
        if (_initialConnectAttempts != null) {
            serverLocator.setInitialConnectAttempts(_initialConnectAttempts);
        }
        if (_failoverOnInitialConnection != null) {
            serverLocator.setFailoverOnInitialConnection(_failoverOnInitialConnection);
        }
        if (_connectionLoadBalancingPolicyClassName != null) {
            serverLocator.setConnectionLoadBalancingPolicyClassName(_connectionLoadBalancingPolicyClassName);
        }
        if (_initialMessagePacketSize != null) {
            serverLocator.setInitialMessagePacketSize(_initialMessagePacketSize);
        }
        for (Interceptor interceptor : _interceptors) {
            serverLocator.addInterceptor(interceptor);
        }
        if (_compressLargeMessage != null) {
            serverLocator.setCompressLargeMessage(_compressLargeMessage);
        }
        for (ClusterTopologyListener listener : _clusterListeners) {
            serverLocator.addClusterTopologyListener(listener);
            
        }
        
        return serverLocator;
    }

    private void checkUseGlobalsIsTrue(final String propertyName) throws IllegalArgumentException {
        if (_useGlobalPools == null || _useGlobalPools) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Configuring '").append(propertyName);
            sb.append("' does not make sense when 'useGlobalPools' is set to true (default).");
            sb.append("'").append(propertyName).append("' property controls the non-global pool size");
            sb.append("for this ServerLocator. If you want for this ServerLocator to have its own then ");
            sb.append("set 'useGlobalPools' to false");
            throw new IllegalArgumentException(sb.toString());
        }
    }
    
}
