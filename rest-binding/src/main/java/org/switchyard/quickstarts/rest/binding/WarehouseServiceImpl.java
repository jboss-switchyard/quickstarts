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

package org.switchyard.quickstarts.rest.binding;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.component.bean.Service;
import org.switchyard.component.common.label.EndpointLabel;
import org.switchyard.component.resteasy.composer.RESTEasyContextMapper;

/**
 * A WarehouseService implementation.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
@Service(WarehouseService.class)
public class WarehouseServiceImpl implements WarehouseService {

    @Inject
    private Context context;

    private static final Logger LOGGER = Logger.getLogger(WarehouseService.class);
    private static final String SUCCESS = "SUCCESS";
    private ConcurrentMap<Integer, Item> items = new ConcurrentHashMap<Integer, Item>();

    public Item getItem(Integer itemId) {
        System.out.println("++++++ getItem " + itemId);
        Item item = this.items.get(itemId);
        // TODO: Currently not possible to set property on return path for CDI Beans
        /*if (item == null) {
            context.setProperty(RESTEasyContextMapper.HTTP_RESPONSE_STATUS, 404).addLabels(new String[]{EndpointLabel.HTTP.label()});
        }*/
        return item;
    }

    public String addItem(Item item) throws Exception {
        if (getItem(item.getItemId()) != null) {
            throw new RuntimeException("Item " + item.getItemId() + " already exists!");
        }
        this.items.put(item.getItemId(), item);
        LOGGER.info("Added item " + item.getItemId() + " with name " + item.getName());
        return SUCCESS;
    }

    public String updateItem(Item item) throws Exception {
        Item item2 = this.items.get(item.getItemId());
        item2.setName(item.getName());
        LOGGER.info("Updated item " + item.getItemId() + " with name " + item.getName());
        return SUCCESS;
    }

    public String removeItem(Integer itemId) throws Exception {
        if (getItem(itemId) == null) {
            throw new RuntimeException("Item " + itemId + " not found!");
        }
        this.items.remove(itemId);
        LOGGER.info("Removed item " +itemId);
        return SUCCESS;
    }

    public Integer getItemCount() {
        System.out.println("++++++ getItemCount ");
        return this.items.size();
    }
}
