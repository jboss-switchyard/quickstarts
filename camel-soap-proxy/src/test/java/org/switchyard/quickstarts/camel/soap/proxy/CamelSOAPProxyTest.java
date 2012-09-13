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
package org.switchyard.quickstarts.camel.soap.proxy;

import javax.xml.ws.Endpoint;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.HTTPMixIn;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        scanners = {TransformSwitchYardScanner.class},
        mixins = {HTTPMixIn.class})
public class CamelSOAPProxyTest {

    private static final String WEB_SERVICE = "http://localhost:8080/ReverseService";
    private static final String PROXY_SERVICE = "http://localhost:8080/proxy/ReverseService";

    private HTTPMixIn _http;
    private Endpoint _endpoint;

    @Before
    public void startWebService() throws Exception {
        _endpoint = Endpoint.publish(WEB_SERVICE, new ReverseService());
    }

    @After
    public void stopWebService() throws Exception {
        _endpoint.stop();
    }

    @Test
    public void testWebService() throws Exception {
        _http.postResourceAndTestXML(WEB_SERVICE, "/xml/soap-request.xml", "/xml/soap-response.xml");
    }

    @Test
    public void testProxyService() throws Exception {
        _http.postResourceAndTestXML(PROXY_SERVICE, "/xml/soap-request.xml", "/xml/soap-response.xml");
    }

}
