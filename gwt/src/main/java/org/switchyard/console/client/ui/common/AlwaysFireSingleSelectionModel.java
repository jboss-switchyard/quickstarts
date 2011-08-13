/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.console.client.ui.common;

import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel.AbstractSelectionModel;

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
public class AlwaysFireSingleSelectionModel<T> extends AbstractSelectionModel<T> {

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
    public T getSelected() {
        return _selected ? _selection : null;
    }

    @Override
    public void setSelected(T object, boolean selected) {
        _selection = object;
        _selected = selected;
        scheduleSelectionChangeEvent();
    }

}
