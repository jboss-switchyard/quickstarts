/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.component.jca.config.model.v1;

import java.io.IOException;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.jca.config.model.JCABindingModel;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.selector.StaticOperationSelectorModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Test for {@link V1JCABindingModel}.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class V1JCABindingModelInboundTest {
    private JCABindingModel jbm;
    
    @Before
    public void parseJCABindingModel() throws IOException {
        final ModelPuller<SwitchYardModel> modelPuller = new ModelPuller<SwitchYardModel>();
        final URL xml = getClass().getResource("jca-inbound-binding.xml");
        final SwitchYardModel switchYardModel = modelPuller.pull(xml);
        jbm = (JCABindingModel) switchYardModel.getComposite().getServices().get(0).getBindings().get(0);
    }
    
    @Test
    public void testInboundConnection() {
        Assert.assertNotNull(jbm.getInboundConnection());

        Assert.assertNotNull(jbm.getInboundConnection().getResourceAdapter());
        Assert.assertEquals("hornetq-ra.rar", jbm.getInboundConnection().getResourceAdapter().getName());
        Assert.assertEquals("value1", jbm.getInboundConnection().getResourceAdapter().getProperty("prop1"));

        Assert.assertNotNull(jbm.getInboundConnection().getActivationSpec());
        Assert.assertEquals("javax.jms.Queue", jbm.getInboundConnection().getActivationSpec().getProperty("destinationType"));
        Assert.assertEquals("queue/TestQueue", jbm.getInboundConnection().getActivationSpec().getProperty("destination"));
    }
    
    @Test
    public void testInboundInteraction() {
        Assert.assertNotNull(jbm.getInboundInteraction());
        
        Assert.assertNotNull(jbm.getInboundInteraction().getListener());
        Assert.assertEquals("javax.jms.MessageListener", jbm.getInboundInteraction().getListener().getClassName());
        Assert.assertEquals("onMessage", ((StaticOperationSelectorModel)jbm.getOperationSelector()).getOperationName());
        Assert.assertEquals("org.switchyard.component.jca.endpoint.JMSEndpoint", jbm.getInboundInteraction().getEndpoint().getEndpointClassName());
        Assert.assertEquals("value2", jbm.getInboundInteraction().getEndpoint().getProperty("prop2"));
        Assert.assertEquals(true, jbm.getInboundInteraction().isTransacted());
        Assert.assertEquals(5, jbm.getInboundInteraction().getBatchCommit().getBatchSize());
        Assert.assertEquals(5000, jbm.getInboundInteraction().getBatchCommit().getBatchTimeout());
    }
    
}
