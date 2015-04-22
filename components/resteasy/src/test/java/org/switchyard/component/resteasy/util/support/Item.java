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

package org.switchyard.component.resteasy.util.support;

/**
 * Item for WarehouseService.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class Item {
    private Integer _itemId;
    private String _name;

    public Item(Integer itemId, String name) {
        _itemId = itemId;
        _name = name;
    }

    public Integer getItemId() {
        return _itemId;
    }

    public void setItemId(Integer itemId) {
        _itemId = itemId;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String toString() {
        return "[" + _itemId + ":" +_name + "]";
    }

    public boolean equals(Item item) {
        return (_itemId == item.getItemId()) && (_name.equals(item.getName()));
    }
}
