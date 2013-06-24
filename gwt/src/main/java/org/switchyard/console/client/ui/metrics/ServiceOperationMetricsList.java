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
import org.switchyard.console.client.model.OperationMetrics;
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
 * ServiceOperationMetricsList
 * 
 * Wraps a table control for displaying metrics for services referenced by a
 * service.
 * 
 * @author Rob Cernich
 */
public class ServiceOperationMetricsList extends AbstractDataTable<OperationMetrics> {

    private static final ProvidesKey<OperationMetrics> KEY_PROVIDER = new ProvidesKey<OperationMetrics>() {
        @Override
        public Object getKey(OperationMetrics item) {
            return item.getName();
        }
    };

    private ServiceMetrics _serviceMetrics;

    /**
     * Create a new ServiceReferenceMetricsList.
     */
    public ServiceOperationMetricsList() {
        super("Service Operation Metrics");
    }

    @Override
    protected void createColumns(DefaultCellTable<OperationMetrics> table,
            ListDataProvider<OperationMetrics> dataProvider) {
        final TextColumn<OperationMetrics> nameColumn = new TextColumn<OperationMetrics>() {
            @Override
            public String getValue(OperationMetrics metrics) {
                return metrics.getName();
            }
        };
        nameColumn.setSortable(true);

        final Column<OperationMetrics, Number> countColumn = new Column<OperationMetrics, Number>(new NumberCell()) {
            @Override
            public Number getValue(OperationMetrics metrics) {
                return metrics.getTotalCount();
            }
        };
        countColumn.setSortable(true);

        final Column<OperationMetrics, Number> averageTimeColumn = new Column<OperationMetrics, Number>(
                new NumberCell()) {
            @Override
            public Number getValue(OperationMetrics metrics) {
                return metrics.getAverageProcessingTime();
            }
        };
        averageTimeColumn.setSortable(true);

        final Column<OperationMetrics, Double> totalTimePercentageColumn = new Column<OperationMetrics, Double>(
                new PercentageBarCell()) {
            @Override
            public Double getValue(OperationMetrics metrics) {
                if (_serviceMetrics == null || _serviceMetrics.getTotalProcessingTime() == 0) {
                    return 0.0;
                }
                return metrics.getTotalProcessingTime() / (double) _serviceMetrics.getTotalProcessingTime();
            }
        };
        totalTimePercentageColumn.setSortable(true);

        final Column<OperationMetrics, Double> faultPercentageColumn = new Column<OperationMetrics, Double>(
                new PercentageBarCell()) {
            @Override
            public Double getValue(OperationMetrics metrics) {
                if (metrics.getTotalCount() == 0) {
                    return 0.0;
                }
                return metrics.getFaultCount() / (double) metrics.getTotalCount();
            }
        };
        faultPercentageColumn.setSortable(true);

        ColumnSortEvent.ListHandler<OperationMetrics> sortHandler = new ColumnSortEvent.ListHandler<OperationMetrics>(
                dataProvider.getList());
        sortHandler.setComparator(nameColumn, createColumnCommparator(nameColumn));
        sortHandler.setComparator(countColumn, createNumberColumnCommparator(countColumn));
        sortHandler.setComparator(averageTimeColumn, createNumberColumnCommparator(averageTimeColumn));
        sortHandler.setComparator(totalTimePercentageColumn, createNumberColumnCommparator(totalTimePercentageColumn));
        sortHandler.setComparator(faultPercentageColumn, createNumberColumnCommparator(faultPercentageColumn));

        table.addColumn(nameColumn, "Name");
        table.addColumn(countColumn, "Message Count");
        table.addColumn(averageTimeColumn, "Average Time");
        table.addColumn(totalTimePercentageColumn, "Time %");
        table.addColumn(faultPercentageColumn, "Fault %");

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
            setData(serviceMetrics.getOperations());
        }
    }

    @Override
    protected ProvidesKey<OperationMetrics> createKeyProvider() {
        return KEY_PROVIDER;
    }

}
