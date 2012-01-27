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
package org.switchyard.console.client.ui.config;

import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.switchyard.console.client.ui.common.AbstractDataTable;
import org.switchyard.console.components.client.extension.ComponentProviders;
import org.switchyard.console.components.client.internal.ComponentExtensionManager.ComponentProviderProxy;
import org.switchyard.console.components.client.model.Component;

import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.inject.Inject;

/**
 * ComponentsList
 * 
 * Wraps a table control for displaying components.
 * 
 * @author Rob Cernich
 */
public class ComponentsList extends AbstractDataTable<Component> {

    private final ComponentProviders _componentProviders;

    /**
     * Create a new ComponentsList.
     * 
     * @param componentProviders the manager class for component type
     *            extensions.
     */
    @Inject
    public ComponentsList(ComponentProviders componentProviders) {
        super("Installed Components");
        _componentProviders = componentProviders;
    }

    @Override
    protected ProvidesKey<Component> createKeyProvider() {
        return new ProvidesKey<Component>() {
            @Override
            public Object getKey(Component item) {
                return item.getName();
            }
        };

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void createColumns(DefaultCellTable<Component> table, ListDataProvider<Component> dataProvider) {
        final TextColumn<Component> nameColumn = new TextColumn<Component>() {
            @Override
            public String getValue(Component component) {
                ComponentProviderProxy provider = _componentProviders.getExtensionProviderByComponentName(component
                        .getName());
                if (provider == null) {
                    return component.getName();
                }
                return provider.getDisplayName();
            }
        };
        nameColumn.setSortable(true);

        ColumnSortEvent.ListHandler<Component> sortHandler = new ColumnSortEvent.ListHandler<Component>(
                dataProvider.getList());
        sortHandler.setComparator(nameColumn, createColumnCommparator(nameColumn));

        table.addColumn(nameColumn, "Name");

        table.addColumnSortHandler(sortHandler);
        table.getColumnSortList().push(nameColumn);
    }

}
