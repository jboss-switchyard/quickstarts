/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.http.binding;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

/**
 * Tests for Camel CXFRS binding.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = { CDIMixIn.class, HTTPMixIn.class },
    scanners = TransformSwitchYardScanner.class)
@RunWith(SwitchYardRunner.class)
public class HttpBindingTest {

    private static final String BASE_URL = "http://localhost:8081/http-binding";

    private HTTPMixIn http;

    @BeforeDeploy
    public void setProperties() {
        System.setProperty("org.switchyard.component.http.standalone.port", "8081");
    }

    /**
     * Ignore until this is fixed http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7189193
     */
    @Ignore
    @Test
    public void stockServiceEndpoint() throws Exception {
        http.setContentType("text/plain");
        String response = http.sendString(BASE_URL + "/quote", "vineyard", HTTPMixIn.HTTP_POST);
        Assert.assertEquals("136.5", response);
    }

    @Test
    public void symbolServiceEndpoint() throws Exception {
        String response = http.sendString(BASE_URL + "/quote", "rum", HTTPMixIn.HTTP_POST);
        Assert.assertEquals("0.0", response);
        http.setContentType("text/plain");
        response = http.sendString(BASE_URL + "/symbol", "vineyard", HTTPMixIn.HTTP_POST);
        Assert.assertEquals("WINE", response);
        int status = http.sendStringAndGetStatus(BASE_URL + "/symbol", "rum", HTTPMixIn.HTTP_POST);
        Assert.assertEquals(404, status);
    }

    @Test
    public void error() throws Exception {
        int status = http.sendStringAndGetStatus(BASE_URL + "/error", "", HTTPMixIn.HTTP_POST);
        Assert.assertEquals(500, status);
    }

    @Test
    public void headers() throws Exception {
        String response = http.sendString(BASE_URL + "/symbol", "headers", HTTPMixIn.HTTP_POST);
        Assert.assertTrue("Unexpected response: [" + response + "]", response.indexOf("Content-type=text/xml; charset=UTF-8") >= 0);
    }

    @Test
    public void requestInfo() throws Exception {
        String response = http.sendString(BASE_URL + "/symbol", "requestInfo", HTTPMixIn.HTTP_POST);
        Assert.assertTrue(response.indexOf("HttpRequestInfo [authType=null, characterEncoding=UTF-8, contentType=text/xml;charset=UTF-8, contextPath=/http-binding/symbol") == 0);
    }
}
