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

import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
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
        super("Validators");
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

        table.addColumn(nameColumn, "Name");
        table.addColumn(typeColumn, "Type");

        table.addColumnSortHandler(sortHandler);
        table.getColumnSortList().push(typeColumn);
        table.getColumnSortList().push(nameColumn);

        table.setWidth("100%", false);
        table.setColumnWidth(nameColumn, 70, Style.Unit.PCT);
        table.setColumnWidth(typeColumn, 30, Style.Unit.PCT);
    }

    @Override
    protected ProvidesKey<Validator> createKeyProvider() {
        return new ProvidesKey<Validator>() {
            @Override
            public Object getKey(Validator item) {
                return "" + item.getName() + ":" + item.getType();
            }
        };
    }

}
