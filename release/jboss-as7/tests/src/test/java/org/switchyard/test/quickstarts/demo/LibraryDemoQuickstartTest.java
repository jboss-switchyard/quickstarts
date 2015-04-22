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
package org.switchyard.test.quickstarts.demo;

import org.custommonkey.xmlunit.XMLAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.test.ArquillianUtil;

@RunWith(Arquillian.class)
public class LibraryDemoQuickstartTest {

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarDemoDeployment("switchyard-demo-library");
    }

    @Test
    public void testProcessOrder() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();
        httpMixIn.initialize();
        try {
            suggestion(httpMixIn, 1);
            loan(httpMixIn, 2);
            loan(httpMixIn, 3);
            loan(httpMixIn,4);
            loan(httpMixIn, 5);
            suggestion(httpMixIn, 6);
            loan(httpMixIn, 7);
            loan(httpMixIn, 8);
            loan(httpMixIn,9);
        } finally {
            httpMixIn.uninitialize();
        }
    }
    
    private void suggestion(HTTPMixIn httpMixIn, int interaction) throws Exception {
        String actual = httpMixIn.postString("http://localhost:8080/suggestion/SuggestionService", xml("request", interaction));
        String expected = xml("response", interaction);
        XMLAssert.assertXMLEqual(expected, actual);
    }

    private void loan(HTTPMixIn httpMixIn, int interaction) throws Exception {
        String actual = httpMixIn.postString("http://localhost:8080/loan/LoanService", xml("request", interaction));
        String expected = xml("response", interaction);
        XMLAssert.assertXMLEqual(expected, actual);
    }

    private String xml(String direction, int interaction) throws Exception {
        return new StringPuller().pull("org/switchyard/test/quickstarts/demo/library/soap-" + direction + "-" + interaction + ".xml", getClass());
    }
}
