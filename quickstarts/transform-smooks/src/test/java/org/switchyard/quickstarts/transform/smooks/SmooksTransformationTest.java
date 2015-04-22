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
package org.switchyard.quickstarts.transform.smooks;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.smooks.SmooksMixIn;

/**
 * Transformation only tests.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = SmooksMixIn.class)
public class SmooksTransformationTest {

    private SmooksMixIn smooksMixIn;

    @Test
    public void test_Order_read_write() throws Exception {
        // Use the Smooks TestMixIn to verify that the Order_XML.xml
        // Smooks java binding transform works against the supplied order.xml...
        smooksMixIn.testJavaXMLReadWrite(Order.class, "/smooks/Order_XML.xml", "/xml/order.xml");
    }

    @Test
    public void test_OrderAck_read_write() throws Exception {
        // Use the Smooks TestMixIn to verify that the OrderAck_XML.xml
        // Smooks java binding transform works against the supplied orderAck.xml...
        smooksMixIn.testJavaXMLReadWrite(OrderAck.class, "/smooks/OrderAck_XML.xml", "/xml/orderAck.xml");
    }
}
