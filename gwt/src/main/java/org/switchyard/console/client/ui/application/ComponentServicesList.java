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

import java.util.Collections;
import java.util.List;

import org.jboss.as.console.client.widgets.DefaultWindow;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.model.Application;
import org.switchyard.console.client.model.ComponentService;
import org.switchyard.console.client.model.Service;
import org.switchyard.console.client.ui.common.AlwaysFireSingleSelectionModel;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
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
public class ComponentServicesList {

    private static final ProvidesKey<ComponentService> KEY_PROVIDER = new ProvidesKey<ComponentService>() {
        @Override
        public Object getKey(ComponentService item) {
            return item.getName();
        }
    };

    private DefaultCellTable<ComponentService> _servicesTable;
    private ListDataProvider<ComponentService> _servicesDataProvider;
    private AlwaysFireSingleSelectionModel<ComponentService> _selectionModel;
    private DefaultWindow _implementationDetailsWindow;
    private ImplementationDetailsWidget _implementationDetailsWidget;

    ComponentServicesList() {
        _servicesTable = new DefaultCellTable<ComponentService>(5);

        TextColumn<ComponentService> nameColumn = new TextColumn<ComponentService>() {
            @Override
            public String getValue(ComponentService service) {
                return NameTokens.parseQName(service.getName())[1];
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

        Column<ComponentService, String> implementationColumn = new Column<ComponentService, String>(new ClickableTextCell()) {
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

        _servicesTable.addColumn(nameColumn, "Name");
        _servicesTable.addColumn(interfaceColumn, "Interface");
        _servicesTable.addColumn(implementationColumn, "Implementation");

        _selectionModel = new AlwaysFireSingleSelectionModel<ComponentService>(KEY_PROVIDER);
        _servicesTable.setSelectionModel(_selectionModel);

        _servicesDataProvider = new ListDataProvider<ComponentService>(KEY_PROVIDER);
        _servicesDataProvider.addDataDisplay(_servicesTable);

        createImplementationsDetailsWindow();
    }

    /**
     * @return this object's widget.
     */
    public Widget asWidget() {
        return _servicesTable;
    }

    /**
     * Bind this control to a {@link ServicesList}.
     * 
     * @param servicesList the {@link ServicesList} to listen to.
     */
    public void bind(final ServicesList servicesList) {
        servicesList.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                Service selected = servicesList.getSelection();
                if (selected == null) {
                    return;
                }
                String promotedServiceName = selected.getPromotedService();
                if (promotedServiceName == null) {
                    return;
                }
                for (ComponentService service : _servicesDataProvider.getList()) {
                    if (promotedServiceName.equals(_servicesDataProvider.getKey(service))) {
                        _selectionModel.setSelected(service, true);
                        return;
                    }
                }
                _selectionModel.setSelected(null, true);
            }
        });
    }

    /**
     * @param application the application providing the data.
     */
    public void setApplication(Application application) {
        List<ComponentService> services = application.getComponentServices();
        if (services == null) {
            services = Collections.emptyList();
        }
        _servicesDataProvider.setList(services);
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
