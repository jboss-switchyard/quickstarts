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

package org.switchyard.quickstarts.rest.binding;

/**
 * Interface for OrderService.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public interface OrderService {

    public Order newOrder();
    public Order getOrder(Integer orderId) throws Exception;
    public String addItems(Order order) throws Exception;
    public String removeItem(String orderItemId) throws Exception;
    public String createInventory() throws Exception;
    public String updateInventory() throws Exception;
    public String removeInventory() throws Exception;
    public Boolean isInventorySetup();
}
