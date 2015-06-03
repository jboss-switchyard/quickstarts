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

package org.switchyard.transform.ootb.map;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.metadata.JavaTypes;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.internal.TransformerUtil;
import org.switchyard.transform.ootb.map.ordermodel.Item;
import org.switchyard.transform.ootb.map.ordermodel.Order;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class FromMapToJavaTest {

    @Test
    public void test_factory_creation() {
        QName from = JavaTypes.toMessageType(Map.class);
        QName to = JavaTypes.toMessageType(Order.class);

        Transformer<?,?> transformer = TransformerUtil.newTransformer(FromMapToJava.class, from, to);
        Assert.assertTrue(transformer instanceof FromMapToJava);
    }

    @Test
    public void test_transform() {
        Map orderHash = buildOrderMap();
        FromMapToJava transformer = new FromMapToJava();

        transformer.setTo(JavaTypes.toMessageType(Order.class));

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
