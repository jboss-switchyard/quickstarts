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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.component.hornetq.config.model.HornetQBindingModel;
import org.switchyard.component.hornetq.config.model.HornetQConfigModel;
import org.switchyard.component.hornetq.config.model.HornetQConnectorConfigModel;
import org.switchyard.component.hornetq.config.model.HornetQDiscoveryGroupConfigModel;
import org.switchyard.config.OutputKey;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.Validation;
import org.switchyard.config.model.domain.PropertiesModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Unit test for {@link V1HornetQBindingModel}.
 * 
 * @author Daniel Bevenius
 *
 */
public class V1HornetQConfigModelTest {
    
    private static HornetQConfigModel completeConfigModel;
    private static HornetQConfigModel emptyConfigModel;
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @BeforeClass
    public static void readHornetQBindings() throws IOException {
        completeConfigModel = pull("hornetq-all-binding.xml");
        emptyConfigModel = new V1HornetQConfigModel();
    }
    
    private static HornetQConfigModel pull(final String fileName) throws IOException {
        final ModelPuller<SwitchYardModel> modelPuller = new ModelPuller<SwitchYardModel>();
        final URL xml = V1HornetQConfigModelTest.class.getResource(fileName);
        final SwitchYardModel switchYardModel = modelPuller.pull(xml);
        final HornetQBindingModel hbm = (HornetQBindingModel) switchYardModel.getComposite().getServices().get(0).getBindings().get(0);
        return hbm.getHornetQConfig();
    }
    
    @Test
    public void queue() {
        assertThat(completeConfigModel.getQueue(), is(equalTo("testQueue")));
        assertThat(emptyConfigModel.getQueue(), is(nullValue()));
    }
    
    @Test
    public void connectorConfiguration() {
        final HornetQConnectorConfigModel connector = completeConfigModel.getConnectorConfiguration();
        assertThat(connector, is(notNullValue()));
        assertThat(connector.getConnectorName(), is(equalTo("testConnector")));
        assertThat(connector.getConnectorClassName(), is(equalTo("org.test.Connector")));
        final PropertiesModel properties = connector.getProperties();
        assertThat(properties, is((notNullValue())));
        assertThat(properties.getProperty("testConnectorProp1").getName(), is(equalTo("testConnectorProp1")));
        assertThat(properties.getProperty("testConnectorProp1").getValue(), is(equalTo("testConnectorValue1")));
        assertThat(properties.getProperty("testConnectorProp2").getName(), is(equalTo("testConnectorProp2")));
        assertThat(properties.getProperty("testConnectorProp2").getValue(), is(equalTo("testConnectorValue2")));
        assertThat(emptyConfigModel.getConnectorConfiguration(), is(nullValue()));
    }

    @Test
    public void isHA() {
        assertThat(completeConfigModel.isUseHA(), is(true));
        assertThat(emptyConfigModel.isUseHA(), is(nullValue()));
    }
    
    @Test
    public void discoveryGroup() {
        final HornetQDiscoveryGroupConfigModel discoveryGroup = completeConfigModel.getDiscoveryGroup();
        assertThat(discoveryGroup.getLocalBindAddress(), is(equalTo("localhost")));
        assertThat(discoveryGroup.getGroupAddress(), is(equalTo("localhost")));
        assertThat(discoveryGroup.getGroupPort(), is(10203));
        assertThat(discoveryGroup.getRefreshTimeout(), is(500l));
        assertThat(discoveryGroup.getInitialWaitTimeout(), is(5000l));
    }
    
    @Test
    public void disableFinalizedCheck() {
        assertThat(completeConfigModel.isDisableFinalizeCheck(), is(true));
        assertThat(emptyConfigModel.isDisableFinalizeCheck(), is(nullValue()));
    }
    
    @Test
    public void clientFailureCheckPeriod() {
        assertThat(completeConfigModel.getClientFailureCheckPeriod(), is(2050l));
        assertThat(emptyConfigModel.getClientFailureCheckPeriod(), is(nullValue()));
    }
    
    @Test
    public void cacheLargeMessagesOnConsumer() {
        assertThat(completeConfigModel.isCacheLargeMessagesClient(), is(true));
        assertThat(emptyConfigModel.isCacheLargeMessagesClient(), is(nullValue()));
    }
    
    @Test
    public void callTimeout() {
        assertThat(completeConfigModel.getCallTimeout(), is(3000l));
        assertThat(emptyConfigModel.getCallTimeout(), is(nullValue()));
    }
    
    @Test
    public void connectionTTL() {
        assertThat(completeConfigModel.getConnectionTTL(), is(1000l));
        assertThat(emptyConfigModel.getConnectionTTL(), is(nullValue()));
    }
    
    @Test
    public void minLargeMessageSize() {
        assertThat(completeConfigModel.getMinLargeMessageSize(), is(5000));
        assertThat(emptyConfigModel.getMinLargeMessageSize(), is(nullValue()));
    }
    
    @Test
    public void consumerWindowSize() {
        assertThat(completeConfigModel.getConsumerWindowSize(), is(2000));
        assertThat(emptyConfigModel.getConsumerWindowSize(), is(nullValue()));
    }
    
    @Test
    public void consumerMaxRate() {
        assertThat(completeConfigModel.getConsumerMaxRate(), is(10));
        assertThat(emptyConfigModel.getConsumerMaxRate(), is(nullValue()));
    }
    
    @Test
    public void confirmationWindowSize() {
        assertThat(completeConfigModel.getConfirmationWindowSize(), is(2));
        assertThat(emptyConfigModel.getConfirmationWindowSize(), is(nullValue()));
    }
    
    @Test
    public void producerWindowSize() {
        assertThat(completeConfigModel.getProducerWindowSize(), is(100));
        assertThat(emptyConfigModel.getProducerWindowSize(), is(nullValue()));
    }
    
    @Test
    public void producerMaxRate() {
        assertThat(completeConfigModel.getProducerMaxRate(), is(20));
        assertThat(emptyConfigModel.getProducerMaxRate(), is(nullValue()));
    }
    
    @Test
    public void blockOnAcknowledge() {
        assertThat(completeConfigModel.isBlockOnAcknowledge(), is(true));
        assertThat(emptyConfigModel.isBlockOnAcknowledge(), is(nullValue()));
    }
    
    @Test
    public void blockOnDurableSend() {
        assertThat(completeConfigModel.isBlockOnDurableSend(), is(false));
        assertThat(emptyConfigModel.isBlockOnDurableSend(), is(nullValue()));
    }
    
    @Test
    public void blockOnNonDurableSend() {
        assertThat(completeConfigModel.isBlockOnNonDurableSend(), is(true));
        assertThat(emptyConfigModel.isBlockOnNonDurableSend(), is(nullValue()));
    }
    
    @Test
    public void autoGroup() {
        assertThat(completeConfigModel.isAutoGroup(), is(true));
        assertThat(emptyConfigModel.isAutoGroup(), is(nullValue()));
    }
    
    @Test
    public void groupID() {
        assertThat(completeConfigModel.getGroupID(), is(equalTo("testGroup")));
        assertThat(emptyConfigModel.getGroupID(), is(nullValue()));
    }
    
    @Test
    public void preAcknowledge() {
        assertThat(completeConfigModel.isPreAcknowledge(), is(true));
        assertThat(emptyConfigModel.isPreAcknowledge(), is(nullValue()));
    }
    
    @Test
    public void ackBatchSize() {
        assertThat(completeConfigModel.getAckBatchSize(), is(200));
        assertThat(emptyConfigModel.getAckBatchSize(), is(nullValue()));
    }
    
    @Test
    public void useGlobalPools() {
        assertThat(completeConfigModel.isUseGlobalPools(), is(true));
        assertThat(emptyConfigModel.isUseGlobalPools(), is(nullValue()));
    }
    
    @Test
    public void scheduledThreadPoolMaxSize() {
        assertThat(completeConfigModel.getScheduledThreadPoolMaxSize(), is(10));
        assertThat(emptyConfigModel.getScheduledThreadPoolMaxSize(), is(nullValue()));
    }
    
    @Test
    public void threadPoolMaxSize() {
        assertThat(completeConfigModel.getThreadPoolMaxSize(), is(5));
        assertThat(emptyConfigModel.getThreadPoolMaxSize(), is(nullValue()));
    }
    
    @Test
    public void retryInterval() {
        assertThat(completeConfigModel.getRetryInterval(), is(5000l));
        assertThat(emptyConfigModel.getRetryInterval(), is(nullValue()));
    }
    
    @Test
    public void retryInternvalMultiplier() {
        assertThat(completeConfigModel.getRetryIntervalMultiplier(), is(500d));
        assertThat(emptyConfigModel.getRetryIntervalMultiplier(), is(nullValue()));
    }
    
    @Test
    public void maxRetryInterval() {
        assertThat(completeConfigModel.getMaxRetryInterval(), is(500l));
        assertThat(emptyConfigModel.getMaxRetryInterval(), is(nullValue()));
    }
    
    @Test
    public void reconnectAttempts() {
        assertThat(completeConfigModel.getReconnectAttempts(), is(2));
        assertThat(emptyConfigModel.getReconnectAttempts(), is(nullValue()));
    }
    
    @Test
    public void initialReconnectAttempts() {
        assertThat(completeConfigModel.getInitialConnectAttempts(), is(3));
        assertThat(emptyConfigModel.getInitialConnectAttempts(), is(nullValue()));
    }
    
    @Test
    public void failoverOnInitialConnection() {
        assertThat(completeConfigModel.isFailoverOnInitialConnection(), is(false));
        assertThat(emptyConfigModel.isFailoverOnInitialConnection(), is(nullValue()));
    }
    
    @Test
    public void connectionLoadBalancingPolicyClassName() {
        assertThat(completeConfigModel.getConnectionLoadBalancingPolicyClassName(), is(equalTo("Mock")));
        assertThat(emptyConfigModel.getConnectionLoadBalancingPolicyClassName(), is(nullValue()));
    }
    
    @Test
    public void initialMessagePacketSize() {
        assertThat(completeConfigModel.getInitialMessagePacketSize(), is(2500));
        assertThat(emptyConfigModel.getInitialMessagePacketSize(), is(nullValue()));
    }
    
    @Test
    public void compressLargeMessage() {
        assertThat(completeConfigModel.isCompressLargeMessage(), is(true));
        assertThat(emptyConfigModel.isCompressLargeMessage(), is(nullValue()));
    }
    
    @Test
    public void xaSession() {
        assertThat(completeConfigModel.isXASession(), is(true));
        assertThat(emptyConfigModel.isXASession(), is(false));
    }
    
    @Test
    public void validateModel() throws IOException {
        final HornetQConfigModel bindingModel = pull("hornetq-all-binding.xml");
        final Validation validation = bindingModel.validateModel();
        assertThat(validation.isValid(), is(true));
    }
    
    @Test
    public void programmaticCreation() throws Exception {
        final V1HornetQConfigModel model = new V1HornetQConfigModel();
        model.setCompressLargeMessage(true).setAutoGroup(true).setUseHA(true).setQueue("someQueue");
        model.orderModelChildren(); // force the model to be ordered
        final Validation validation = model.validateModel();
        assertThat(validation.isValid(), is(true));
    }
        
    @Test
    public void writeReadModel() throws Exception {
        final V1HornetQConfigModel model = new V1HornetQConfigModel();
        model.setUseGlobalPools(false).setAutoGroup(true).setQueue("queue");
        final String xml = model.toString();
        final File savedModel = folder.newFile("hornetq-model.xml");
        final FileWriter fileWriter = new FileWriter(savedModel);
        model.write(fileWriter, OutputKey.OMIT_XML_DECLARATION, OutputKey.PRETTY_PRINT);
        
        final String xmlFromFile = new StringPuller().pull(savedModel);
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(xml, xmlFromFile);
    }
}
