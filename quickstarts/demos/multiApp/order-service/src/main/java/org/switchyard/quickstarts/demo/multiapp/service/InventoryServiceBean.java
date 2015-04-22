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
package org.switchyard.quickstarts.demo.multiapp.service;

import java.util.HashMap;
import java.util.Map;

import org.switchyard.component.bean.Service;
import org.switchyard.quickstarts.demo.multiapp.InventoryService;
import org.switchyard.quickstarts.demo.multiapp.Item;
import org.switchyard.quickstarts.demo.multiapp.ItemNotFoundException;

@Service(InventoryService.class)
public class InventoryServiceBean implements InventoryService {

    private final Map<String, Item> _inventory = new HashMap<String, Item>();

    public InventoryServiceBean() {
        Item butter = new Item()
            .setItemId("BUTTER")
            .setName("Not Parkay")
            .setQuantity(1000);
        _inventory.put(butter.getItemId(), butter);
    }

    @Override
    public Item lookupItem(String itemId) throws ItemNotFoundException {
        Item item = _inventory.get(itemId);
        if (item == null) {
            throw new ItemNotFoundException("We don't got any " + itemId);
        }

        return item;
    }

    @Override
    public void createItem(Item item) {
        _inventory.put(item.getItemId(), item);
    }
}
