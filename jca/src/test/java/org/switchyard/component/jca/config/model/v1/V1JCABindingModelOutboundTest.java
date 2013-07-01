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
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Test for {@link V1JCABindingModel}.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class V1JCABindingModelOutboundTest {
    private JCABindingModel jbm;
    
    @Before
    public void parseJCABindingModel() throws IOException {
        final ModelPuller<SwitchYardModel> modelPuller = new ModelPuller<SwitchYardModel>();
        final URL xml = getClass().getResource("jca-outbound-binding.xml");
        final SwitchYardModel switchYardModel = modelPuller.pull(xml);
        jbm = (JCABindingModel) switchYardModel.getComposite().getReferences().get(0).getBindings().get(0);
    }
    
    @Test
    public void testOutboundConnection() {
        Assert.assertNotNull(jbm.getOutboundConnection());

        Assert.assertNotNull(jbm.getOutboundConnection().getResourceAdapter());
        Assert.assertEquals("hornetq-ra.rar", jbm.getOutboundConnection().getResourceAdapter().getName());
        Assert.assertEquals("value1", jbm.getOutboundConnection().getResourceAdapter().getProperty("prop1"));
        
        Assert.assertNotNull(jbm.getOutboundConnection().getConnection());
        Assert.assertEquals("value2", jbm.getOutboundConnection().getConnection().getProperty("prop2"));
    }
    
    @Test
    public void testOutboundInteraction() {
        Assert.assertNotNull(jbm.getOutboundInteraction());
        
        Assert.assertNotNull(jbm.getOutboundInteraction().getConnectionSpec());
        Assert.assertEquals("value3", jbm.getOutboundInteraction().getConnectionSpec().getProperty("prop3"));
        
        Assert.assertNotNull(jbm.getOutboundInteraction().getInteractionSpec());
        Assert.assertEquals("value4", jbm.getOutboundInteraction().getInteractionSpec().getProperty("prop4"));
        
        Assert.assertNotNull(jbm.getOutboundInteraction().getOperation());
        Assert.assertEquals("operationName", jbm.getOutboundInteraction().getOperation().getName());
        Assert.assertNotNull(jbm.getOutboundInteraction().getOperation().getInteractionSpec());
        Assert.assertEquals("value5", jbm.getOutboundInteraction().getOperation().getInteractionSpec().getProperty("prop5"));
        Assert.assertEquals("value6", jbm.getOutboundInteraction().getProcessor().getProperty("prop6"));
    }
}
