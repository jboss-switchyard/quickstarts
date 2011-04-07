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

package org.switchyard.transform.ootb.map;


import org.junit.Assert;
import org.junit.Test;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.transform.ootb.map.ordermodel.Item;
import org.switchyard.transform.ootb.map.ordermodel.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class FromMapToJavaTest {

    @Test
    public void test() {
        Map orderHash = buildOrderMap();
        FromMapToJava transformer = new FromMapToJava();

        transformer.setTo(JavaService.toMessageType(Order.class));

        Order order = (Order) transformer.transform(orderHash);

        Assert.assertEquals(1234, order.getHeader().getOrderId());
        Assert.assertEquals("catalog", order.getHeader().getOriginType());
        Assert.assertEquals(1234, order.getHeader().getCustomerDetails().getCustId());
        Assert.assertEquals("Seamus Ungabunga", order.getHeader().getCustomerDetails().getName());
        List<Item> items = order.getItems();
        Assert.assertEquals(2, items.size());
        Assert.assertEquals((Integer) 1234, items.get(0).getItemId());
        Assert.assertEquals("X", items.get(0).getProductId());
        Assert.assertEquals((Integer) 1235, items.get(1).getItemId());
        Assert.assertEquals("Y", items.get(1).getProductId());
    }

    private Map buildOrderMap() {
        Map<String, Object> order = new HashMap<String, Object>();
        Map<String, Object> header = new HashMap<String, Object>();
        Map<String, Object> customerDetails = new HashMap<String, Object>();
        List<Map> items = new ArrayList<Map>();
        Map<String, Object> item1 = new HashMap<String, Object>();
        Map<String, Object> item2 = new HashMap<String, Object>();

        order.put("header", header);
        header.put("orderId", 1234);
        header.put("originType", "catalog");
        header.put("customerDetails", customerDetails);
        customerDetails.put("custId", 1234);
        customerDetails.put("name", "Seamus Ungabunga");
        order.put("items", items);
        items.add(item1);
        items.add(item2);
        item1.put("itemId", 1234);
        item1.put("productId", "X");
        item2.put("itemId", 1235);
        item2.put("productId", "Y");

        return order;
    }
}
