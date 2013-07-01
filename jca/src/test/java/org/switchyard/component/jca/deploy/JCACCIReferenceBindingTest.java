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
package org.switchyard.component.jca.deploy;

import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.Record;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.jca.InteractionListener;
import org.switchyard.component.test.mixins.jca.JCAMixIn;
import org.switchyard.component.test.mixins.jca.MockConnectionFactory;
import org.switchyard.component.test.mixins.jca.MockManagedConnectionFactory;
import org.switchyard.component.test.mixins.jca.ResourceAdapterConfig;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

/**
 * Functional test for {@link JCAActivator}.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "switchyard-outbound-cci-test.xml", mixins = {JCAMixIn.class, CDIMixIn.class})
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

