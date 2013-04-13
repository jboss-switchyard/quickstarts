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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hornetq.api.core.DiscoveryGroupConfiguration;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.Interceptor;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.UDPBroadcastGroupConfiguration;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.protocol.core.Packet;
import org.hornetq.spi.core.protocol.RemotingConnection;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link ServerLocatorBuilder}.
 * 
 * @author Daniel Bevenius
 *
 */
public class ServerLocatorBuilderTest {
    
    private ServerLocatorBuilder builder;

    @Before
    public void setup() {
        builder = new ServerLocatorBuilder();
        UDPBroadcastGroupConfiguration udpCfg = new UDPBroadcastGroupConfiguration("localhost", 10222, null, -1);
        builder.discoveryGroupConfiguration(new DiscoveryGroupConfiguration(HornetQClient.DEFAULT_DISCOVERY_REFRESH_TIMEOUT, HornetQClient.DEFAULT_DISCOVERY_INITIAL_WAIT_TIMEOUT, udpCfg));
    }

    @Test
    public void discoveryGroupConfiguration() {
        final ServerLocator locator = builder.build();
        assertThat(locator.isHA(), is(false));
    }
    
    @Test
    public void useHA() {
        final ServerLocator locator = builder.useHA(true).build();
        assertThat(locator.isHA(), is(true));
    }
    
    @Test
    public void transportConfigurations() {
        final TransportConfiguration[] transports = { 
                new TransportConfiguration("test.mock.TransportImpl"),
                new TransportConfiguration("test.mock.TransportImpl2")};
        final ServerLocator locator = new ServerLocatorBuilder().transportConfigurations(transports).build();
        final TransportConfiguration[] actualTransports = locator.getStaticTransportConfigurations();
        assertThat(actualTransports, is(transports));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIfNeitherDiscoveryGroupOrTransportConfigurationWasConfigured() {
        new ServerLocatorBuilder().build();
    }
    
    @Test
    public void callTimeout() {
        final ServerLocator locator = builder.callTimeout(1000l).build();
        assertThat(locator.getCallTimeout(), is(1000l));
    }
    
    @Test
    public void clientFailureCheckPeriod() {
        final ServerLocator locator = builder.clientFailureCheckPeriod(500l).build();
        assertThat(locator.getClientFailureCheckPeriod(), is(500l));
    }
    
    @Test
    public void cacheLargeMessagesClient() {
        final ServerLocator locator = builder.cacheLargeMessagesOnConsumers(true).build();
        assertThat(locator.isCacheLargeMessagesClient(), is(true));
    }
    
    @Test
    public void connectionTTL() {
        final ServerLocator locator = builder.connectionTTL(300l).build();
        assertThat(locator.getConnectionTTL(), is(300l));
    }
    
    @Test
    public void minLargeMessagesSize() {
        final ServerLocator locator = builder.minLargeMessageSize(100).build();
        assertThat(locator.getMinLargeMessageSize(), is(100));
    }
    
    @Test
    public void consumerWindowSize() {
        final ServerLocator locator = builder.consumerWindowSize(200).build();
        assertThat(locator.getConsumerWindowSize(), is(200));
    }
    
    @Test
    public void consumerMaxRate() {
        final ServerLocator locator = builder.consumerMaxRate(100).build();
        assertThat(locator.getConsumerMaxRate(), is(100));
    }
    
    @Test
    public void confirmationWindowSize() {
        final ServerLocator locator = builder.confirmationWindowSize(10).build();
        assertThat(locator.getConfirmationWindowSize(), is(10));
    }
    
    @Test
    public void producerWindowSize() {
        final ServerLocator locator = builder.producerWindowSize(145).build();
        assertThat(locator.getProducerWindowSize(), is(145));
    }
    
    @Test
    public void producerMaxRate() {
        final ServerLocator locator = builder.producerMaxRate(133).build();
        assertThat(locator.getProducerMaxRate(), is(133));
    }
    
    @Test
    public void blockOnAcknowledge() {
        final ServerLocator locator = builder.blockOnAcknowledge(true).build();
        assertThat(locator.isBlockOnAcknowledge(), is(true));
    }
    
    @Test
    public void blockOnDurableSend() {
        final ServerLocator locator = builder.blockOnDurableSend(false).build();
        assertThat(locator.isBlockOnDurableSend(), is(false));
    }
    
    @Test
    public void blockOnNonDurableSend() {
        final ServerLocator locator = builder.blockOnNonDurableSend(true).build();
        assertThat(locator.isBlockOnNonDurableSend(), is(true));
    }
    
    @Test
    public void autoGroup() {
        final ServerLocator locator = builder.autoGroup(true).build();
        assertThat(locator.isAutoGroup(), is(true));
    }
    
    @Test
    public void groupID() {
        final String groupID = "dummyGroupID";
        final ServerLocator locator = builder.groupID(groupID).build();
        assertThat(locator.getGroupID(), is(equalTo(groupID)));
    }
    
    @Test
    public void preAcknowledge() {
        final ServerLocator locator = builder.preAcknowledge(true).build();
        assertThat(locator.isPreAcknowledge(), is(true));
    }
    
    @Test
    public void ackBatchSize() {
        final ServerLocator locator = builder.ackBatchSize(100).build();
        assertThat(locator.getAckBatchSize(), is(100));
    }
    
    @Test
    public void useGlobalPools () {
        final ServerLocator locator = builder.useGlobalPools(false).build();
        assertThat(locator.isUseGlobalPools(), is(false));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void scheduledThreadPoolMaxSizeShouldThrowIfUseGlobalPoolsIsTrue() {
        builder.scheduledThreadPoolMaxSize(10).build();
    }
    
    @Test
    public void scheduledThreadPoolMaxSize() {
        final ServerLocator locator = builder.useGlobalPools(false).scheduledThreadPoolMaxSize(10).build();
        assertThat(locator.getScheduledThreadPoolMaxSize(), is(10));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void threadPoolMaxSizeShouldThrowIfUseGlobalPoolsIsTrue() {
        final ServerLocator locator = builder.threadPoolMaxSize(3).build();
        assertThat(locator.getThreadPoolMaxSize(), is(3));
    }
    
    @Test
    public void threadPoolMaxSize() {
        final ServerLocator locator = builder.useGlobalPools(false).threadPoolMaxSize(3).build();
        assertThat(locator.getThreadPoolMaxSize(), is(3));
    }
    
    @Test
    public void retryInterval() {
        final ServerLocator locator = builder.retryInterval(5000l).build();
        assertThat(locator.getRetryInterval(), is(5000l));
    }
    
    @Test
    public void retryIntervalMultiplier() {
        final ServerLocator locator = builder.retryIntervalMultiplier(2.0).build();
        assertThat(locator.getRetryIntervalMultiplier(), is(2.0));
    }
    
    @Test
    public void maxRetryInterval() {
        final ServerLocator locator = builder.maxRetryInterval(100l).build();
        assertThat(locator.getMaxRetryInterval(), is(100l));
    }
    
    @Test
    public void reconnectAttempts() {
        final ServerLocator locator = builder.reconnectAttempts(3).build();
        assertThat(locator.getReconnectAttempts(), is(3));
    }
    
    @Test
    public void initialConnectAttempts() {
        final ServerLocator locator = builder.initialReconnectAttempts(3).build();
        assertThat(locator.getInitialConnectAttempts(), is(3));
    }
    
    @Test
    public void failOverOnInitialConnection() {
        final ServerLocator locator = builder.failoverOnInitialConnection(true).build();
        assertThat(locator.isFailoverOnInitialConnection(), is(true));
    }
    
    @Test
    public void connectionLoadBalancingPolicyClassName() {
        final ServerLocator locator = builder.connectionLoadBalancingPolicyClassName("someName").build();
        assertThat(locator.getConnectionLoadBalancingPolicyClassName(), is("someName"));
    }
    
    @Test
    public void initialMessagePacketSize() {
        final ServerLocator locator = builder.initialMessagePacketSize(2000).build();
        assertThat(locator.getInitialMessagePacketSize(), is(2000));
    }
    
    @Test
    public void addInterceptor() {
        final Interceptor interceptor = new Interceptor() {
            @Override
            public boolean intercept(Packet packet, RemotingConnection connection) throws HornetQException {
                return false;
            }
        };
        final ServerLocator locator = builder.addInterceptors(interceptor).build();
        assertThat(locator.removeInterceptor(interceptor), is(true));
    }
    
    @Test
    public void compressLargeMessage() {
        final ServerLocator locator = builder.compressLargeMessage(true).build();
        assertThat(locator.isCompressLargeMessage(), is(true));
    }
    
    @Test
    public void defaultValues() {
        final ServerLocator locator = builder.build();
        assertThat(locator.getCallTimeout(), is(HornetQClient.DEFAULT_CALL_TIMEOUT));
        assertThat(locator.getClientFailureCheckPeriod(), is(HornetQClient.DEFAULT_CLIENT_FAILURE_CHECK_PERIOD));
        assertThat(locator.isCacheLargeMessagesClient(), is(HornetQClient.DEFAULT_CACHE_LARGE_MESSAGE_CLIENT));
        assertThat(locator.getConnectionTTL(), is(HornetQClient.DEFAULT_CONNECTION_TTL));
        assertThat(locator.getMinLargeMessageSize(), is(HornetQClient.DEFAULT_MIN_LARGE_MESSAGE_SIZE));
        assertThat(locator.getConsumerWindowSize(), is(HornetQClient.DEFAULT_CONSUMER_WINDOW_SIZE));
        assertThat(locator.getConsumerMaxRate(), is(HornetQClient.DEFAULT_CONSUMER_MAX_RATE));
        assertThat(locator.getConfirmationWindowSize(), is(HornetQClient.DEFAULT_CONFIRMATION_WINDOW_SIZE));
        assertThat(locator.getProducerWindowSize(), is(HornetQClient.DEFAULT_PRODUCER_WINDOW_SIZE));
        assertThat(locator.getProducerMaxRate(), is(HornetQClient.DEFAULT_PRODUCER_MAX_RATE));
        assertThat(locator.isBlockOnAcknowledge(), is(HornetQClient.DEFAULT_BLOCK_ON_ACKNOWLEDGE));
        assertThat(locator.isBlockOnDurableSend(), is(HornetQClient.DEFAULT_BLOCK_ON_DURABLE_SEND));
        assertThat(locator.isBlockOnNonDurableSend(), is(HornetQClient.DEFAULT_BLOCK_ON_NON_DURABLE_SEND));
        assertThat(locator.isAutoGroup(), is(HornetQClient.DEFAULT_AUTO_GROUP));
        assertThat(locator.getGroupID(), is(nullValue()));
        assertThat(locator.isPreAcknowledge(), is(HornetQClient.DEFAULT_PRE_ACKNOWLEDGE));
        assertThat(locator.getAckBatchSize(), is(HornetQClient.DEFAULT_ACK_BATCH_SIZE));
        assertThat(locator.isUseGlobalPools(), is(HornetQClient.DEFAULT_USE_GLOBAL_POOLS));
        assertThat(locator.getScheduledThreadPoolMaxSize(), is(HornetQClient.DEFAULT_SCHEDULED_THREAD_POOL_MAX_SIZE));
        assertThat(locator.getThreadPoolMaxSize(), is(HornetQClient.DEFAULT_THREAD_POOL_MAX_SIZE));
        assertThat(locator.getRetryInterval(), is(HornetQClient.DEFAULT_RETRY_INTERVAL));
        assertThat(locator.getRetryIntervalMultiplier(), is(HornetQClient.DEFAULT_RETRY_INTERVAL_MULTIPLIER));
        assertThat(locator.getMaxRetryInterval(), is(HornetQClient.DEFAULT_MAX_RETRY_INTERVAL));
        assertThat(locator.getReconnectAttempts(), is(HornetQClient.DEFAULT_RECONNECT_ATTEMPTS));
        assertThat(locator.getInitialConnectAttempts(), is(HornetQClient.INITIAL_CONNECT_ATTEMPTS));
        assertThat(locator.isFailoverOnInitialConnection(), is(HornetQClient.DEFAULT_FAILOVER_ON_INITIAL_CONNECTION));
        assertThat(locator.getConnectionLoadBalancingPolicyClassName(), is(HornetQClient.DEFAULT_CONNECTION_LOAD_BALANCING_POLICY_CLASS_NAME));
        assertThat(locator.getInitialMessagePacketSize(), is(HornetQClient.DEFAULT_INITIAL_MESSAGE_PACKET_SIZE));
        assertThat(locator.isCompressLargeMessage(), is(HornetQClient.DEFAULT_COMPRESS_LARGE_MESSAGES));
    }
    
}
