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

package org.switchyard.quickstarts.m1app;

import org.junit.Test;
import org.switchyard.test.SwitchYardTestCase;
import org.switchyard.test.SwitchYardDeploymentConfig;
import org.switchyard.test.TestMixIns;
import org.switchyard.test.mixins.CDIMixIn;
import org.switchyard.test.mixins.HTTPMixIn;

@SwitchYardDeploymentConfig(SwitchYardDeploymentConfig.SWITCHYARD_XML)
@TestMixIns({CDIMixIn.class, HTTPMixIn.class})
public class WebServiceTest extends SwitchYardTestCase {

    @Test
    public void invokeOrderWebService() throws Exception {
        // Use the HttpMixIn to invoke the SOAP binding endpoint with a SOAP input (from the test classpath)
        // and compare the SOAP response to a SOAP response resource (from the test classpath)...
        getMixIn(HTTPMixIn.class).
                postResourceAndTestXML("http://localhost:18001/OrderService", "/xml/soap-request.xml", "/xml/soap-response.xml");
    }
}

