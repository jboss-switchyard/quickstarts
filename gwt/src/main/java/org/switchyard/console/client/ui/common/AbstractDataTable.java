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
        _layout.setStyleName("fill-layout-width");
        _layout.getElement().setAttribute("style", "padding-top:5px;");

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

    protected List<T> getData() {
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

    /**
     * Add column definitions to the table.
     * 
     * @param table the table.
     * @param dataProvider the data provider associated with the table.
     */
    protected abstract void createColumns(DefaultCellTable<T> table, ListDataProvider<T> dataProvider);

}
