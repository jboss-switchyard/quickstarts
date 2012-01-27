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
        super("Applications");
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
