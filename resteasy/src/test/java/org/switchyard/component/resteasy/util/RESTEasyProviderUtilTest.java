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
package org.switchyard.component.resteasy.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.component.resteasy.InboundHandler;

/**
 * Tests for RESTEasyProviderUtil.
 */
public class RESTEasyProviderUtilTest extends InboundHandler {

    private static final String STRING_CLASS_NAME = "java.lang.String";
    private static final String BAD_CLASS_NAME = "bad.Cls";

    private Map<String, String> contextParamsWithProviders;
    private Map<String, String> contextParamsWithOnlyBadProviders;
    private Map<String, String> contextParams;

    public RESTEasyProviderUtilTest() {
        contextParams = new HashMap<String, String>(1);
        contextParams.put(ResteasyContextParameters.RESTEASY_USE_BUILTIN_PROVIDERS, "false");

        contextParamsWithProviders = new HashMap<String, String>(1);
        contextParamsWithProviders.put(ResteasyContextParameters.RESTEASY_PROVIDERS, STRING_CLASS_NAME + "," + BAD_CLASS_NAME);

        contextParamsWithOnlyBadProviders = new HashMap<String, String>(1);
        contextParamsWithOnlyBadProviders.put(ResteasyContextParameters.RESTEASY_PROVIDERS, BAD_CLASS_NAME);
    }

    @Test
    public void testGetProviders() throws Exception {
        Assert.assertNull(RESTEasyProviderUtil.getProviders(null));
        Assert.assertNull(RESTEasyProviderUtil.getProviders(contextParams));

        List<String> providers = RESTEasyProviderUtil.getProviders(contextParamsWithProviders);
        Assert.assertEquals(2, providers.size());
        Assert.assertEquals(STRING_CLASS_NAME, providers.get(0));
        Assert.assertEquals(BAD_CLASS_NAME, providers.get(1));
    }

    @Test
    public void testGetProviderClasses() throws Exception {
        Assert.assertNull(RESTEasyProviderUtil.getProviderClasses(null));
        Assert.assertNull(RESTEasyProviderUtil.getProviderClasses(contextParams));
        Assert.assertNull(RESTEasyProviderUtil.getProviderClasses(contextParamsWithOnlyBadProviders));

        List<Class<?>> providerClasses = RESTEasyProviderUtil.getProviderClasses(contextParamsWithProviders);
        Assert.assertEquals(1, providerClasses.size());
        Assert.assertEquals(String.class, providerClasses.get(0));
    }

}
