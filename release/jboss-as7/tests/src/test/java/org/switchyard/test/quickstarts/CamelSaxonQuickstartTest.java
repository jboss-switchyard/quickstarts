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

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.test.ArquillianUtil;

@RunWith(Arquillian.class)
public class CamelSaxonQuickstartTest {

    private static final String URL = "http://localhost:8080/quickstart-camel-saxon/GreetingService";

    private static final String SOAP_REQUEST = 
"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
"   <soapenv:Header/>" +
"   <soapenv:Body>" +
"      <greet xmlns=\"urn:switchyard-quickstart:camel-saxon:0.1.0\">Garfield</greet>" +
"   </soapenv:Body>" +
"</soapenv:Envelope>";

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-camel-saxon");
    }

    @Test
    public void testGreeting() throws Exception {
        HTTPMixIn soapMixIn = new HTTPMixIn();
        soapMixIn.initialize();

        try {
            int status = soapMixIn.sendStringAndGetStatus(URL, SOAP_REQUEST, HTTPMixIn.HTTP_POST);
            Assert.assertEquals("Unexpected status.", 202, status);
        } finally {
            soapMixIn.uninitialize();
        }
    }

    @Test
    public void testGatewayRestart(@ArquillianResource ManagementClient client) throws Exception {
        HTTPMixIn soapMixIn = new HTTPMixIn();
        soapMixIn.initialize();

        try {
            int status = soapMixIn.sendStringAndGetStatus(URL, SOAP_REQUEST, HTTPMixIn.HTTP_POST);
            Assert.assertEquals("Unexpected status.", 202, status);

            final String namespace = "urn:switchyard-quickstart:camel-saxon:0.1.0";
            final String application = "camel-saxon";
            final String service = "GreetingService";
            final String bindingType = "soap";
            final ModelNode operation = new ModelNode();
            operation.get(ModelDescriptionConstants.OP_ADDR).add("subsystem", "switchyard");
            operation.get(ModelDescriptionConstants.NAME).set("_" + service + "_" + bindingType + "_1");
            operation.get("service-name").set(new QName(namespace, service).toString());
            operation.get("application-name").set(new QName(namespace, application).toString());

            // stop the gateway
            operation.get(ModelDescriptionConstants.OP).set("stop-gateway");
            ModelNode result = client.getControllerClient().execute(operation);
            Assert.assertEquals("Failed to stop gateway: " + result.toString(), ModelDescriptionConstants.SUCCESS,
                    result.get(ModelDescriptionConstants.OUTCOME).asString());
            status = soapMixIn.sendStringAndGetStatus(URL, SOAP_REQUEST, HTTPMixIn.HTTP_POST);
            Assert.assertEquals("Unexpected status for disabled gateway.", 404, status);

            // restart the gateway
            operation.get(ModelDescriptionConstants.OP).set("start-gateway");
            result = client.getControllerClient().execute(operation);
            Assert.assertEquals("Failed to restart gateway" + result.toString(), ModelDescriptionConstants.SUCCESS,
                    result.get(ModelDescriptionConstants.OUTCOME).asString());
            status = soapMixIn.sendStringAndGetStatus(URL, SOAP_REQUEST, HTTPMixIn.HTTP_POST);
            Assert.assertEquals("Unexpected status for restarted gateway.", 202, status);
        } finally {
            soapMixIn.uninitialize();
        }
    }
}
