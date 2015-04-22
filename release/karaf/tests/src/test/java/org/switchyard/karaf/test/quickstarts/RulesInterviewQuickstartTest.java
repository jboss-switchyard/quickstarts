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
package org.switchyard.karaf.test.quickstarts;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.component.test.mixins.http.HTTPMixIn;

public class RulesInterviewQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.rules.interview";
    private static String featureName = "switchyard-quickstart-rules-interview";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }

    @Test
    public void testVerifyPass() throws Exception {
        doTestVerify("rules-interview", getSoapClientPort(), true);
    }

    @Test
    public void testVerifyFail() throws Exception {
        doTestVerify("rules-interview", getSoapClientPort(), false);
    }

    static final void doTestVerify(String context, String port, boolean pass) throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();
        httpMixIn.initialize();
        try {
            String request = getSoapRequest(context, pass);
            String response = httpMixIn.postString("http://localhost:" + port + "/" + context + "/Interview", request);
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
