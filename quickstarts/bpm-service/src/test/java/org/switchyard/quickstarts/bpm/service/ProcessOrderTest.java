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
import org.switchyard.quickstarts.bpm.service.data.OrderAck;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class, config = SwitchYardTestCaseConfig.SWITCHYARD_XML)
public class ProcessOrderTest {

    @ServiceOperation("ProcessOrder.submitOrder")
    private Invoker service;

    @BeforeDeploy
    public void setProperties() {
        System.setProperty("org.switchyard.component.http.standalone.port", "18001");
    }

    @Test
    public void orderShipped() {
        Order order = new Order();
        order.setItemId("cowbell");
        order.setOrderId("SHIPIT");
        order.setQuantity(50);

        OrderAck ack = service.sendInOut(order).getContent(OrderAck.class);

        Assert.assertTrue(ack.isAccepted());
        System.out.println(ack.getStatus());
    }

    @Test
    public void orderOnHold() {
        Order order = new Order();
        order.setItemId("cowbell");
        order.setOrderId("FEVER");
        order.setQuantity(500);

        OrderAck ack = service.sendInOut(order).getContent(OrderAck.class);

        Assert.assertFalse(ack.isAccepted());
        System.out.println(ack.getStatus());
    }
}
