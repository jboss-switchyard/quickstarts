/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2010-2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
 * details. You should have received a copy of the GNU Lesser General Public 
 * License, v.2.1 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301, USA.
 */

package org.switchyard.quickstarts.hornetq;

import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;
import org.switchyard.test.mixins.HTTPMixIn;
import org.switchyard.test.mixins.HornetQMixIn;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        scanners = {BeanSwitchYardScanner.class, TransformSwitchYardScanner.class},
        mixins = {CDIMixIn.class, HTTPMixIn.class, HornetQMixIn.class})
public class WebServiceTest {

    private HTTPMixIn httpMixIn;
    private HornetQMixIn hqMixIn;

    @Test
    public void invokeGreetingWebService() throws Exception {
        // Use the HttpMixIn to invoke the SOAP binding endpoint with a SOAP input (from the test classpath)
        // and compare the SOAP response to a SOAP response resource (from the test classpath)...
        httpMixIn.postResource("http://localhost:18001/quickstart-hornetq-reference-binding/GreetingService", "/xml/soap-request.xml");
        ClientConsumer consumer = hqMixIn.getClientSession().createConsumer("jms.queue.GreetingServiceQueue");
        ClientMessage message = consumer.receive(300);
        Assert.assertTrue(message != null);
        byte[] bytea = new byte[message.getBodySize()];
        message.getBodyBuffer().readBytes(bytea);
        Assert.assertEquals("Hello there Tomo :-) ", new String(bytea));
    }
}

