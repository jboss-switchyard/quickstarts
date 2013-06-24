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
package org.switchyard.console.client.ui.application;

import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.switchyard.console.client.model.Application;
import org.switchyard.console.client.ui.common.AbstractDataTable;

import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 * ApplicationsList
 * 
 * Wraps a table control for displaying applications.
 * 
 * @author Rob Cernich
 */
public class ApplicationsList extends AbstractDataTable<Application> {

    /**
     * Create a new ApplicationsList.
     */
    public ApplicationsList() {
        this("Applications");
    }

    /**
     * Create a new ApplicationsList.
     * 
     * @param title the title to display for the list
     */
    public ApplicationsList(String title) {
        super(title);
    }

    @Override
    protected ProvidesKey<Application> createKeyProvider() {
        return new ProvidesKey<Application>() {
            @Override
            public Object getKey(Application item) {
                return item.getName();
            }
        };

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void createColumns(DefaultCellTable<Application> table, ListDataProvider<Application> dataProvider) {
        final TextColumn<Application> nameColumn = new TextColumn<Application>() {
            @Override
            public String getValue(Application application) {
                return application.localName();
            }
        };
        nameColumn.setSortable(true);

        final TextColumn<Application> namespaceColumn = new TextColumn<Application>() {
            @Override
            public String getValue(Application application) {
                return application.namespace();
            }
        };
        namespaceColumn.setSortable(true);

        ColumnSortEvent.ListHandler<Application> sortHandler = new ColumnSortEvent.ListHandler<Application>(
                dataProvider.getList());
        sortHandler.setComparator(nameColumn, createColumnCommparator(nameColumn));
        sortHandler.setComparator(namespaceColumn, createColumnCommparator(namespaceColumn));

        table.addColumn(nameColumn, "Name");
        table.addColumn(namespaceColumn, "Target Namespace");
        table.addColumnSortHandler(sortHandler);

        table.addColumnSortHandler(sortHandler);
        table.getColumnSortList().push(namespaceColumn);
        table.getColumnSortList().push(nameColumn);
    }

}
