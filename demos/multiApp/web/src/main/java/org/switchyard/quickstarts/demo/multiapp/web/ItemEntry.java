/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2010-2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
 * details. You should have received a copy of the GNU Lesser General Public 
 * License, v.2.1 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301, USA.
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
