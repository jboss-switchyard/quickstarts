/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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
 
package org.switchyard.component.resteasy.util;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.component.resteasy.InboundHandler;
import org.switchyard.component.resteasy.composer.RESTEasyBindingData;
import org.switchyard.component.resteasy.config.model.v1.V1RESTEasyBindingModel;
import org.switchyard.component.resteasy.util.support.Item;

/**
 * Tests for ClassUtil.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class ClassUtilTest extends InboundHandler {

    public ClassUtilTest() {
        super(new V1RESTEasyBindingModel(), null);
    }

    @Test
    public void generateClassInstances() throws Exception {
        String[] intfs = {"org.switchyard.component.resteasy.util.support.WarehouseResource"};
        Object instance = ClassUtil.generateSingletons(intfs, this).get(0);
        Method method = instance.getClass().getMethod("getItem", Integer.class);
        Item response = (Item)method.invoke(instance, 1);
        Item apple = new Item(1, "Apple");
        Assert.assertTrue(response.equals(apple));
        method = instance.getClass().getMethod("addItem", Item.class);
        Item orange = new Item(2, "Orange");
        Assert.assertEquals("[2:Orange]", method.invoke(instance, orange));
        method = instance.getClass().getMethod("updateItem", Item.class);
        Item grape = new Item(2, "Grape");
        Assert.assertEquals("[2:Grape]", method.invoke(instance, grape));
        method = instance.getClass().getMethod("removeItem", Integer.class);
        //Assert.assertEquals("2", method.invoke(instance, 2));
        method = instance.getClass().getMethod("getItemCount");
        Assert.assertEquals(0, method.invoke(instance));
        method = instance.getClass().getMethod("testVoid");
        method.invoke(instance);
    }

    public RESTEasyBindingData invoke(final RESTEasyBindingData restMessageRequest, final boolean oneWay) {
        Object input = restMessageRequest.getParameters().length == 0 ? null : restMessageRequest.getParameters()[0];
        String operationName = restMessageRequest.getOperationName();
        if (operationName.equals("getItem")) {
            Assert.assertTrue(input instanceof Integer);
            Assert.assertTrue((Integer)input == 1);
            Assert.assertFalse(oneWay);
            return new RESTEasyBindingData(new Item(1, "Apple"));
        } else if (operationName.equals("addItem") || operationName.equals("updateItem")) {
            Assert.assertTrue(input instanceof Item);
            Assert.assertFalse(oneWay);
            return new RESTEasyBindingData(((Item)input).toString());
        } else if (operationName.equals("removeItem")) {
            Assert.assertTrue(input instanceof Integer);
            Assert.assertFalse(oneWay);
            return new RESTEasyBindingData("2");
        } else if (operationName.equals("getItemCount")) {
            Assert.assertTrue(input == null);
            Assert.assertFalse(oneWay);
            return new RESTEasyBindingData(0);
        } else if (operationName.equals("testVoid")) {
            Assert.assertTrue(input == null);
            Assert.assertTrue(oneWay);
        }
        return null;
    }
}
