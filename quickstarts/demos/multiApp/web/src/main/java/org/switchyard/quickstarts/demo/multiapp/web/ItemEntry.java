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
package org.switchyard.quickstarts.demo.multiapp.web;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.switchyard.component.bean.Reference;
import org.switchyard.quickstarts.demo.multiapp.InventoryService;
import org.switchyard.quickstarts.demo.multiapp.Item;

@Named
@RequestScoped
public class ItemEntry {

    @Inject
    @Reference
    private InventoryService inventory;

    private Item item = new Item();

    public void setItemId(String itemId) {
        item.setItemId(itemId);
    }

    public String getItemId() {
        return item.getItemId();
    }

    public void setName(String name) {
        item.setName(name);
    }

    public String getName() {
        return item.getName();
    }

    public void setQuantity(int quantity) {
        item.setQuantity(quantity);
    }

    public int getQuantity() {
        return item.getQuantity();
    }

    public void create() {
        inventory.createItem(item);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
            "Item " + getItemId() + " has been added."));
    }
}
