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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.test.ArquillianUtil;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
@RunWith(Arquillian.class)
public class RulesInterviewQuickstartTest {

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-rules-interview");
    }

    @Test
    public void testVerifyPass() throws Exception {
        doTestVerify("rules-interview", true);
    }

    @Test
    public void testVerifyFail() throws Exception {
        doTestVerify("rules-interview", false);
    }

    static final void doTestVerify(String context, boolean pass) throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();
        httpMixIn.initialize();
        try {
            String request = getSoapRequest(context, pass);
            String response = httpMixIn.postString("http://localhost:8080/" + context + "/Interview", request);
            XMLAssert.assertXpathEvaluatesTo(String.valueOf(pass), "//return", response);
        } finally {
            httpMixIn.uninitialize();
        }
    }

    private static final String getSoapRequest(String context, boolean pass) {
        return
        "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:urn='urn:switchyard-quickstart:" + context + ":0.1.0'>" +
            "<soapenv:Header/>" +
            "<soapenv:Body>" +
                "<urn:verify>" +
                    "<applicant>" +
                        "<age>" + (pass ? 20 : 16) + "</age>" +
                        "<name>" + (pass ? "Twenty" : "Sixteen") + "</name>" +
                    "</applicant>" +
                "</urn:verify>" +
            "</soapenv:Body>" +
        "</soapenv:Envelope>";
    }

}
