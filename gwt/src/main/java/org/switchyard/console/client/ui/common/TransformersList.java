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

import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.model.Transformer;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 * TransformersList
 * 
 * Wraps a table control for displaying transformers.
 * 
 * @author Rob Cernich
 */
public class TransformersList extends AbstractDataTable<Transformer> {

    /**
     * Create a new TransformersList.
     */
    public TransformersList() {
        super(Singleton.MESSAGES.label_transformers());
    }

    @Override
    protected void createColumns(DefaultCellTable<Transformer> table, ListDataProvider<Transformer> dataProvider) {
        TextColumn<Transformer> fromColumn = new TextColumn<Transformer>() {
            @Override
            public String getValue(Transformer transform) {
                return transform.getFrom();
            }
        };
        fromColumn.setSortable(true);

        TextColumn<Transformer> toColumn = new TextColumn<Transformer>() {
            @Override
            public String getValue(Transformer transform) {
                return transform.getTo();
            }
        };
        toColumn.setSortable(true);

        TextColumn<Transformer> typeColumn = new TextColumn<Transformer>() {
            @Override
            public String getValue(Transformer transform) {
                return transform.getType();
            }
        };
        typeColumn.setSortable(true);

        ColumnSortEvent.ListHandler<Transformer> sortHandler = new ColumnSortEvent.ListHandler<Transformer>(
                dataProvider.getList());
        sortHandler.setComparator(fromColumn, createColumnCommparator(fromColumn));
        sortHandler.setComparator(toColumn, createColumnCommparator(toColumn));
        sortHandler.setComparator(typeColumn, createColumnCommparator(typeColumn));

        table.addColumn(fromColumn, Singleton.MESSAGES.label_from());
        table.addColumn(toColumn, Singleton.MESSAGES.label_to());
        table.addColumn(typeColumn, Singleton.MESSAGES.label_type());

        table.addColumnSortHandler(sortHandler);
        table.getColumnSortList().push(typeColumn);
        table.getColumnSortList().push(toColumn);
        table.getColumnSortList().push(fromColumn);

        table.setWidth("100%", false); //$NON-NLS-1$
        table.setColumnWidth(fromColumn, 45, Style.Unit.PCT);
        table.setColumnWidth(toColumn, 45, Style.Unit.PCT);
        table.setColumnWidth(typeColumn, 10, Style.Unit.PCT);
    }

    @Override
    protected ProvidesKey<Transformer> createKeyProvider() {
        return new ProvidesKey<Transformer>() {
            @Override
            public Object getKey(Transformer item) {
                return item.getType() + ":" + item.getFrom() + ":" + item.getTo(); //$NON-NLS-1$ //$NON-NLS-2$
            }
        };
    }

}
