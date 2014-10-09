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
package org.switchyard.quickstarts.bpm.service;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.quickstarts.bpm.service.data.Order;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class)
public class InventoryTest {

    @ServiceOperation("Inventory.checkAvailability")
    private Invoker service;

    @Test
    public void weGotIt() {
        Order order = new Order();
        order.setItemId("cowbell");
        order.setQuantity(50);

        Assert.assertTrue(
            service.sendInOut(order)
                .getContent(Boolean.class));
    }

    @Test
    public void weDontGotIt() {
        Order order = new Order();
        order.setItemId("cowbell");
        order.setQuantity(500);

        Assert.assertFalse(
            service.sendInOut(order)
                .getContent(Boolean.class));
    }
}
