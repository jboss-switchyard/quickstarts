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
package org.switchyard.component.test.mixins.hornetq;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSession.BindingQuery;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.component.test.mixins.naming.NamingMixIn;

/**
 * Unit test for {@link HornetQMixIn}.
 * 
 * @author Daniel Bevenius
 *
 */
public class HornetQMixInTest {
    
    private static NamingMixIn namingMixIn;
    private static HornetQMixIn hornetQMixIn;
    
    @BeforeClass
    public static void setup() {
        namingMixIn = new NamingMixIn();
        namingMixIn.initialize();
        hornetQMixIn = new HornetQMixIn();
        hornetQMixIn.initialize();
    }
    
    @AfterClass
    public static void tearDown() {
        hornetQMixIn.uninitialize();
        namingMixIn.uninitialize();
    }

    @Test
    public void getClientSession() {
        final ClientSession clientSession = hornetQMixIn.getClientSession();
        Assert.assertNotNull(clientSession);
    }
    
    @Test
    public void createQueue() throws HornetQException {
        final ClientSession clientSession = hornetQMixIn.getClientSession();
        final SimpleString queueName = new SimpleString("testQueue");
        clientSession.createQueue(queueName, queueName);
        final BindingQuery bindingQuery = clientSession.bindingQuery(queueName);
        Assert.assertTrue(bindingQuery.isExists());
        Assert.assertEquals(1, bindingQuery.getQueueNames().size());
        final SimpleString actualQueueName = bindingQuery.getQueueNames().get(0);
        Assert.assertEquals(queueName, actualQueueName);
    }
    
}
