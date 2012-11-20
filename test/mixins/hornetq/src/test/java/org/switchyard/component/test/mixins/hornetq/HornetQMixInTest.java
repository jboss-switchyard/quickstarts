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
