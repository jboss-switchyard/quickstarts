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

package org.switchyard.quickstarts.soap.binding.rpc;

import javax.xml.ws.Endpoint;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = {CDIMixIn.class, HTTPMixIn.class},
        scanners = {BeanSwitchYardScanner.class, TransformSwitchYardScanner.class })
public class SoapBindingTest {

    private static final String SWITCHYARD_WEB_SERVICE = "http://localhost:8080/soap-binding-rpc/HelloWorldWSService";

    private static Endpoint _endpoint;

    private HTTPMixIn _httpMixIn;

    @Test
    public void testSwitchYardWebService() throws Exception {
        _httpMixIn.postResourceAndTestXML(SWITCHYARD_WEB_SERVICE, "/xml/soap-request.xml", "/xml/soap-response.xml");
        int status = _httpMixIn.postResourceAndGetStatus(SWITCHYARD_WEB_SERVICE, "/xml/soap-request-500.xml");
        Assert.assertEquals(500, status);
    }
}
