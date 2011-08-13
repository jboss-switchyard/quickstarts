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

import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.model.Application;
import org.switchyard.console.client.model.Service;
import org.switchyard.console.client.ui.common.AlwaysFireSingleSelectionModel;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;

/**
 * ServicesList
 * 
 * Wraps a table control for displaying an application's services.
 * 
 * @author Rob Cernich
 */
public class ServicesList {

    private static final ProvidesKey<Service> KEY_PROVIDER = new ProvidesKey<Service>() {
        @Override
        public Object getKey(Service item) {
            return item.getName();
        }
    };

    private String _applicationName;
    private DefaultCellTable<Service> _servicesTable;
    private AlwaysFireSingleSelectionModel<Service> _selectionModel;
    private ListDataProvider<Service> _servicesDataProvider;

    ServicesList() {
        _servicesTable = new DefaultCellTable<Service>(5);

        Column<Service, String> nameColumn = new Column<Service, String>(new ClickableTextCell()) {
            @Override
            public String getValue(Service service) {
                return NameTokens.parseQName(service.getName())[1];
            }
        };
        nameColumn.setFieldUpdater(new FieldUpdater<Service, String>() {
            @Override
            public void update(int index, Service object, String value) {
                History.newItem(NameTokens.createServiceLink(object.getName(), _applicationName));
            }
        });
        nameColumn.setSortable(true);

        TextColumn<Service> promotesColumn = new TextColumn<Service>() {
            @Override
            public String getValue(Service service) {
                return NameTokens.parseQName(service.getPromotedService())[1];
            }
        };
        promotesColumn.setSortable(true);

        _servicesTable.addColumn(nameColumn, "Name");
        _servicesTable.addColumn(promotesColumn, "Promoted Service");

        _selectionModel = new AlwaysFireSingleSelectionModel<Service>();
        _servicesTable.setSelectionModel(_selectionModel);

        _servicesDataProvider = new ListDataProvider<Service>(KEY_PROVIDER);
        _servicesDataProvider.addDataDisplay(_servicesTable);
    }

    /**
     * @return this object's widget.
     */
    public Widget asWidget() {
        return _servicesTable;
    }

    /**
     * Register a selection change handler with the services list.
     * 
     * @param handler the handler
     * @return the {@link HandlerRegistration}
     */
    public HandlerRegistration addSelectionChangeHandler(SelectionChangeEvent.Handler handler) {
        return _selectionModel.addSelectionChangeHandler(handler);
    }

    /**
     * @return the selected service
     */
    public Service getSelection() {
        return _selectionModel.getSelected();
    }

    /**
     * Selects the specified service in the list.
     * 
     * @param service the service to select.
     */
    public void setSelection(Service service) {
        _selectionModel.setSelected(service, true);
    }

    /**
     * @param application the application providing the data.
     */
    public void setApplication(Application application) {
        _applicationName = application.getName();
        List<Service> services = application.getServices();
        if (services == null) {
            services = Collections.emptyList();
        }
        _servicesDataProvider.setList(services);
    }
}
