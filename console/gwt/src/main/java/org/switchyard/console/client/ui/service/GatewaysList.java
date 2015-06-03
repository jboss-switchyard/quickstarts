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
package org.switchyard.console.client.ui.service;

import java.util.EnumSet;

import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.switchyard.console.client.Singleton;
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
        super(Singleton.MESSAGES.label_gateways());
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
                return Singleton.MESSAGES.button_viewConfiguration();
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
                return binding.getState() == null ? Singleton.MESSAGES.constant_unknown() : binding.getState().toString();
            }
        };

        Column<Binding, String> startStopColumn = new Column<Binding, String>(new ButtonCell()) {
            @Override
            public String getValue(Binding binding) {
                return binding.getState() == null
                        || EnumSet.<State> of(State.NONE, State.STOPPING).contains(binding.getState()) ? Singleton.MESSAGES.label_start()
                        : Singleton.MESSAGES.label_stop();
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

        table.addColumn(nameColumn, Singleton.MESSAGES.label_name());
        table.addColumn(typeColumn, Singleton.MESSAGES.label_type());
        table.addColumn(statusColumn, Singleton.MESSAGES.label_status());
        table.addColumn(startStopColumn, Singleton.MESSAGES.label_startStop());
        table.addColumn(configColumn, Singleton.MESSAGES.label_configuration());

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
        _bindingDetailsWindow = new DefaultWindow(Singleton.MESSAGES.label_gatewayConfiguration());
        _bindingDetailsWindow.setGlassEnabled(true);
        _bindingDetailsWindow.setAutoHideEnabled(true);
        _bindingDetailsWindow.setAutoHideOnHistoryEventsEnabled(true);
        _bindingDetailsWindow.setWidth(600);
        _bindingDetailsWindow.setHeight(360);

        _bindingDetailsWidget = new BindingDetailsWidget();
        _bindingDetailsWindow.setWidget(_bindingDetailsWidget.asWidget());
    }

}
