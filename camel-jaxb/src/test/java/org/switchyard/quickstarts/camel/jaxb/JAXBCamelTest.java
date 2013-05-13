/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.quickstarts.camel.jaxb;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

/**
 * Tests for Camel jaxb binding.
 */
@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML, 
    mixins = {HTTPMixIn.class}
)
@RunWith(SwitchYardRunner.class)
public class JAXBCamelTest {

    private static final String BASE_URL = "http://localhost:8080/camel-binding";

    private HTTPMixIn http;

    @Test
    public void httpJAXBCamelEndpoint() throws Exception {
        GreetingRequest request = new GreetingRequest("Magesh");

        String response = http.sendString(BASE_URL, JAXBUtil.marshal(request), HTTPMixIn.HTTP_POST);

        GreetingResponse rsp = new GreetingResponse("Ola Magesh!");
        Assert.assertEquals(rsp, JAXBUtil.unmarshal(response));
    }

}
