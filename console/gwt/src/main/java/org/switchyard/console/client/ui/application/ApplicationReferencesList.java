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
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.model.Application;
import org.switchyard.console.client.model.Reference;
import org.switchyard.console.client.ui.common.AbstractDataTable;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 * ApplicationReferencesList
 * 
 * Wraps a table control for displaying an application's references.
 * 
 * @author Rob Cernich
 */
public class ApplicationReferencesList extends AbstractDataTable<Reference> {

    private static final ProvidesKey<Reference> KEY_PROVIDER = new ProvidesKey<Reference>() {
        @Override
        public Object getKey(Reference item) {
            return item.getName();
        }
    };

    private Application _application;
    private final ApplicationPresenter _presenter;

    ApplicationReferencesList(ApplicationPresenter presenter) {
        super(Singleton.MESSAGES.label_references());
        _presenter = presenter;
    }

    @Override
    protected void createColumns(DefaultCellTable<Reference> table, ListDataProvider<Reference> dataProvider) {
        Column<Reference, String> nameColumn = new Column<Reference, String>(new ClickableTextCell()) {
            @Override
            public String getValue(Reference reference) {
                return reference.localName();
            }
        };
        nameColumn.setFieldUpdater(new FieldUpdater<Reference, String>() {
            @Override
            public void update(int index, Reference object, String value) {
                _presenter.onNavigateToReference(object, _application);
            }
        });
        nameColumn.setSortable(true);

        ColumnSortEvent.ListHandler<Reference> sortHandler = new ColumnSortEvent.ListHandler<Reference>(
                dataProvider.getList());
        sortHandler.setComparator(nameColumn, createColumnCommparator(nameColumn));

        table.addColumn(nameColumn, Singleton.MESSAGES.label_name());

        table.addColumnSortHandler(sortHandler);
        table.getColumnSortList().push(nameColumn);
    }

    /**
     * @param application the application providing the data.
     */
    public void setApplication(Application application) {
        _application = application;
        setData(application.getReferences());
    }

    @Override
    protected ProvidesKey<Reference> createKeyProvider() {
        return KEY_PROVIDER;
    }

}
