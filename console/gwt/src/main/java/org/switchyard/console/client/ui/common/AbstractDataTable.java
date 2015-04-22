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

import java.util.Comparator;
import java.util.List;

import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * AbstractDataTable
 * 
 * Wraps a table control for displaying data.
 * 
 * @param <T> the type of data displayed by the table.
 * 
 * @author Rob Cernich
 */
public abstract class AbstractDataTable<T> {

    private VerticalPanel _layout;
    private SingleSelectionModel<T> _selectionModel;
    private DefaultCellTable<T> _table;
    private ListDataProvider<T> _dataProvider;

    protected AbstractDataTable(String label) {
        _layout = new VerticalPanel();
        _layout.setStyleName("fill-layout-width"); //$NON-NLS-1$
        _layout.getElement().setAttribute("style", "padding-top:5px;"); //$NON-NLS-1$ //$NON-NLS-2$

        _table = new DefaultCellTable<T>(5);

        ProvidesKey<T> keyProvider = createKeyProvider();

        _selectionModel = createSelectionModel(keyProvider);
        _table.setSelectionModel(_selectionModel);

        _dataProvider = new ListDataProvider<T>(keyProvider);
        _dataProvider.addDataDisplay(_table);

        createColumns(_table, _dataProvider);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(_table);

        _layout.add(new ContentGroupLabel(label));
        _layout.add(_table);
        _layout.add(pager);
    }

    /**
     * @return this object's widget.
     */
    public Widget asWidget() {
        return _layout;
    }

    /**
     * Register a selection change handler with the list.
     * 
     * @param handler the handler
     * @return the {@link HandlerRegistration}
     */
    public HandlerRegistration addSelectionChangeHandler(SelectionChangeEvent.Handler handler) {
        return _selectionModel.addSelectionChangeHandler(handler);
    }

    /**
     * @return the selected item
     */
    public T getSelection() {
        return _selectionModel.getSelectedObject();
    }

    /**
     * Selects the specified item in the list.
     * 
     * @param selection the item to select.
     */
    public void setSelection(T selection) {
        _selectionModel.setSelected(selection, true);
    }

    /**
     * @param data the data.
     */
    public void setData(List<T> data) {
        _dataProvider.getList().clear();
        if (data != null) {
            _dataProvider.getList().addAll(data);
            ColumnSortEvent.fire(_table, _table.getColumnSortList());
        }
    }

    /**
     * @return the data associated with the list.
     */
    public List<T> getData() {
        return _dataProvider.getList();
    }

    /**
     * @return a new key provider for the table.
     */
    protected abstract ProvidesKey<T> createKeyProvider();

    protected SingleSelectionModel<T> createSelectionModel(ProvidesKey<T> keyProvider) {
        return new AlwaysFireSingleSelectionModel<T>(keyProvider);
    }

    protected Comparator<T> createColumnCommparator(final Column<T, String> column) {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return column.getValue(o1).compareToIgnoreCase(column.getValue(o2));
            }
        };
    }

    protected Comparator<T> createNumberColumnCommparator(final Column<T, ? extends Number> column) {
        return new Comparator<T>() {
            @SuppressWarnings({"rawtypes", "unchecked" })
            @Override
            public int compare(T o1, T o2) {
                return ((Comparable) column.getValue(o1)).compareTo((Comparable) column.getValue(o2));
            }
        };
    }

    /**
     * Add column definitions to the table.
     * 
     * @param table the table.
     * @param dataProvider the data provider associated with the table.
     */
    protected abstract void createColumns(DefaultCellTable<T> table, ListDataProvider<T> dataProvider);

}
