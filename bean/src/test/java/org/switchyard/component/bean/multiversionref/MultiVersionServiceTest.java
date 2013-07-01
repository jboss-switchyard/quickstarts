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

package org.switchyard.component.bean.multiversionref;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;

/**
 * This test is testing checking that @Referenced services can be invoked
 * through old and new interfaces and that the appropriate input/output
 * parameter transformations are automatically applied based on the ExchangeContract
 * information specified in the ClientBeanProxyHandler.
 * <p/>
 * InventoryClientService1 uses the old inventory interface to invoke the inventory service,
 * while InventoryClientService2 uses the new inventory interface.  In both cases, they are
 * invoking the same backend service (the new version).  SwitchYard ensures that the appropriate
 * type transformation happen for InventoryClientService1.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class)
public class MultiVersionServiceTest {

    @ServiceOperation("InventoryClientService1.doStuff")
    private Invoker doStuffOp1;

    @ServiceOperation("InventoryClientService2.doStuff")
    private Invoker doStuffOp2;

    @Test
    public void test_InventoryClientService1() {
        String response = doStuffOp1.sendInOut("hello").getContent(String.class);

        Assert.assertEquals("old", response);
    }

    @Test
    public void test_InventoryClientService2() {
        String response = doStuffOp2.sendInOut("hello").getContent(String.class);

        Assert.assertEquals("new", response);
    }
}
