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
package org.switchyard.quickstarts.jca.inflow;

import javax.jms.BytesMessage;
import javax.jms.MessageProducer;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.mixins.CDIMixIn;
import org.switchyard.test.mixins.HornetQMixIn;
import org.switchyard.test.mixins.jca.JCAMixIn;
import org.switchyard.test.mixins.jca.JCAMixInConfig;

/**
 * Functional test for a SwitchYard Service which has a service binding to a HornetQ
 * queue via JCA inflow.
 * 
 * @author Daniel Bevenius
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = {CDIMixIn.class, HornetQMixIn.class, JCAMixIn.class},
        scanners = BeanSwitchYardScanner.class)
@JCAMixInConfig(hornetQResourceAdapter = "hornetq-ra.rar")
public class JCAInflowBindingTest {
    
    private SwitchYardTestKit _testKit;
    private HornetQMixIn _mixIn;
    
    /**
     * Triggers the 'GreetingService' by sending a HornetQ Message to the 'GreetingServiceQueue'
     */
    @Test
    public void triggerGreetingService() throws Exception {
        final String payload = "dummy payload";
        final MockHandler greetingService = _testKit.registerInOnlyService("GreetingService");
        
        final MessageProducer producer = _mixIn.getJMSSession().createProducer(HornetQMixIn.getJMSQueue("GreetingServiceQueue"));
        final BytesMessage message = _mixIn.getJMSSession().createBytesMessage();
        message.writeBytes(payload.getBytes());
        producer.send(message);
        
        Thread.sleep(1000);
        
        final Exchange recievedExchange = greetingService.getMessages().iterator().next();
        Assert.assertEquals(payload, new String(recievedExchange.getMessage().getContent(byte[].class)));
    }
    
}
