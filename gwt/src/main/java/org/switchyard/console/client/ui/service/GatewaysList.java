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

import java.util.EnumSet;

import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.switchyard.console.client.model.Binding;
import org.switchyard.console.client.model.State;
import org.switchyard.console.client.ui.common.AbstractDataTable;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 * GatewaysList
 * 
 * Wraps a table control for displaying a service's gateways.
 * 
 * @author Rob Cernich
 */
public class GatewaysList extends AbstractDataTable<Binding> {

    private DefaultWindow _bindingDetailsWindow;
    private BindingDetailsWidget _bindingDetailsWidget;
    private GatewayPresenter _presenter;

    /**
     * Create a new GatewaysList.
     */
    public GatewaysList() {
        super("Gateways");
    }

    /**
     * @param presenter the presenter.
     */
    public void setPresenter(GatewayPresenter presenter) {
        _presenter = presenter;
    }

    @Override
    protected void createColumns(DefaultCellTable<Binding> table, ListDataProvider<Binding> dataProvider) {
        TextColumn<Binding> nameColumn = new TextColumn<Binding>() {
            @Override
            public String getValue(Binding binding) {
                return binding.getName();
            }
        };
        nameColumn.setSortable(true);

        TextColumn<Binding> typeColumn = new TextColumn<Binding>() {
            @Override
            public String getValue(Binding binding) {
                return binding.getType();
            }
        };
        typeColumn.setSortable(true);

        Column<Binding, String> configColumn = new Column<Binding, String>(new ButtonCell()) {
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

        Column<Binding, String> statusColumn = new TextColumn<Binding>() {
            @Override
            public String getValue(Binding binding) {
                return binding.getState() == null ? "Unknown" : binding.getState().toString();
            }
        };

        Column<Binding, String> startStopColumn = new Column<Binding, String>(new ButtonCell()) {
            @Override
            public String getValue(Binding binding) {
                return binding.getState() == null
                        || EnumSet.<State> of(State.NONE, State.STOPPING).contains(binding.getState()) ? "Start"
                        : "Stop";
            }
        };
        startStopColumn.setFieldUpdater(new FieldUpdater<Binding, String>() {
            @Override
            public void update(int index, Binding binding, String value) {
                if (EnumSet.<State> of(State.STARTING, State.STARTED).contains(binding.getState())) {
                    _presenter.stopGateway(binding);
                } else {
                    _presenter.startGateway(binding);
                }
            }
        });

        ColumnSortEvent.ListHandler<Binding> sortHandler = new ColumnSortEvent.ListHandler<Binding>(
                dataProvider.getList());
        sortHandler.setComparator(typeColumn, createColumnCommparator(typeColumn));
        sortHandler.setComparator(nameColumn, createColumnCommparator(nameColumn));

        table.addColumn(nameColumn, "Name");
        table.addColumn(typeColumn, "Type");
        table.addColumn(statusColumn, "Status");
        table.addColumn(startStopColumn, "Start/Stop");
        table.addColumn(configColumn, "Configuration");

        table.addColumnSortHandler(sortHandler);

        createBindingDetailsWindow();
    }

    @Override
    protected ProvidesKey<Binding> createKeyProvider() {
        return new ProvidesKey<Binding>() {
            @Override
            public Object getKey(Binding item) {
                return item.getType() + ":" + item.getConfiguration();
            }
        };
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
