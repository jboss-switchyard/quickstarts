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
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.model.ComponentReference;
import org.switchyard.console.client.ui.common.AbstractDataTable;

import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 * ComponentReferencesList
 * 
 * Wraps a table control for displaying a service's references.
 * 
 * @author Rob Cernich
 */
public class ComponentReferencesList extends AbstractDataTable<ComponentReference> {

    private static final ProvidesKey<ComponentReference> KEY_PROVIDER = new ProvidesKey<ComponentReference>() {
        @Override
        public Object getKey(ComponentReference item) {
            return item.getName();
        }
    };

    ComponentReferencesList() {
        super(Singleton.MESSAGES.label_references());
    }

    protected void createColumns(DefaultCellTable<ComponentReference> table,
            ListDataProvider<ComponentReference> dataProvider) {
        TextColumn<ComponentReference> nameColumn = new TextColumn<ComponentReference>() {
            @Override
            public String getValue(ComponentReference reference) {
                return reference.localName();
            }
        };
        nameColumn.setSortable(true);

        TextColumn<ComponentReference> interfaceColumn = new TextColumn<ComponentReference>() {
            @Override
            public String getValue(ComponentReference reference) {
                return reference.getInterface();
            }
        };
        interfaceColumn.setSortable(true);

        ColumnSortEvent.ListHandler<ComponentReference> sortHandler = new ColumnSortEvent.ListHandler<ComponentReference>(
                dataProvider.getList());
        sortHandler.setComparator(nameColumn, createColumnCommparator(nameColumn));
        sortHandler.setComparator(interfaceColumn, createColumnCommparator(interfaceColumn));

        table.addColumn(nameColumn, Singleton.MESSAGES.label_name());
        table.addColumn(interfaceColumn, Singleton.MESSAGES.label_interface());

        table.addColumnSortHandler(sortHandler);
        table.getColumnSortList().push(nameColumn);
    }

    @Override
    protected ProvidesKey<ComponentReference> createKeyProvider() {
        return KEY_PROVIDER;
    }

}
