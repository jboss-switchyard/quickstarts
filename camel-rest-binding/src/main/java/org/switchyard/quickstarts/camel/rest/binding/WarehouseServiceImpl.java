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

package org.switchyard.quickstarts.camel.rest.binding;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.switchyard.component.bean.Service;

/**
 * A WarehouseService implementation.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
@Service(WarehouseService.class)
public class WarehouseServiceImpl implements WarehouseService {

    private static final Logger LOGGER = Logger.getLogger(WarehouseService.class);
    private static final String SUCCESS = "Warehouse is DUMB!";
    private ConcurrentMap<Integer, Item> _items = new ConcurrentHashMap<Integer, Item>();

    public Item getItem(Integer itemId) {
        return _items.get(itemId);
    }

    public String addItem(Item item) throws Exception {
        if (getItem(item.getItemId()) != null) {
            throw new RuntimeException("Item " + item.getItemId() + " already exists!");
        }
        _items.put(item.getItemId(), item);
        LOGGER.info("Added item " + item.getItemId() + " with name " + item.getName());
        return SUCCESS;
    }

    public String updateItem(Item item) throws Exception {
        Item item2 = _items.get(item.getItemId());
        item2.setName(item.getName());
        LOGGER.info("Updated item " + item.getItemId() + " with name " + item.getName());
        return SUCCESS;
    }

    public String removeItem(Integer itemId) throws Exception {
        if (getItem(itemId) == null) {
            throw new RuntimeException("Item " + itemId + " not found!");
        }
        _items.remove(itemId);
        LOGGER.info("Removed item " +itemId);
        return SUCCESS;
    }

    public Integer getItemCount() {
        return _items.size();
    }
}
