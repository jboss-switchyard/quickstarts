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

import javax.jms.MessageConsumer;
import javax.transaction.UserTransaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;
import org.switchyard.test.mixins.HornetQMixIn;
import org.switchyard.test.mixins.jca.JCAMixIn;
import org.switchyard.test.mixins.jca.ResourceAdapterConfig;

/**
 * Functional test for {@link JCAActivator}.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "switchyard-outbound-jms-test.xml", mixins = {CDIMixIn.class, HornetQMixIn.class, JCAMixIn.class})
public class JCAJMSReferenceBindingTest  {
    
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
    public void testOutboundJMS() throws Exception {
        String payload = "Ashihiki";
        UserTransaction tx = _jcaMixIn.getUserTransaction();
        tx.begin();
        _service.sendInOnly(payload);
        tx.commit();
        
        final MessageConsumer consumer = _hqMixIn.getJMSSession().createConsumer(HornetQMixIn.getJMSQueue(OUTPUT_QUEUE));
        _hqMixIn.readJMSMessageAndTestString(consumer.receive(1000), payload);
    }
}

