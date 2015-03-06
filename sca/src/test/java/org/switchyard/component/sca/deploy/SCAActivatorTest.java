package org.switchyard.component.sca.deploy;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.sca.NOPEndpointPublisher;
import org.switchyard.config.model.composite.SCABindingModel;
import org.switchyard.config.model.composite.v1.V1SCABindingModel;
import org.switchyard.config.model.switchyard.SwitchYardNamespace;

public class SCAActivatorTest {
    private SCAActivator activator;
    
    @Before
    public void setUp() throws Exception {
        activator = new SCAActivator(null);
    }
    
    @Test
    public void testBindingActivationWithNullTargetAndNamespace() throws Exception {
        activator.setEndpointPublisher(new NOPEndpointPublisher());
        
        SCABindingModel scab = new V1SCABindingModel(SwitchYardNamespace.DEFAULT.uri());
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
