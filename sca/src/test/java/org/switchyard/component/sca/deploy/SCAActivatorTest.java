package org.switchyard.component.sca.deploy;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.sca.NOPEndpointPublisher;
import org.switchyard.config.ConfigurationPuller;
import org.switchyard.config.model.composite.SCABindingModel;
import org.switchyard.config.model.composite.v1.V1SCABindingModel;

public class SCAActivatorTest {
    private static final String TEST_XML = "TestConfig.xml";
    private ConfigurationPuller _cfg_puller;
    private SCAActivator activator;
    
    @Before
    public void setUp() throws Exception {
        _cfg_puller = new ConfigurationPuller(); 
        activator = new SCAActivator(_cfg_puller.pull(TEST_XML, getClass()));
    }
    
    @After
    public void after() {
        _cfg_puller = null;
    }
    
    @Test
    public void testBindingActivationWithNullTargetAndNamespace() throws Exception {        
        activator.setEndpointPublisher(new NOPEndpointPublisher());
        
        SCABindingModel scab = new V1SCABindingModel();
        scab.setClustered(true)
            .setLoadBalance("RoundRobin")
            .setTarget(null)
            .setTargetNamespace(null);
        
        try {
            activator.activateBinding(new QName("urn:test","one"), scab);
            
            Assert.fail("No IllegalArgumentException has been thrown");
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            Assert.assertTrue(message.contains("SWITCHYARD039600"));
        }
    }
}
