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
package org.switchyard.console.client.ui.common;

import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * AlwaysFireSingleSelectionModel
 * 
 * A selection model similar to SingleSelectionModel except that a selection
 * changed event is fired every time the selection is set.
 * 
 * @param <T> the data type of records in the list
 * 
 * @author Rob Cernich
 */
public class AlwaysFireSingleSelectionModel<T> extends SingleSelectionModel<T> {

    private T _selection;
    private boolean _selected;

    /**
     * Create a new AlwaysFireSingleSelectionModel.
     */
    public AlwaysFireSingleSelectionModel() {
        super(null);
    }

    /**
     * Create a new AlwaysFireSingleSelectionModel.
     * 
     * @param keyProvider the {@link ProvidesKey} to use.
     */
    public AlwaysFireSingleSelectionModel(ProvidesKey<T> keyProvider) {
        super(keyProvider);
    }

    @Override
    public boolean isSelected(T object) {
        Object objectKey = getKey(object);
        Object selectedKey = getKey(_selection);
        return objectKey == null ? selectedKey == null : objectKey.equals(selectedKey) && _selected;
    }

    /**
     * @return the current selection
     */
    @Override
    public T getSelectedObject() {
        return _selected ? _selection : null;
    }

    @Override
    public void setSelected(T object, boolean selected) {
        _selection = object;
        _selected = selected;
        scheduleSelectionChangeEvent();
    }

    @Override
    protected void fireSelectionChangeEvent() {
        if (isEventScheduled()) {
            setEventCancelled(true);
        }
        SelectionChangeEvent.fire(this);
    }

}
