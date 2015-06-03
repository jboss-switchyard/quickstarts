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
package org.switchyard.console.client.ui.application;

import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.model.Application;
import org.switchyard.console.client.model.Service;
import org.switchyard.console.client.ui.common.AbstractDataTable;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 * ApplicationServicesList
 * 
 * Wraps a table control for displaying an application's services.
 * 
 * @author Rob Cernich
 */
public class ApplicationServicesList extends AbstractDataTable<Service> {

    private static final ProvidesKey<Service> KEY_PROVIDER = new ProvidesKey<Service>() {
        @Override
        public Object getKey(Service item) {
            return item.getName();
        }
    };

    private Application _application;
    private final ApplicationPresenter _presenter;

    ApplicationServicesList(ApplicationPresenter presenter) {
        super(Singleton.MESSAGES.label_services());
        _presenter = presenter;
    }

    @Override
    protected void createColumns(DefaultCellTable<Service> table, ListDataProvider<Service> dataProvider) {
        Column<Service, String> nameColumn = new Column<Service, String>(new ClickableTextCell()) {
            @Override
            public String getValue(Service service) {
                return service.localName();
            }
        };
        nameColumn.setFieldUpdater(new FieldUpdater<Service, String>() {
            @Override
            public void update(int index, Service object, String value) {
                _presenter.onNavigateToService(object, _application);
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

        ColumnSortEvent.ListHandler<Service> sortHandler = new ColumnSortEvent.ListHandler<Service>(
                dataProvider.getList());
        sortHandler.setComparator(nameColumn, createColumnCommparator(nameColumn));
        sortHandler.setComparator(promotesColumn, createColumnCommparator(promotesColumn));

        table.addColumn(nameColumn, Singleton.MESSAGES.label_name());
        table.addColumn(promotesColumn, Singleton.MESSAGES.label_promotedService());

        table.addColumnSortHandler(sortHandler);
        table.getColumnSortList().push(nameColumn);
    }

    /**
     * @param application the application providing the data.
     */
    public void setApplication(Application application) {
        _application = application;
        setData(application.getServices());
    }

    @Override
    protected ProvidesKey<Service> createKeyProvider() {
        return KEY_PROVIDER;
    }

}
