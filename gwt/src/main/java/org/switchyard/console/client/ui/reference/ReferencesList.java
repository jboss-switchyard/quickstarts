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
package org.switchyard.console.client.ui.reference;

import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.switchyard.console.client.model.Reference;
import org.switchyard.console.client.ui.common.AbstractDataTable;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 * ReferencesList
 * 
 * Wraps a table control for displaying an application's references.
 */
public class ReferencesList extends AbstractDataTable<Reference> {

    private static final ProvidesKey<Reference> KEY_PROVIDER = new ProvidesKey<Reference>() {
        @Override
        public Object getKey(Reference item) {
            return item.getName();
        }
    };

    /**
     * Create a new ReferencesList.
     */
    public ReferencesList() {
        super("References");
    }

    @Override
    protected void createColumns(DefaultCellTable<Reference> table, ListDataProvider<Reference> dataProvider) {
        Column<Reference, String> nameColumn = new Column<Reference, String>(new ClickableTextCell()) {
            @Override
            public String getValue(Reference reference) {
                return reference.localName();
            }
        };
        nameColumn.setSortable(true);

        final TextColumn<Reference> namespaceColumn = new TextColumn<Reference>() {
            @Override
            public String getValue(Reference reference) {
                return reference.namespace();
            }
        };
        namespaceColumn.setSortable(true);

        ColumnSortEvent.ListHandler<Reference> sortHandler = new ColumnSortEvent.ListHandler<Reference>(
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
    protected ProvidesKey<Reference> createKeyProvider() {
        return KEY_PROVIDER;
    }

}
