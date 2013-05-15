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

import java.util.List;

import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.switchyard.console.client.model.MessageMetrics;
import org.switchyard.console.client.model.ServiceMetrics;
import org.switchyard.console.client.ui.metrics.MetricsPresenter.MyView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * RuntimeView
 * 
 * <p/>
 * View implementation for SwitchYard runtime metrics.
 * 
 * @author Rob Cernich
 */
public class MetricsView extends DisposableViewImpl implements MyView {

    private MetricsPresenter _presenter;
    private MessageMetricsViewer _systemMetricsViewer;
    private ServiceMetricsList _servicesList;
    private ServiceMetricsList _referencesList;
    private MessageMetrics _systemMetrics;

    /**
     * Create a new MetricsView.
     */
    public MetricsView() {
        _systemMetricsViewer = new MessageMetricsViewer(false);
        _servicesList = new ServiceMetricsList("Service Metrics");
        _referencesList = new ServiceMetricsList("Reference Metrics") {
            @Override
            protected MetricsDetailsWidget createDetailsWidget() {
                return new ReferenceDetailsWidget();
            }
        };
    }

    @Override
    public Widget createWidget() {
        final Button resetButton = new Button("Reset All Metrics", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                _presenter.resetSystemMetrics();
            }
        });
        SimpleLayout layout = new SimpleLayout().setTitle("SwitchYard Message Metrics").setHeadline("System")
                .setDescription("Displays message metrics for the SwitchYard subsystem.")
                .addContent("System Message Metrics", _systemMetricsViewer.asWidget())
                .addContent("reset", resetButton)
                .addContent("spacer", new HTMLPanel("&nbsp;"))
                .addContent("Service Message Metrics", _servicesList.asWidget())
                .addContent("Reference Message Metrics", _referencesList.asWidget());

        final Widget result = layout.build();
        // hackery, prevent button from filling the row
        resetButton.getElement().removeClassName("fill-layout-width");
        return result;
    }

    @Override
    public void setPresenter(MetricsPresenter presenter) {
        _presenter = presenter;
        _servicesList.setPresenter(presenter);
        _referencesList.setPresenter(presenter);
    }

    @Override
    public void setServices(List<ServiceMetrics> serviceMetrics) {
        _servicesList.setData(serviceMetrics);
    }

    @Override
    public void setReferences(List<ServiceMetrics> referenceMetrics) {
        _referencesList.setData(referenceMetrics);
    }

    @Override
    public void setSystemMetrics(MessageMetrics systemMetrics) {
        _systemMetrics = systemMetrics;
        if (systemMetrics == null) {
            _systemMetricsViewer.clear();
            return;
        }
        _systemMetricsViewer.setMessageMetrics(systemMetrics);
        _servicesList.setSystemMetrics(systemMetrics);
        _referencesList.setSystemMetrics(systemMetrics);
    }

    @Override
    public void clearMetrics() {
        _systemMetricsViewer.clear();
    }

}
