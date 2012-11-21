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

package org.switchyard.quickstarts.validate.xml;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.http.HTTPMixIn;


@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        scanners = {BeanSwitchYardScanner.class},
        mixins = {CDIMixIn.class, HTTPMixIn.class})
public class WebServiceTest {

    private HTTPMixIn httpMixIn;
    
    @Test
    public void invokeOrderWebService() throws Exception {
        httpMixIn.
            postResourceAndTestXML("http://localhost:18001/quickstart-validate-xml/OrderService", "/xml/soap-request.xml", "/xml/soap-response.xml");
    }
    
    @Test
    public void invokeOrderWebServiceValidationFail() throws Exception {
        String response = httpMixIn.
                postResource("http://localhost:18001/quickstart-validate-xml/OrderService", "/xml/soap-request-with-invalid-element.xml");
        Assert.assertTrue(response.contains("<faultstring>Error during validation with '/xsd/orders.xsd' as 'XML_SCHEMA'.</faultstring>"));
    }
    
    /*
     * This test will succeed when you disable the validate.xml element in switchyard.xml.
     * 
    @Test
    public void invokeOrderWebServiceWithInvalidElement() throws Exception {
        httpMixIn.
            postResourceAndTestXML("http://localhost:18001/quickstart-validate-xml/OrderService", "/xml/soap-request-with-invalid-element.xml", "/xml/soap-response.xml");
    }
    */
}
