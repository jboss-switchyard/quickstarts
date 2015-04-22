/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.karaf.test.quickstarts.demo;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.karaf.test.quickstarts.AbstractQuickstartTest;

public class LibraryDemoQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.demos.switchyard.demo.library";
    private static String featureName = "switchyard-demo-library";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }

    @Override
    @Test
    public void testDeployment() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();
        httpMixIn.initialize();
        try {
            String port = getSoapClientPort();
            suggestion(httpMixIn, port, 1);
            loan(httpMixIn, port, 2);
            loan(httpMixIn, port, 3);
            loan(httpMixIn, port, 4);
            loan(httpMixIn, port, 5);
            suggestion(httpMixIn, port, 6);
            loan(httpMixIn, port, 7);
            loan(httpMixIn, port, 8);
            loan(httpMixIn, port, 9);
        } finally {
            httpMixIn.uninitialize();
        }
    }

    private void suggestion(HTTPMixIn httpMixIn, String port, int interaction) throws Exception {
        String actual = httpMixIn.postString("http://localhost:" + port + "/suggestion/SuggestionService", xml("request", interaction));
        String expected = xml("response", interaction);
        XMLAssert.assertXMLEqual(expected, actual);
    }

    private void loan(HTTPMixIn httpMixIn, String port, int interaction) throws Exception {
        String actual = httpMixIn.postString("http://localhost:" + port + "/loan/LoanService", xml("request", interaction));
        String expected = xml("response", interaction);
        XMLAssert.assertXMLEqual(expected, actual);
    }

    private String xml(String direction, int interaction) throws Exception {
        return new StringPuller().pull("quickstarts/demos/library/soap-" + direction + "-" + interaction + ".xml", getClass());
    }

}
