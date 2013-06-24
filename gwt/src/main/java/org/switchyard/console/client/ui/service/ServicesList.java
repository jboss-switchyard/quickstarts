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
package org.switchyard.console.client.ui.service;

import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.switchyard.console.client.model.Service;
import org.switchyard.console.client.ui.common.AbstractDataTable;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 * ServicesList
 * 
 * Wraps a table control for displaying an application's services.
 * 
 * @author Rob Cernich
 */
public class ServicesList extends AbstractDataTable<Service> {

    private static final ProvidesKey<Service> KEY_PROVIDER = new ProvidesKey<Service>() {
        @Override
        public Object getKey(Service item) {
            return item.getName();
        }
    };

    /**
     * Create a new ServicesList.
     */
    public ServicesList() {
        super("Services");
    }

    @Override
    protected void createColumns(DefaultCellTable<Service> table, ListDataProvider<Service> dataProvider) {
        Column<Service, String> nameColumn = new Column<Service, String>(new ClickableTextCell()) {
            @Override
            public String getValue(Service service) {
                return service.localName();
            }
        };
        nameColumn.setSortable(true);

        final TextColumn<Service> namespaceColumn = new TextColumn<Service>() {
            @Override
            public String getValue(Service service) {
                return service.namespace();
            }
        };
        namespaceColumn.setSortable(true);

        ColumnSortEvent.ListHandler<Service> sortHandler = new ColumnSortEvent.ListHandler<Service>(
                dataProvider.getList());
        sortHandler.setComparator(nameColumn, createColumnCommparator(nameColumn));
        sortHandler.setComparator(namespaceColumn, createColumnCommparator(namespaceColumn));

        table.addColumn(nameColumn, "Name");
        table.addColumn(namespaceColumn, "Target Namespace");

        table.addColumnSortHandler(sortHandler);
        table.getColumnSortList().push(namespaceColumn);
        table.getColumnSortList().push(nameColumn);
    }

    @Override
    protected ProvidesKey<Service> createKeyProvider() {
        return KEY_PROVIDER;
    }

}
