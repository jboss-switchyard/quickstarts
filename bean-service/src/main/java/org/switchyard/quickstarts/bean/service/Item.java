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

package org.switchyard.quickstarts.bean.service;

public class Item {
    private String _itemId;
    private String _name;
    private int _quantity;

    public String getName() {
        return _name;
    }
    
    public String getItemId() {
        return _itemId;
    }
    public int getQuantity() {
        return _quantity;
    }
    
    public Item setName(String name) {
        _name = name;
        return this;
    }
    
    public Item setItemId(String itemId) {
        _itemId = itemId;
        return this;
    }
    
    public Item setQuantity(int quantity) {
        _quantity = quantity;
        return this;
    }
}
