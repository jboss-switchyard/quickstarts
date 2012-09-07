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
package org.switchyard.component.hornetq.deploy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientProducer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.ServiceDomain;
import org.switchyard.component.hornetq.internal.HornetQUtil;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.mixins.CDIMixIn;
import org.switchyard.test.mixins.HornetQMixIn;

/**
 * Functional test for {@link HornetQActivator}.
 * 
 * @author Daniel Bevenius
 *
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "switchyard-service-binding-test.xml", mixins = {CDIMixIn.class, HornetQMixIn.class})
public class HornetQServiceBindingTest  {
    
    private static final String INPUT_QUEUE = "jms.queue.consumer";
    private SwitchYardTestKit _testKit;
    private HornetQMixIn _mixIn;
    private ServiceDomain _domain;
    
    @Before
    public void getHornetQMixIn() {
        _mixIn = _testKit.getMixIn(HornetQMixIn.class);
    }

    @Test
    public void servicebinding() throws HornetQException {
        // replace existing component service in config
        _testKit.removeService("HornetQService");
        final MockHandler mockHandler = _testKit.registerInOutService("HornetQService");
        final ClientProducer hornetQProducer = _mixIn.getClientSession().createProducer(INPUT_QUEUE);
        hornetQProducer.send(_mixIn.createMessage("payload"));
        
        // wait for message to be picked up the HornetQ queue.
        mockHandler.waitForOKMessage();
        
        assertThat(mockHandler.getMessages().size(), is(1));
        final Object content = mockHandler.getMessages().poll().getMessage().getContent();
        assertThat(content, is(instanceOf(byte[].class)));
        final String string = new String((byte[])content);
        assertThat(string, is(equalTo("payload")));
        HornetQUtil.closeClientProducer(hornetQProducer);
    }
}
