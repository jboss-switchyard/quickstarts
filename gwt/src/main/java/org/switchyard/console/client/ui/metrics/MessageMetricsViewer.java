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

import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.charts.Column;
import org.jboss.as.console.client.shared.runtime.charts.NumberColumn;
import org.jboss.as.console.client.shared.runtime.charts.TextColumn;
import org.jboss.as.console.client.shared.runtime.plain.PlainColumnView;
import org.switchyard.console.client.model.MessageMetrics;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * MessageMetricsViewer
 * 
 * <p/>
 * Displays message metrics.
 * 
 * @author Rob Cernich
 */
public class MessageMetricsViewer {

    private PlainColumnView _messageCounts;
    private PlainColumnView _processingTimes;
    private boolean _displaysChildMetrics;

    /**
     * Create a new MessageMetricsViewer.
     * 
     * @param displaysChildMetrics true if this viewer should display percentage
     *            bars for parent data.
     */
    public MessageMetricsViewer(boolean displaysChildMetrics) {
        _displaysChildMetrics = displaysChildMetrics;
    }

    /**
     * @return the control.
     */
    public Widget asWidget() {
        Column<?> containerTotalCountItem = new NumberColumn("ContainerTotalCount", "Total Count").setVisible(false);
        Column<?> totalCountItem = new NumberColumn("TotalCount", "Total Count").setBaseline(true);
        Column<?> successCountItem = new NumberColumn("SuccessCount", "Success Count")
                .setComparisonColumn(totalCountItem);
        Column<?> faultCountItem = new NumberColumn("FaultCount", "Fault Count").setComparisonColumn(totalCountItem);
        if (_displaysChildMetrics) {
            totalCountItem.setComparisonColumn(containerTotalCountItem);
        }

        // XXX: these should really be "LongColumn"
        Column<?> containerTotalProcessingTime = new NumberColumn("ContainerTotalProcessingTime",
                "Total Processing Time").setVisible(false).setBaseline(true);
        Column<?> totalProcessingTimeItem = new NumberColumn("TotalProcessingTime", "Total Processing Time");
        if (_displaysChildMetrics) {
            totalProcessingTimeItem.setComparisonColumn(containerTotalProcessingTime);
        }

        // XXX: using TextColumn for long and double fields
        Column<?> averageProcessingTimeItem = new TextColumn("AverageProcessingTime", "Average Processing Time");
        Column<?> minProcessingTimeItem = new TextColumn("MinProcessingTime", "Min. Processing Time");
        Column<?> maxProcessingTimeItem = new TextColumn("MaxProcessingTime", "Max. Processing Time");

        _messageCounts = new PlainColumnView("Message Counts");
        _messageCounts.setColumns(containerTotalCountItem, totalCountItem, successCountItem, faultCountItem);

        _processingTimes = new PlainColumnView("Processing Times");
        _processingTimes.setColumns(containerTotalProcessingTime, totalProcessingTimeItem, averageProcessingTimeItem,
                minProcessingTimeItem, maxProcessingTimeItem);

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout-width");

        panel.add(_messageCounts.asWidget());
        panel.add(_processingTimes.asWidget());
        return panel;
    }

    /**
     * @param metrics the metrics to be displayed.
     */
    public void setMessageMetrics(MessageMetrics metrics) {
        Metric countMetric = new Metric("0", "" + metrics.getTotalCount(), "" + metrics.getSuccessCount(), ""
                + metrics.getFaultCount());
        _messageCounts.addSample(countMetric);

        Metric timeMetric = new Metric("0", "" + metrics.getTotalProcessingTime(), ""
                + metrics.getAverageProcessingTime(), "" + metrics.getMinProcessingTime(), ""
                + metrics.getMaxProcessingTime());
        _processingTimes.addSample(timeMetric);
    }

    /**
     * @param metrics the metrics to be displayed.
     * @param totalCount the parent's total count.
     * @param totalTime the parent's total time.
     */
    public void setMessageMetrics(MessageMetrics metrics, int totalCount, long totalTime) {
        Metric countMetric = new Metric(""+ totalCount, "" + metrics.getTotalCount(), "" + metrics.getSuccessCount(), ""
                + metrics.getFaultCount());
        _messageCounts.addSample(countMetric);

        Metric timeMetric = new Metric("" + totalTime, "" + metrics.getTotalProcessingTime(), ""
                + metrics.getAverageProcessingTime(), "" + metrics.getMinProcessingTime(), ""
                + metrics.getMaxProcessingTime());
        _processingTimes.addSample(timeMetric);
    }

    /**
     * Clear the display.
     */
    public void clear() {
        _messageCounts.clearSamples();
        _processingTimes.clearSamples();
    }
}
