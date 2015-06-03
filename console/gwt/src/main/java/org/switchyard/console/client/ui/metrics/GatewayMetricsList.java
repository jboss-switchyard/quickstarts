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
package org.switchyard.console.client.ui.metrics;

import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.model.GatewayMetrics;
import org.switchyard.console.client.model.ServiceMetrics;
import org.switchyard.console.client.ui.common.AbstractDataTable;
import org.switchyard.console.client.ui.common.PercentageBarCell;

import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 * GatewayMetricsList
 * 
 * Wraps a table control for displaying metrics for gateways on a
 * service/reference.
 */
public class GatewayMetricsList extends AbstractDataTable<GatewayMetrics> {

    private static final ProvidesKey<GatewayMetrics> KEY_PROVIDER = new ProvidesKey<GatewayMetrics>() {
        @Override
        public Object getKey(GatewayMetrics item) {
            return item.getName();
        }
    };

    private ServiceMetrics _serviceMetrics;

    /**
     * Create a new GatewayMetricsList.
     */
    public GatewayMetricsList() {
        super(Singleton.MESSAGES.label_gatewayMetrics());
    }

    @Override
    protected void createColumns(DefaultCellTable<GatewayMetrics> table,
            ListDataProvider<GatewayMetrics> dataProvider) {
        final TextColumn<GatewayMetrics> nameColumn = new TextColumn<GatewayMetrics>() {
            @Override
            public String getValue(GatewayMetrics metrics) {
                return metrics.getName();
            }
        };
        nameColumn.setSortable(true);

        final TextColumn<GatewayMetrics> typeColumn = new TextColumn<GatewayMetrics>() {
            @Override
            public String getValue(GatewayMetrics metrics) {
                return metrics.getType();
            }
        };
        typeColumn.setSortable(true);

        final Column<GatewayMetrics, Number> countColumn = new Column<GatewayMetrics, Number>(new NumberCell()) {
            @Override
            public Number getValue(GatewayMetrics metrics) {
                return metrics.getTotalCount();
            }
        };
        countColumn.setSortable(true);

        final Column<GatewayMetrics, Number> averageTimeColumn = new Column<GatewayMetrics, Number>(
                new NumberCell()) {
            @Override
            public Number getValue(GatewayMetrics metrics) {
                return metrics.getAverageProcessingTime();
            }
        };
        averageTimeColumn.setSortable(true);

        final Column<GatewayMetrics, Double> totalTimePercentageColumn = new Column<GatewayMetrics, Double>(
                new PercentageBarCell()) {
            @Override
            public Double getValue(GatewayMetrics metrics) {
                if (_serviceMetrics == null || _serviceMetrics.getTotalProcessingTime() == 0) {
                    return 0.0;
                }
                return metrics.getTotalProcessingTime() / (double) _serviceMetrics.getTotalProcessingTime();
            }
        };
        totalTimePercentageColumn.setSortable(true);

        final Column<GatewayMetrics, Double> faultPercentageColumn = new Column<GatewayMetrics, Double>(
                new PercentageBarCell()) {
            @Override
            public Double getValue(GatewayMetrics metrics) {
                if (metrics.getTotalCount() == 0) {
                    return 0.0;
                }
                return metrics.getFaultCount() / (double) metrics.getTotalCount();
            }
        };
        faultPercentageColumn.setSortable(true);

        ColumnSortEvent.ListHandler<GatewayMetrics> sortHandler = new ColumnSortEvent.ListHandler<GatewayMetrics>(
                dataProvider.getList());
        sortHandler.setComparator(nameColumn, createColumnCommparator(nameColumn));
        sortHandler.setComparator(typeColumn, createColumnCommparator(typeColumn));
        sortHandler.setComparator(countColumn, createNumberColumnCommparator(countColumn));
        sortHandler.setComparator(averageTimeColumn, createNumberColumnCommparator(averageTimeColumn));
        sortHandler.setComparator(totalTimePercentageColumn, createNumberColumnCommparator(totalTimePercentageColumn));
        sortHandler.setComparator(faultPercentageColumn, createNumberColumnCommparator(faultPercentageColumn));

        table.addColumn(nameColumn, Singleton.MESSAGES.label_name());
        table.addColumn(typeColumn, Singleton.MESSAGES.label_type());
        table.addColumn(countColumn, Singleton.MESSAGES.label_messageCount());
        table.addColumn(averageTimeColumn, Singleton.MESSAGES.label_averageTime());
        table.addColumn(totalTimePercentageColumn, Singleton.MESSAGES.label_timePercent());
        table.addColumn(faultPercentageColumn, Singleton.MESSAGES.label_faultPercent());

        table.addColumnSortHandler(sortHandler);
        table.getColumnSortList().push(averageTimeColumn);
        table.getColumnSortList().push(countColumn);
        table.getColumnSortList().push(totalTimePercentageColumn);
        table.getColumnSortList().push(faultPercentageColumn);
        table.getColumnSortList().push(nameColumn);
    }

    /**
     * @param serviceMetrics the parent service's metrics, containing a list of
     *            reference metrics.
     */
    public void setServiceMetrics(ServiceMetrics serviceMetrics) {
        _serviceMetrics = serviceMetrics;
        if (serviceMetrics == null) {
            setData(null);
        } else {
            setData(serviceMetrics.getGateways());
        }
    }

    @Override
    protected ProvidesKey<GatewayMetrics> createKeyProvider() {
        return KEY_PROVIDER;
    }

}
