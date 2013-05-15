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
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.switchyard.console.client.model.ComponentService;
import org.switchyard.console.client.model.Service;
import org.switchyard.console.client.ui.common.AbstractDataTable;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;

/**
 * ComponentServicesList
 * 
 * Wraps a table control for displaying an application's component services.
 * 
 * @author Rob Cernich
 */
public class ComponentServicesList extends AbstractDataTable<ComponentService> {

    private static final ProvidesKey<ComponentService> KEY_PROVIDER = new ProvidesKey<ComponentService>() {
        @Override
        public Object getKey(ComponentService item) {
            return item.getName();
        }
    };

    private DefaultWindow _implementationDetailsWindow;
    private ImplementationDetailsWidget _implementationDetailsWidget;

    ComponentServicesList() {
        super("Component Services");
    }

    @Override
    protected void createColumns(DefaultCellTable<ComponentService> table,
            ListDataProvider<ComponentService> dataProvider) {
        TextColumn<ComponentService> nameColumn = new TextColumn<ComponentService>() {
            @Override
            public String getValue(ComponentService service) {
                return service.localName();
            }
        };
        nameColumn.setSortable(true);

        TextColumn<ComponentService> interfaceColumn = new TextColumn<ComponentService>() {
            @Override
            public String getValue(ComponentService service) {
                return service.getInterface();
            }
        };
        interfaceColumn.setSortable(true);

        Column<ComponentService, String> implementationColumn = new Column<ComponentService, String>(
                new ButtonCell()) {
            @Override
            public String getValue(ComponentService dummy) {
                return "View Details...";
            }
        };
        implementationColumn.setFieldUpdater(new FieldUpdater<ComponentService, String>() {
            @Override
            public void update(int index, ComponentService service, String value) {
                showDetails(service);
            }
        });
        implementationColumn.setSortable(false);

        ColumnSortEvent.ListHandler<ComponentService> sortHandler = new ColumnSortEvent.ListHandler<ComponentService>(
                dataProvider.getList());
        sortHandler.setComparator(nameColumn, createColumnCommparator(nameColumn));
        sortHandler.setComparator(interfaceColumn, createColumnCommparator(interfaceColumn));
        sortHandler.setComparator(implementationColumn, createColumnCommparator(implementationColumn));

        table.addColumn(nameColumn, "Name");
        table.addColumn(interfaceColumn, "Interface");
        table.addColumn(implementationColumn, "Implementation");

        table.addColumnSortHandler(sortHandler);
        table.getColumnSortList().push(nameColumn);

        createImplementationsDetailsWindow();
    }

    /**
     * Bind this control to a {@link ApplicationServicesList}.
     * 
     * @param applicationServicesList the {@link ApplicationServicesList} to listen to.
     */
    public void bind(final ApplicationServicesList applicationServicesList) {
        applicationServicesList.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                Service selected = applicationServicesList.getSelection();
                if (selected == null) {
                    return;
                }
                String promotedServiceName = selected.getPromotedService();
                if (promotedServiceName == null) {
                    return;
                }
                for (ComponentService service : getData()) {
                    if (promotedServiceName.equals(KEY_PROVIDER.getKey(service))) {
                        setSelection(service);
                        return;
                    }
                }
                setSelection(null);
            }
        });
    }

    @Override
    protected ProvidesKey<ComponentService> createKeyProvider() {
        return KEY_PROVIDER;
    }

    private void showDetails(ComponentService service) {
        _implementationDetailsWidget.setService(service);
        _implementationDetailsWindow.center();
    }

    private void createImplementationsDetailsWindow() {
        _implementationDetailsWindow = new DefaultWindow("Implementation Details");
        _implementationDetailsWindow.setGlassEnabled(true);
        _implementationDetailsWindow.setAutoHideEnabled(true);
        _implementationDetailsWindow.setAutoHideOnHistoryEventsEnabled(true);
        _implementationDetailsWindow.setWidth(600);
        _implementationDetailsWindow.setHeight(360);

        _implementationDetailsWidget = new ImplementationDetailsWidget();
        _implementationDetailsWindow.setWidget(_implementationDetailsWidget.asWidget());
    }

}
