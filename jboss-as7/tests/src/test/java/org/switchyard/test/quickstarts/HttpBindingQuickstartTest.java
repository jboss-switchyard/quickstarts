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

import javax.xml.namespace.QName;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.component.test.mixins.http.HTTPMixIn;

@RunWith(Arquillian.class)
public class HttpBindingQuickstartTest {

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-http-binding");
    }

    @Test
    public void quoteService() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();

        httpMixIn.initialize();
        try {
            String response = httpMixIn.sendString(BASE_URL + "/quote", "vineyard", HTTPMixIn.HTTP_POST);
            Assert.assertEquals("136.5", response);
        } finally {
            httpMixIn.uninitialize();
        }
    }

    @Test
    public void gatewayRestart(@ArquillianResource ManagementClient client) throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();

        httpMixIn.initialize();
        try {
            String response = httpMixIn.sendString(BASE_URL + "/quote", "vineyard", HTTPMixIn.HTTP_POST);
            Assert.assertEquals("136.5", response);

            final String namespace = "urn:switchyard-quickstart:http-binding:1.0";
            final ModelNode operation = new ModelNode();
            operation.get(ModelDescriptionConstants.OP_ADDR).add("subsystem", "switchyard");
            operation.get(ModelDescriptionConstants.NAME).set("_QuoteService_http_1");
            operation.get("service-name").set(new QName(namespace, "QuoteService").toString());
            operation.get("application-name").set(new QName(namespace, "http-binding").toString());

            // stop the gateway
            operation.get(ModelDescriptionConstants.OP).set("stop-gateway");
            ModelNode result = client.getControllerClient().execute(operation);
            Assert.assertEquals("Failed to stop gateway: " + result.toString(), ModelDescriptionConstants.SUCCESS,
                    result.get(ModelDescriptionConstants.OUTCOME).asString());
            Assert.assertEquals(404,
                    httpMixIn.sendStringAndGetStatus(BASE_URL + "/quote", "vineyard", HTTPMixIn.HTTP_POST));

            // restart the gateway
            operation.get(ModelDescriptionConstants.OP).set("start-gateway");
            result = client.getControllerClient().execute(operation);
            Assert.assertEquals("Failed to restart gateway: " + result.toString(), ModelDescriptionConstants.SUCCESS,
                    result.get(ModelDescriptionConstants.OUTCOME).asString());
            Assert.assertEquals("136.5", httpMixIn.sendString(BASE_URL + "/quote", "vineyard", HTTPMixIn.HTTP_POST));
        } finally {
            httpMixIn.uninitialize();
        }
    }

    @Test
    public void headers() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();
        httpMixIn.initialize();
        try {
            String response = httpMixIn.sendString(BASE_URL + "/symbol", "headers", HTTPMixIn.HTTP_POST);
            Assert.assertEquals("content-type=text/xml; charset=utf-8", response.toLowerCase());
            int status = httpMixIn.sendStringAndGetStatus(BASE_URL + "/symbol", "rum", HTTPMixIn.HTTP_POST);
            Assert.assertEquals(404, status);
        } finally {
            httpMixIn.uninitialize();
        }
    }

    @Test
    public void requestInfo() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();
        httpMixIn.initialize();
        try {
            String response = httpMixIn.sendString(BASE_URL + "/symbol", "requestInfo", HTTPMixIn.HTTP_POST);
            Assert.assertTrue(response.indexOf("HttpRequestInfo [authType=null, characterEncoding=UTF-8, contentType=text/xml; charset=UTF-8, contextPath=") == 0);
        } finally {
            httpMixIn.uninitialize();
        }
    }
    private static final String BASE_URL = "http://localhost:8080/http-binding";
}
