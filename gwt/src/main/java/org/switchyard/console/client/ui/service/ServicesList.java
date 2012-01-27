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

    ServicesList() {
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
