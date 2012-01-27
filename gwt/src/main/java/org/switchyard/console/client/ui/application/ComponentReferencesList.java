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
package org.switchyard.console.client.ui.application;

import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
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
        super("References");
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

        table.addColumn(nameColumn, "Name");
        table.addColumn(interfaceColumn, "Interface");

        table.addColumnSortHandler(sortHandler);
        table.getColumnSortList().push(nameColumn);
    }

    @Override
    protected ProvidesKey<ComponentReference> createKeyProvider() {
        return KEY_PROVIDER;
    }

}
