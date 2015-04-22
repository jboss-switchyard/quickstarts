/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.test.quickstarts;

import org.custommonkey.xmlunit.XMLAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.component.test.mixins.http.HTTPMixIn;

/**
 *
 * @author Tomohisa Igarashi <tm.igarashi@gmail.com> (C) 2011 Red Hat Inc.
 */
@RunWith(Arquillian.class)
public class BPMServiceQuickstartTest {

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-bpm-service");
    }

    @Test
    public void testProcessOrder() throws Exception {
    	HTTPMixIn httpMixIn = new HTTPMixIn();
    	httpMixIn.initialize();
    	try {
    		String response = httpMixIn.postString("http://localhost:8080/swydws/ProcessOrder", SOAP_REQUEST);
    		XMLAssert.assertXpathEvaluatesTo("PO-19839-XYZ", "//orderId", response);
    		XMLAssert.assertXpathEvaluatesTo("true", "//accepted", response);
    		XMLAssert.assertXpathEvaluatesTo("Thanks for your order, it has been shipped!", "//status", response);
    	} finally {
    		httpMixIn.uninitialize();
    	}
    }

    private static final String SOAP_REQUEST = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
    		"    <soap:Body>\n" +
    		"        <orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart:bpm-service:1.0\">\n" +
    		"            <orderId>PO-19839-XYZ</orderId>\n" +
    		"            <itemId>cowbell</itemId>\n" +
    		"            <quantity>100</quantity>\n" +
    		"        </orders:submitOrder>\n" +
    		"    </soap:Body>\n" +
    		"</soap:Envelope>";    	
}
