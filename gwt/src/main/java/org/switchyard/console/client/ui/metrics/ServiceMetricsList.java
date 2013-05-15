/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.console.client.ui.metrics;

import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.switchyard.console.client.model.MessageMetrics;
import org.switchyard.console.client.model.ServiceMetrics;
import org.switchyard.console.client.ui.common.AbstractDataTable;
import org.switchyard.console.client.ui.common.PercentageBarCell;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 * ServiceReferenceMetricsList
 * 
 * Wraps a table control for displaying metrics for services referenced by a
 * service.
 * 
 * @author Rob Cernich
 */
public class ServiceMetricsList extends AbstractDataTable<ServiceMetrics> {

    private static final ProvidesKey<ServiceMetrics> KEY_PROVIDER = new ProvidesKey<ServiceMetrics>() {
        @Override
        public Object getKey(ServiceMetrics item) {
            return item.getName();
        }
    };

    private MessageMetrics _systemMetrics;
    private MetricsPresenter _presenter;
    private DefaultWindow _detailsWindow;
    private MetricsDetailsWidget _detailsWidget;
    private String _title;

    /**
     * Create a new ServiceReferenceMetricsList.
     * 
     * @param title for the list
     */
    public ServiceMetricsList(String title) {
        super(title);
        _title = title;
    }

    /**
     * @param presenter the presenter.
     */
    public void setPresenter(MetricsPresenter presenter) {
        _presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        createDetailsWindow();
        return super.asWidget();
    }

    @Override
    protected void createColumns(DefaultCellTable<ServiceMetrics> table, ListDataProvider<ServiceMetrics> dataProvider) {
        final TextColumn<ServiceMetrics> nameColumn = new TextColumn<ServiceMetrics>() {
            @Override
            public String getValue(ServiceMetrics metrics) {
                return metrics.localName();
            }
        };
        nameColumn.setSortable(true);

        final TextColumn<ServiceMetrics> namespaceColumn = new TextColumn<ServiceMetrics>() {
            @Override
            public String getValue(ServiceMetrics service) {
                return service.namespace();
            }
        };
        namespaceColumn.setSortable(true);

        final Column<ServiceMetrics, Number> countColumn = new Column<ServiceMetrics, Number>(new NumberCell()) {
            @Override
            public Number getValue(ServiceMetrics metrics) {
                return metrics.getTotalCount();
            }
        };
        countColumn.setSortable(true);

        final Column<ServiceMetrics, Number> averageTimeColumn = new Column<ServiceMetrics, Number>(new NumberCell()) {
            @Override
            public Number getValue(ServiceMetrics metrics) {
                return metrics.getAverageProcessingTime();
            }
        };
        averageTimeColumn.setSortable(true);

        final Column<ServiceMetrics, Double> totalTimePercentageColumn = new Column<ServiceMetrics, Double>(
                new PercentageBarCell()) {
            @Override
            public Double getValue(ServiceMetrics metrics) {
                if (_systemMetrics == null || _systemMetrics.getTotalProcessingTime() == 0) {
                    return 0.0;
                }
                return metrics.getTotalProcessingTime() / (double) _systemMetrics.getTotalProcessingTime();
            }
        };
        totalTimePercentageColumn.setSortable(true);

        final Column<ServiceMetrics, Double> faultPercentageColumn = new Column<ServiceMetrics, Double>(
                new PercentageBarCell()) {
            @Override
            public Double getValue(ServiceMetrics metrics) {
                if (_systemMetrics == null || _systemMetrics.getFaultCount() == 0) {
                    return 0.0;
                }
                return metrics.getFaultCount() / (double) _systemMetrics.getFaultCount();
            }
        };
        faultPercentageColumn.setSortable(true);


        Column<ServiceMetrics, String> viewDetailsColumn = new Column<ServiceMetrics, String>(new ButtonCell()) {
            @Override
            public String getValue(ServiceMetrics dummy) {
                return "Details...";
            }
        };
        viewDetailsColumn.setFieldUpdater(new FieldUpdater<ServiceMetrics, String>() {
            @Override
            public void update(int index, ServiceMetrics metrics, String value) {
                showDetails(metrics);
            }
        });

        Column<ServiceMetrics, String> clearColumn = new Column<ServiceMetrics, String>(new ButtonCell()) {
            @Override
            public String getValue(ServiceMetrics dummy) {
                return "Reset Metrics";
            }
        };
        clearColumn.setFieldUpdater(new FieldUpdater<ServiceMetrics, String>() {
            @Override
            public void update(int index, ServiceMetrics metrics, String value) {
                _presenter.resetMetrics(metrics);
            }
        });

        ColumnSortEvent.ListHandler<ServiceMetrics> sortHandler = new ColumnSortEvent.ListHandler<ServiceMetrics>(
                dataProvider.getList());
        sortHandler.setComparator(nameColumn, createColumnCommparator(nameColumn));
        sortHandler.setComparator(namespaceColumn, createColumnCommparator(namespaceColumn));
        sortHandler.setComparator(countColumn, createNumberColumnCommparator(countColumn));
        sortHandler.setComparator(averageTimeColumn, createNumberColumnCommparator(averageTimeColumn));
        sortHandler.setComparator(totalTimePercentageColumn, createNumberColumnCommparator(totalTimePercentageColumn));
        sortHandler.setComparator(faultPercentageColumn, createNumberColumnCommparator(faultPercentageColumn));

        table.addColumn(nameColumn, "Name");
        table.addColumn(namespaceColumn, "Target Namespace");
        table.addColumn(countColumn, "Message Count");
        table.addColumn(averageTimeColumn, "Average Time");
        table.addColumn(totalTimePercentageColumn, "Time %");
        table.addColumn(faultPercentageColumn, "Fault %");
        table.addColumn(viewDetailsColumn, "Details");
        table.addColumn(clearColumn, "Reset");

        table.addColumnSortHandler(sortHandler);
        table.getColumnSortList().push(averageTimeColumn);
        table.getColumnSortList().push(countColumn);
        table.getColumnSortList().push(faultPercentageColumn);
        table.getColumnSortList().push(namespaceColumn);
        table.getColumnSortList().push(nameColumn);
        // insert twice for descending
        table.getColumnSortList().push(totalTimePercentageColumn);
        table.getColumnSortList().push(totalTimePercentageColumn);
    }

    /**
     * @param systemMetrics the system metrics.
     */
    public void setSystemMetrics(MessageMetrics systemMetrics) {
        _systemMetrics = systemMetrics;
    }

    @Override
    protected ProvidesKey<ServiceMetrics> createKeyProvider() {
        return KEY_PROVIDER;
    }

    /**
     * create the details widget
     */
    protected MetricsDetailsWidget createDetailsWidget() {
        return new ServiceDetailsWidget();
    }

    private void showDetails(ServiceMetrics metrics) {
        _detailsWidget.setMetrics(metrics, _systemMetrics);
        _detailsWindow.center();
    }

    private void createDetailsWindow() {
        _detailsWindow = new DefaultWindow(_title);
        _detailsWindow.setGlassEnabled(true);
        _detailsWindow.setAutoHideEnabled(true);
        _detailsWindow.setAutoHideOnHistoryEventsEnabled(true);
        _detailsWindow.setWidth("80%");
        _detailsWindow.setHeight("80%");

        _detailsWidget = createDetailsWidget();
        _detailsWindow.setWidget(_detailsWidget.asWidget());
    }

}
