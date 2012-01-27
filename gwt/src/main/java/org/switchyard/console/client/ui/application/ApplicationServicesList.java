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
import org.switchyard.console.client.NameTokens;
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
        super("Services");
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

        table.addColumn(nameColumn, "Name");
        table.addColumn(promotesColumn, "Promoted Service");

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
