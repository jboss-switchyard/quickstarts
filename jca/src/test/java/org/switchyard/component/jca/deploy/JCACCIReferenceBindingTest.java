/*
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
package org.switchyard.component.jca.deploy;

import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.Record;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.jca.InteractionListener;
import org.switchyard.component.test.mixins.jca.JCAMixIn;
import org.switchyard.component.test.mixins.jca.MockConnectionFactory;
import org.switchyard.component.test.mixins.jca.MockManagedConnectionFactory;
import org.switchyard.component.test.mixins.jca.ResourceAdapterConfig;

/**
 * Functional test for {@link JCAActivator}.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "switchyard-outbound-cci-test.xml", mixins = {CDIMixIn.class, JCAMixIn.class})
public class JCACCIReferenceBindingTest  {
    
    private static final String JNDI_CONNECTION_FACTORY = "java:jboss/MyEISConnectionFactory";
    private JCAMixIn _jcaMixIn;

    @ServiceOperation("JCACCIReferenceService.onMessage")
    private Invoker _service;
    
    @BeforeDeploy
    public void before() {
        ResourceAdapterConfig ra = new ResourceAdapterConfig(ResourceAdapterConfig.ResourceAdapterType.MOCK)
                                        .setName("myeis-ra.rar")
                                        .addConnectionDefinition(JNDI_CONNECTION_FACTORY, MockManagedConnectionFactory.class.getName());
        _jcaMixIn.deployResourceAdapters(ra);

        MockConnectionFactory factory = null;
        try {
            factory = (MockConnectionFactory) new InitialContext().lookup(JNDI_CONNECTION_FACTORY);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        factory.setInteractionListener(new InteractionListener() {
            public Record onExecute(InteractionSpec spec, Record input) {
                MappedRecord mapped = (MappedRecord) input;
                mapped.put("name", "Hello, " + mapped.get("name") + "!");
                return mapped;
            }
            public boolean onExecute(InteractionSpec spec, Record input, Record output) {return true;}
        });
    }

    @Test
    public void testOutboundCCI() throws Exception {
        Map<String,String> payload = new HashMap<String,String>();
        payload.put("name", "Uragasumi");
        @SuppressWarnings("unchecked")
        Map<String,String> out = _service.sendInOut(payload).getContent(Map.class);
        Assert.assertEquals("Hello, Uragasumi!", out.get("name"));
    }
}

