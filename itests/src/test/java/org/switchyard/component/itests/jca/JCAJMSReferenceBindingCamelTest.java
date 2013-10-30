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
package org.switchyard.component.itests.jca;

import javax.jms.MessageConsumer;
import javax.transaction.UserTransaction;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;
import org.switchyard.component.test.mixins.jca.JCAMixIn;
import org.switchyard.component.test.mixins.jca.ResourceAdapterConfig;

/**
 * Functional test for {@link JCAActivator}.
 * 
 */
@Ignore
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "switchyard-outbound-jms-camel-test.xml", mixins = {CDIMixIn.class, HornetQMixIn.class, JCAMixIn.class})
public class JCAJMSReferenceBindingCamelTest  {
    
    private static final String OUTPUT_QUEUE = "TestQueue";
    private HornetQMixIn _hqMixIn;
    private JCAMixIn _jcaMixIn;
    
    @ServiceOperation("JCAJMSReferenceService.onMessage")
    private Invoker _service;

    @BeforeDeploy
    public void before() {
        ResourceAdapterConfig ra = new ResourceAdapterConfig(ResourceAdapterConfig.ResourceAdapterType.HORNETQ);
        _jcaMixIn.deployResourceAdapters(ra);
    }
    
    @Test
    public void testManagedOutboundJMS() throws Exception {
        String payload = "Ashihiki";
        UserTransaction tx = _jcaMixIn.getUserTransaction();
        tx.begin();
        _service.sendInOnly(payload);
        
        final MessageConsumer consumer = _hqMixIn.getJMSSession().createConsumer(HornetQMixIn.getJMSQueue(OUTPUT_QUEUE));
        Assert.assertNull(consumer.receive(1000));
        
        tx.commit();
        
        javax.jms.Message msg = consumer.receive(1000);
        _hqMixIn.readJMSMessageAndTestString(msg, payload);
        Assert.assertEquals("jmscorrelation-Ashihiki", msg.getJMSCorrelationID());
    }
}

