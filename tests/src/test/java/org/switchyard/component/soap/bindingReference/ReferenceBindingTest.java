/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.switchyard.component.soap.bindingReference;

import javax.xml.ws.Endpoint;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;
import org.w3c.dom.Element;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class, config = "/soap.bindingReference/switchyard.xml")
public class ReferenceBindingTest {

    @ServiceOperation("DummySOAPServiceService.testOperation")
    private Invoker _testOperation;

    private static final String NAMESPACE_URI = 
        "http://bindingReference.soap.component.switchyard.org/";
    
    private Endpoint _endpoint;
    
    @Before
    public void setUp() throws Exception {
        String host = System.getProperty("org.switchyard.test.soap.host", "localhost");
        String port = System.getProperty("org.switchyard.test.soap.port", "48080");
        String serviceURL = "http://" + host + ":" + port + "/ReferenceBindingTest";
        
        _endpoint = Endpoint.publish(serviceURL, new DummySOAPService());
    }
    
    @After
    public void tearDown() throws Exception {
        _endpoint.stop();
    }

    @Test
    public void testReferenceBinding() throws Exception {
        final String requestString = "Hello!";
        final String requestMessage = 
            "<foo:testOperation xmlns:foo=\"" + NAMESPACE_URI + "\">" +
            "<arg0>" + requestString + "</arg0>" +
            "</foo:testOperation>";
        
        Element replyMessage = _testOperation
            .sendInOut(requestMessage)
            .getContent(Element.class);
         
        Assert.assertTrue(replyMessage.getElementsByTagName("return").getLength() > 0);
    }
}
