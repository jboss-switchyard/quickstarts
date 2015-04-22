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
package org.switchyard.quickstarts.rest.binding;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;

/**
 * An OrderService implementation.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
@Service(OrderService.class)
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = Logger.getLogger(OrderService.class);
    private static final String SUCCESS = "SUCCESS";
    private static Integer orderNo = 0;
    private ConcurrentMap<Integer, Order> _orders = new ConcurrentHashMap<Integer, Order>();

    @Inject
    @Reference("Warehouse")
    private Warehouse _warehouse;

    @Inject
    @Reference("WarehouseRef")
    private WarehouseRef _warehouseRef;

    public OrderServiceImpl() {
    }

    public Order newOrder() {
        orderNo++;
        LOGGER.info("Creating new Order with no: " + orderNo);
        Order order = new Order(orderNo);
        _orders.put(orderNo, order);
        return order;
    }

    public Order getOrder(Integer orderId) throws Exception {
        LOGGER.info("Getting Order with no: " + orderId);
        if (orderId == 404) {
            Order order = new Order(404);
            _orders.put(orderNo, order);
            try {
                _warehouseRef.get404();
            } catch (Exception e) {
                throw e;
            }
            return order;
        }
        Order corder = _orders.get(orderId);
        if (corder == null) {
            throw new ItemNotFoundException("Order " + orderId + " not found!");
        }
        LOGGER.info("Updating descriptions: " + corder.getItems());
        // Get the updated descriptions of items
        for (OrderItem orderItem : corder.getItems()) {
            Item item = _warehouse.getItem(orderItem.getItem().getItemId());
            orderItem.setItem(item);
        }
        return corder;
    }

    public String addItems(Order order) throws Exception {
        LOGGER.info("Adding items to order: " + order.getOrderId());
        Order corder = getOrder(order.getOrderId());
        for (OrderItem orderItem : order.getItems()) {
            LOGGER.info("Adding item: " + orderItem.getItem().getItemId());
            Item item = _warehouse.getItem(orderItem.getItem().getItemId());
            orderItem.setItem(item);
            corder.getItems().add(orderItem);
        }
        LOGGER.info("Order after adding items: " + corder);
        return SUCCESS;
    }

    public String removeItem(String orderItemId) throws Exception {
        // Not so elegant, but what to do?
        String[] ids = orderItemId.split(":");
        if (ids.length != 2) {
            throw new WrongRequestArgsException("Missing either order id or item id!");
        }
        Integer orderId = Integer.parseInt(ids[0]);
        Integer itemId = Integer.parseInt(ids[1]);
        Order corder = getOrder(orderId);
        for (OrderItem orderItem : corder.getItems()) {
            if (orderItem.getItem().getItemId() == itemId) {
                corder.getItems().remove(orderItem);
                break;
            }
        }
        LOGGER.info("Order after deleting item: " + corder);
        return SUCCESS;
    }

    public Boolean isInventorySetup() {
        int size = _warehouse.getItemCount();
        System.out.println(".... Inventory: " + size);
        return (size > 0);
    }

    public String createInventory() throws Exception {
        System.out.println(".......................... createInventory");
        _warehouse.addItem(new Item(1, "Hydrogen Atom - No, we are not kidding!"));
        _warehouse.addItem(new Item(2, "Handcrafted Copper Plate"));
        _warehouse.addItem(new Item(3, "Einstein's Bust - Talks about your future :)"));
        _warehouse.addItem(new Item(4, "Time Machine"));
        return SUCCESS;
    }

    public String updateInventory() throws Exception {
        _warehouse.updateItem(new Item(1, "Water"));
        _warehouse.updateItem(new Item(2, "Cuttlery"));
        _warehouse.updateItem(new Item(3, "Theory of relativity"));
        _warehouse.updateItem(new Item(4, "Coffee Maker"));
        return SUCCESS;
    }

    public String removeInventory() throws Exception {
        _warehouse.removeItem(1);
        _warehouse.removeItem(2);
        _warehouse.removeItem(3);
        _warehouse.removeItem(4);
        return SUCCESS;
    }

    public void throwError() {
        throw new RuntimeException();
    }
}
