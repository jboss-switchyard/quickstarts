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
 * ServiceReferenceMetricsList
 * 
 * Wraps a table control for displaying metrics for services referenced by a
 * service.
 * 
 * @author Rob Cernich
 */
public class ServiceReferenceMetricsList extends AbstractDataTable<ServiceMetrics> {

    private static final ProvidesKey<ServiceMetrics> KEY_PROVIDER = new ProvidesKey<ServiceMetrics>() {
        @Override
        public Object getKey(ServiceMetrics item) {
            return item.getName();
        }
    };

    private ServiceMetrics _serviceMetrics;

    /**
     * Create a new ServiceReferenceMetricsList.
     */
    public ServiceReferenceMetricsList() {
        super("Referenced Service Metrics");
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
                if (_serviceMetrics == null || _serviceMetrics.getTotalProcessingTime() == 0) {
                    return 0.0;
                }
                return metrics.getTotalProcessingTime() / (double) _serviceMetrics.getTotalProcessingTime();
            }
        };
        totalTimePercentageColumn.setSortable(true);

        final Column<ServiceMetrics, Double> faultPercentageColumn = new Column<ServiceMetrics, Double>(
                new PercentageBarCell()) {
            @Override
            public Double getValue(ServiceMetrics metrics) {
                if (metrics.getTotalCount() == 0) {
                    return 0.0;
                }
                return metrics.getFaultCount() / (double) metrics.getTotalCount();
            }
        };
        faultPercentageColumn.setSortable(true);

        ColumnSortEvent.ListHandler<ServiceMetrics> sortHandler = new ColumnSortEvent.ListHandler<ServiceMetrics>(
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
            setData(serviceMetrics.getReferences());
        }
    }

    @Override
    protected ProvidesKey<ServiceMetrics> createKeyProvider() {
        return KEY_PROVIDER;
    }

}
