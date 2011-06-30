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

package org.switchyard.component.bean.multiversionref;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.test.SwitchYardTestCase;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;

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
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class)
public class MultiVersionServiceTest extends SwitchYardTestCase {

    @Test
    public void test_InventoryClientService1() {
        String response = newInvoker("InventoryClientService1.doStuff").sendInOut("hello").getContent(String.class);

        Assert.assertEquals("old", response);
    }

    @Test
    public void test_InventoryClientService2() {
        String response = newInvoker("InventoryClientService2.doStuff").sendInOut("hello").getContent(String.class);

        Assert.assertEquals("new", response);
    }
}
