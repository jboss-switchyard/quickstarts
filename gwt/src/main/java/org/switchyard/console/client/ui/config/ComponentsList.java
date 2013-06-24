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
