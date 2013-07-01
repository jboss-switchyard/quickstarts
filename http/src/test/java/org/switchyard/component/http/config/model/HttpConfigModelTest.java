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

package org.switchyard.component.http.config.model;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.component.http.config.model.BasicAuthModel;
import org.switchyard.component.http.config.model.HttpBindingModel;
import org.switchyard.component.http.config.model.ProxyModel;
import org.switchyard.config.model.ModelPuller;

/**
 * Test of HTTP binding model.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class HttpConfigModelTest {

    private static final String HTTP_BINDING = "http-binding.xml";
    private static final String HTTP_BINDING2 = "http-binding2.xml";
    private static final String HTTP_BINDING_AUTH = "http-binding-auth.xml";
    private static final String HTTP_BINDING_PROXY = "http-binding-proxy.xml";

    @Test
    public void testReadConfigBinding() throws Exception {
        ModelPuller<HttpBindingModel> puller = new ModelPuller<HttpBindingModel>();
        HttpBindingModel model = puller.pull(HTTP_BINDING, getClass());
        Assert.assertTrue(model.isModelValid());
        model = puller.pull(HTTP_BINDING2, getClass());
        Assert.assertTrue(model.isModelValid());
    }

    @Test
    public void authConfigBinding() throws Exception {
        ModelPuller<HttpBindingModel> puller = new ModelPuller<HttpBindingModel>();
        HttpBindingModel model = puller.pull(HTTP_BINDING_AUTH, getClass());
        Assert.assertTrue(model.isModelValid());
        Assert.assertTrue(model.isBasicAuth());
        BasicAuthModel authConfig = model.getBasicAuthConfig();
        Assert.assertNotNull(authConfig);
        Assert.assertEquals("Beal", authConfig.getUser());
        Assert.assertEquals("conjecture", authConfig.getPassword());
        Assert.assertEquals("Any", authConfig.getRealm());
    }

    @Test
    public void proxyConfigBinding() throws Exception {
        ModelPuller<HttpBindingModel> puller = new ModelPuller<HttpBindingModel>();
        HttpBindingModel model = puller.pull(HTTP_BINDING_PROXY, getClass());
        Assert.assertTrue(model.isModelValid());
        ProxyModel proxyConfig = model.getProxyConfig();
        Assert.assertNotNull(proxyConfig);
        Assert.assertEquals("host", proxyConfig.getHost());
        Assert.assertEquals("8090", proxyConfig.getPort());
        Assert.assertEquals("Beal", proxyConfig.getUser());
        Assert.assertEquals("conjecture", proxyConfig.getPassword());
    }
}
