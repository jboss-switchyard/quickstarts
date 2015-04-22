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

import java.util.HashMap;
import java.util.Map;

import org.switchyard.component.bean.Service;
import org.switchyard.quickstarts.bpm.service.data.Order;

@Service(Inventory.class)
public class InventoryBean implements org.switchyard.quickstarts.bpm.service.Inventory {

    private final Map<String, Integer> inventory = new HashMap<String, Integer>();

    public InventoryBean() {
        inventory.put("cowbell", 100);
    }

    @Override
    public boolean checkAvailability(Order order) {
        return inventory.containsKey(order.getItemId().toLowerCase()) &&
            inventory.get(order.getItemId().toLowerCase()) >= order.getQuantity();
    }

}
