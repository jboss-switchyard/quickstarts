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
package org.switchyard.config.model.switchyard;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.ExtensionsModel;
import org.switchyard.config.model.switchyard.v1.V1ThrottlingModel;

/**
 * ThrottlingExtensionTests.
 */
public class ThrottlingExtensionTests {

    private static final String THROTTLING_XML = "/org/switchyard/config/model/switchyard/ThrottlingExtensionTests.xml";
    private static final String THROTTLING_XML2 = "/org/switchyard/config/model/switchyard/ThrottlingExtensionTests2.xml";
    private static final Integer MAX_REQUESTS = 50;
    private static final Long TIME_PERIOD = 2000L;

    private ModelPuller<SwitchYardModel> _puller;

    @Before
    public void before() throws Exception {
        _puller = new ModelPuller<SwitchYardModel>();
    }

    @Test
    public void testCreate() throws Exception {
        ThrottlingModel throttling = new V1ThrottlingModel(SwitchYardNamespace.DEFAULT.uri());
        throttling.setMaxRequests(MAX_REQUESTS).setTimePeriod(TIME_PERIOD);

        Assert.assertEquals(MAX_REQUESTS, (Integer) throttling.getMaxRequests());
        Assert.assertEquals(TIME_PERIOD, throttling.getTimePeriod());
    }

    @Test
    public void testRead() throws Exception {
        SwitchYardModel switchyard = _puller.pull(THROTTLING_XML, getClass());
        ExtensionsModel extensions = switchyard.getComposite().getServices().get(0).getExtensions();
        ThrottlingModel throttling = extensions.getThrottling();

        Assert.assertEquals(MAX_REQUESTS, (Integer) throttling.getMaxRequests());
        Assert.assertEquals(TIME_PERIOD, throttling.getTimePeriod());
    }

    @Test
    public void testPropertySubstitution() throws Exception {
        System.setProperty("property.messages.per.poll", "50");
        SwitchYardModel switchyard = _puller.pull(THROTTLING_XML2, getClass());
        switchyard.assertModelValid();
        ExtensionsModel extensions = switchyard.getComposite().getServices().get(0).getExtensions();
        ThrottlingModel throttling = extensions.getThrottling();

        Assert.assertEquals(MAX_REQUESTS, (Integer) throttling.getMaxRequests());
        Assert.assertEquals(TIME_PERIOD, throttling.getTimePeriod());
    }

    @Test
    public void testValidation() throws Exception {
        SwitchYardModel switchyard = _puller.pull(THROTTLING_XML, getClass());
        switchyard.assertModelValid();
    }

}
