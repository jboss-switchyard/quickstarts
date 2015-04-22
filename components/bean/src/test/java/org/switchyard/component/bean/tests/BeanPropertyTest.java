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

package org.switchyard.component.bean.tests;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;

/*
 * Assorted methods for testing a CDI bean consuming a service in SwitchYard.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "BeanPropertyTests.xml", mixins = CDIMixIn.class)
public class BeanPropertyTest {

    @ServiceOperation("PropertyService.getProperties")
    private Invoker _invoker;

    @Test
    public void testBeanProperty() {
        Map<String,String> response = _invoker.sendInOut(null).getContent(Map.class);
        Assert.assertEquals("bar", response.get("foo"));
        Assert.assertEquals("composite.bar", response.get("composite.foo"));
        Assert.assertEquals("component.bar", response.get("component.foo"));
    }
}
