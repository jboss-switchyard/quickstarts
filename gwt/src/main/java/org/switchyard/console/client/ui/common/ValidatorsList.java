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
import org.switchyard.console.client.model.Validator;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 * ValidatorsList
 * 
 * Wraps a table control for displaying validators.
 * 
 * @author Rob Cernich
 */
public class ValidatorsList extends AbstractDataTable<Validator> {

    /**
     * Create a new ValidatorsList.
     */
    public ValidatorsList() {
        super(Singleton.MESSAGES.label_validators());
    }

    @Override
    protected void createColumns(DefaultCellTable<Validator> table, ListDataProvider<Validator> dataProvider) {
        TextColumn<Validator> nameColumn = new TextColumn<Validator>() {
            @Override
            public String getValue(Validator validator) {
                return validator.getName();
            }
        };
        nameColumn.setSortable(true);

        TextColumn<Validator> typeColumn = new TextColumn<Validator>() {
            @Override
            public String getValue(Validator validator) {
                return validator.getType();
            }
        };
        typeColumn.setSortable(true);

        ColumnSortEvent.ListHandler<Validator> sortHandler = new ColumnSortEvent.ListHandler<Validator>(
                dataProvider.getList());
        sortHandler.setComparator(nameColumn, createColumnCommparator(nameColumn));
        sortHandler.setComparator(typeColumn, createColumnCommparator(typeColumn));

        table.addColumn(nameColumn, Singleton.MESSAGES.label_name());
        table.addColumn(typeColumn, Singleton.MESSAGES.label_type());

        table.addColumnSortHandler(sortHandler);
        table.getColumnSortList().push(typeColumn);
        table.getColumnSortList().push(nameColumn);

        table.setWidth("100%", false); //$NON-NLS-1$
        table.setColumnWidth(nameColumn, 70, Style.Unit.PCT);
        table.setColumnWidth(typeColumn, 30, Style.Unit.PCT);
    }

    @Override
    protected ProvidesKey<Validator> createKeyProvider() {
        return new ProvidesKey<Validator>() {
            @Override
            public Object getKey(Validator item) {
                return "" + item.getName() + ":" + item.getType(); //$NON-NLS-1$ //$NON-NLS-2$
            }
        };
    }

}
