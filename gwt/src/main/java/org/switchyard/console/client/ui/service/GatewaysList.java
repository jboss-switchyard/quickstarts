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

import java.util.Collections;
import java.util.List;

import org.jboss.as.console.client.widgets.DefaultWindow;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.switchyard.console.client.model.Binding;
import org.switchyard.console.client.model.Service;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

/**
 * GatewaysList
 * 
 * Wraps a table control for displaying a service's gateways.
 * 
 * @author Rob Cernich
 */
public class GatewaysList {

    private DefaultCellTable<Binding> _gatewaysTable;
    private ListDataProvider<Binding> _gatewaysDataProvider;
    private DefaultWindow _bindingDetailsWindow;
    private BindingDetailsWidget _bindingDetailsWidget;

    GatewaysList() {
        _gatewaysTable = new DefaultCellTable<Binding>(5);

        TextColumn<Binding> typeColumn = new TextColumn<Binding>() {
            @Override
            public String getValue(Binding binding) {
                return binding.getType();
            }
        };
        typeColumn.setSortable(true);

        Column<Binding, String> configColumn = new Column<Binding, String>(new ClickableTextCell()) {
            @Override
            public String getValue(Binding dummy) {
                return "View Configuration...";
            }
        };
        configColumn.setFieldUpdater(new FieldUpdater<Binding, String>() {
            @Override
            public void update(int index, Binding binding, String value) {
                showDetails(binding);
            }
        });
        configColumn.setSortable(false);

        _gatewaysTable.addColumn(typeColumn, "Type");
        _gatewaysTable.addColumn(configColumn, "Configuration");

        _gatewaysDataProvider = new ListDataProvider<Binding>();
        _gatewaysDataProvider.addDataDisplay(_gatewaysTable);

        createBindingDetailsWindow();
    }

    /**
     * @return this object's widget.
     */
    public Widget asWidget() {
        return _gatewaysTable;
    }

    /**
     * @param service the service providing the data.
     */
    public void setService(Service service) {
        List<Binding> gateways = service.getGateways();
        if (gateways == null) {
            gateways = Collections.emptyList();
        }
        _gatewaysDataProvider.setList(gateways);
    }

    private void showDetails(Binding binding) {
        _bindingDetailsWidget.setBinding(binding);
        _bindingDetailsWindow.center();
    }

    private void createBindingDetailsWindow() {
        _bindingDetailsWindow = new DefaultWindow("Gateway Configuration");
        _bindingDetailsWindow.setGlassEnabled(true);
        _bindingDetailsWindow.setAutoHideEnabled(true);
        _bindingDetailsWindow.setAutoHideOnHistoryEventsEnabled(true);
        _bindingDetailsWindow.setWidth(600);
        _bindingDetailsWindow.setHeight(360);

        _bindingDetailsWidget = new BindingDetailsWidget();
        _bindingDetailsWindow.setWidget(_bindingDetailsWidget.asWidget());
    }
}
