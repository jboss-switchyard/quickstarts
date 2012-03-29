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

import javax.jms.BytesMessage;
import javax.jms.MessageProducer;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.mixins.CDIMixIn;
import org.switchyard.test.mixins.HornetQMixIn;
import org.switchyard.test.mixins.jca.JCAMixIn;
import org.switchyard.test.mixins.jca.JCAMixInConfig;

/**
 * Functional test for {@link JCAActivator}.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "switchyard-inbound-jms-test.xml", mixins = {CDIMixIn.class, JCAMixIn.class, HornetQMixIn.class})
@JCAMixInConfig(hornetQResourceAdapter = "hornetq-ra.rar")
public class JCAJMSServiceBindingTest  {
    
    private static final String INPUT_QUEUE = "TestQueue";
    private SwitchYardTestKit _testKit;
    private HornetQMixIn _hqMixIn;
    
    @Test
    public void testInflowJMS() throws Exception {
        final MockHandler mockHandler = _testKit.registerInOutService("JCAJMSService");
        
        final MessageProducer producer = _hqMixIn.getJMSSession().createProducer(HornetQMixIn.getJMSQueue(INPUT_QUEUE));
        BytesMessage msg = _hqMixIn.getJMSSession().createBytesMessage();
        msg.writeBytes("payload".getBytes());
        producer.send(msg);
        
        // wait for message to be picked up the HornetQ queue.
        mockHandler.waitForOKMessage();
        
        Assert.assertEquals(mockHandler.getMessages().size(), 1);
        final Object content = mockHandler.getMessages().poll().getMessage().getContent();
        Assert.assertTrue(content instanceof byte[]);
        final String string = new String((byte[])content);
        Assert.assertEquals(string, "payload");
    }
}

