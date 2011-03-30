/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.switchyard.quickstarts.transform.smooks;

import org.junit.Test;
import org.switchyard.test.SwitchYardTestCase;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.SmooksMixIn;

/**
 * Transformation only tests.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@SwitchYardTestCaseConfig(mixins = SmooksMixIn.class)
public class SmooksTransformationTest extends SwitchYardTestCase {

    @Test
    public void test_Order_read_write() throws Exception {
        // Use the Smooks TestMixIn to verify that the Order_XML.xml
        // Smooks java binding transform works against the supplied order.xml...
        SmooksMixIn smooksMixIn = getMixIn(SmooksMixIn.class);
        smooksMixIn.testJavaXMLReadWrite(Order.class, "/smooks/Order_XML.xml", "/xml/order.xml");
    }

    @Test
    public void test_OrderAck_read_write() throws Exception {
        // Use the Smooks TestMixIn to verify that the OrderAck_XML.xml
        // Smooks java binding transform works against the supplied orderAck.xml...
        SmooksMixIn smooksMixIn = getMixIn(SmooksMixIn.class);
        smooksMixIn.testJavaXMLReadWrite(OrderAck.class, "/smooks/OrderAck_XML.xml", "/xml/orderAck.xml");
    }
}
