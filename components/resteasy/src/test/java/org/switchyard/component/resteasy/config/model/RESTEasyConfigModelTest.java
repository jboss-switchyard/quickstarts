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

package org.switchyard.component.resteasy.config.model;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.config.model.ModelPuller;

/**
 * Test of rest binding model.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class RESTEasyConfigModelTest {

    private static final String REST_BINDING = "rest-binding.xml";
    private static final String REST_BINDING_REFERENCE = "rest-binding-reference.xml";
    private static final String REST_BINDING_INVALID = "rest-binding-invalid.xml";
    private static final String REST_BINDING_AUTH = "rest-binding-auth.xml";
    private static final String REST_BINDING_PROXY = "rest-binding-proxy.xml";
    private static final String REST_BINDING_CONTEXT_PARAMS = "rest-binding-context-params.xml";

    @Test
    public void testReadConfigBinding() throws Exception {
        ModelPuller<RESTEasyBindingModel> puller = new ModelPuller<RESTEasyBindingModel>();
        RESTEasyBindingModel model = puller.pull(REST_BINDING, getClass());
        Assert.assertTrue(model.isModelValid());
        model = puller.pull(REST_BINDING_INVALID, getClass());
        Assert.assertFalse(model.isModelValid());
    }

    @Test
    public void testReadReferenceBinding() throws Exception {
        ModelPuller<RESTEasyBindingModel> puller = new ModelPuller<RESTEasyBindingModel>();
        RESTEasyBindingModel model = puller.pull(REST_BINDING_REFERENCE, getClass());
        Assert.assertTrue(model.isModelValid());
        Assert.assertEquals(new Integer(5000), model.getTimeout());
    }

    @Test
    public void authBinding() throws Exception {
        ModelPuller<RESTEasyBindingModel> puller = new ModelPuller<RESTEasyBindingModel>();
        RESTEasyBindingModel binding = puller.pull(REST_BINDING_AUTH, getClass());
        binding.assertModelValid();
        NtlmAuthModel authConfig = binding.getNtlmAuthConfig();
        Assert.assertEquals("user", authConfig.getUser());
        Assert.assertEquals("password", authConfig.getPassword());
        Assert.assertEquals("domain", authConfig.getDomain());
    }

    @Test
    public void proxyConfigBinding() throws Exception {
        ModelPuller<RESTEasyBindingModel> puller = new ModelPuller<RESTEasyBindingModel>();
        RESTEasyBindingModel model = puller.pull(REST_BINDING_PROXY, getClass());
        model.assertModelValid();
        ProxyModel proxyConfig = model.getProxyConfig();
        Assert.assertNotNull(proxyConfig);
        Assert.assertEquals("host", proxyConfig.getHost());
        Assert.assertEquals("8090", proxyConfig.getPort());
        Assert.assertEquals("Beal", proxyConfig.getUser());
        Assert.assertEquals("conjecture", proxyConfig.getPassword());
    }

    @Test
    public void testReadConfigBindingWithContextParams() throws Exception {
        ModelPuller<RESTEasyBindingModel> puller = new ModelPuller<RESTEasyBindingModel>();
        RESTEasyBindingModel model = puller.pull(REST_BINDING_CONTEXT_PARAMS, getClass());
        Assert.assertTrue(model.isModelValid());

        List<ContextParamModel> contextParams = model.getContextParamsConfig().getContextParams();
        Assert.assertEquals(2, contextParams.size());

        Assert.assertEquals("resteasy.providers", contextParams.get(0).getName());
        Assert.assertEquals("org.switchyard.quickstarts.rest.binding.TestResourceExceptionMapper", contextParams.get(0).getValue());

        Assert.assertEquals("resteasy.use.builtin.providers", contextParams.get(1).getName());
        Assert.assertEquals("true", contextParams.get(1).getValue());
    }
}
