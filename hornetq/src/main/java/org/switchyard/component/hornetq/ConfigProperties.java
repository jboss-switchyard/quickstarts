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

/**
 * Configuration property constants for HornetQ.
 * 
 * @author Daniel Bevenius
 *
 */
public final class ConfigProperties {
    
    private ConfigProperties() {
    }
    
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String QUEUE = "queue";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String CONFIG_FILE = "hornetqConfig";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String USE_HA = "useHA";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String DISCOVERY_GROUP_NAME = "discoveryGroupName";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String TRANSPORT_CONFIG_NAME = "transportConfigName";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String DISABLE_FINALIZED_CHECK = "disableFinalizeCheck";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String CLIENT_FAILURE_CHECK_PERIOD = "clientFailureCheckPeriod";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String CACHE_LARGE_MESSAGES_ON_CONSUMER = "cacheLargeMessagesOnConsumer";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String CALL_TIMEOUT = "callTimeout";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String CONNECTION_TTL = "connectionTTL";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String MIN_LARGE_MESSAGE_SIZE = "minLargeMessageSize";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String CONSUMER_WINDOW_SIZE = "consumerWindowSize";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String CONSUMER_MAX_RATE = "consumerMaxRate";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String CONFIRMATION_WINDOW_SIZE = "confirmationWindowSize";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String PRODUCER_WINDOW_SIZE = "producerWindowSize";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String PRODUCER_MAX_RATE = "producerMaxRate";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String BLOCK_ON_ACKNOWLEDGE = "blockOnAcknowledge";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String BLOCK_ON_DURABLE_SEND = "blockOnDurableSend";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String BLOCK_ON_NON_DURABLE_SEND = "blockOnNonDurableSend";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String AUTO_GROUP = "autoGroup";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String GROUP_ID = "groupID";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String PRE_ACKNOWLEDGE = "preAcknowledge";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String ACK_BATCH_SIZE = "ackBatchSize";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String USE_GLOBAL_POOLS = "useGlobalPools";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String SCHEDULED_THREAD_POOL_MAX_SIZE = "scheduledThreadPoolMaxSize";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String THREAD_POOL_MAX_SIZE = "threadPoolMaxSize";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String RETRY_INTERVAL = "retryInterval";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String RETRY_INTERVAL_MULTIPLIER = "retryIntervalMultiplier";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String MAX_RETRY_INTERVAL = "maxRetryInterval";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String RECONNECT_ATTEMPTS = "reconnectAttempts";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String INITIAL_RECONNECT_ATTEMPTS = "initialReconnectAttempts";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String FAILOVER_ON_INITIAL_CONNECTION = "failoverOnInitialConnection";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String CONNECTION_LOAD_BALANCING_POLICY_CLASS_NAME = "connectionLoadBalancingPolicyClassName";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String INITIAL_MESSAGE_PACKET_SIZE = "initialMessagePacketSize";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String COMPRESS_LARGE_MESSAGES = "compressLargeMessages";
    /**
     * Name of property that can be used in different configuration sources.
     */
    public static final String XA_SESSION = "xaSession";
}
