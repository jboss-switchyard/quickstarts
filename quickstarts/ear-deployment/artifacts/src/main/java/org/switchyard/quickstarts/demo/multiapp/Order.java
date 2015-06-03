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
package org.switchyard.quickstarts.demo.multiapp;

public class Order {

    private String _orderId;
    private String _itemId;
    private int _quantity;

    public Order setItemId(String itemId) {
        _itemId = itemId;
        return this;
    }

    public Order setOrderId(String orderId) {
        _orderId = orderId;
        return this;
    }

    public Order setQuantity(int quantity) {
        _quantity = quantity;
        return this;
    }

    public String getOrderId() {
        return _orderId;
    }

    public String getItemId() {
        return _itemId;
    }

    public int getQuantity() {
        return _quantity;
    }
}
