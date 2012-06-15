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
