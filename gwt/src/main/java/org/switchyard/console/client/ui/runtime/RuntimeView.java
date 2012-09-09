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
package org.switchyard.console.client.ui.runtime;

import java.util.List;

import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.viewframework.builder.OneToOneLayout;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.switchyard.console.client.model.MessageMetrics;
import org.switchyard.console.client.model.ServiceMetrics;
import org.switchyard.console.client.ui.runtime.RuntimePresenter.MyView;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

/**
 * RuntimeView
 * 
 * <p/>
 * View implementation for SwitchYard runtime metrics.
 * 
 * @author Rob Cernich
 */
public class RuntimeView extends DisposableViewImpl implements MyView {

    private RuntimePresenter _presenter;
    private MessageMetricsViewer _systemMetricsViewer;
    private ServiceMetricsList _servicesList;
    private MessageMetricsViewer _serviceMetricsViewer;
    private ServiceReferenceMetricsList _serviceReferenceMetricsList;
    private ServiceOperationMetricsList _serviceOperationMetricsList;
    private MessageMetrics _systemMetrics;
    private ServiceMetrics _selectedService;

    @Override
    public Widget createWidget() {
        _systemMetricsViewer = new MessageMetricsViewer(false);
        _servicesList = new ServiceMetricsList();
        _serviceMetricsViewer = new MessageMetricsViewer(true);
        _serviceReferenceMetricsList = new ServiceReferenceMetricsList();
        _serviceOperationMetricsList = new ServiceOperationMetricsList();

        _servicesList.addSelectionChangeHandler(new Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                // prevent infinite recursion
                if (_servicesList.getSelection() != _selectedService) {
                    _presenter.onServiceSelected(_servicesList.getSelection());
                }
            }
        });

        Widget servicesWidget = _servicesList.asWidget();
        OneToOneLayout serviceMetricsLayout = new OneToOneLayout()
                .setPlain(true)
                .setHeadline("Services")
                .setDescription(
                        "Displays message metrics for individual services.  Select a service to see message metrics for a specific service.")
                .setMaster(null, servicesWidget).addDetail("Service Metrics", _serviceMetricsViewer.asWidget())
                .addDetail("Operation Metrics", _serviceOperationMetricsList.asWidget())
                .addDetail("Reference Metrics", _serviceReferenceMetricsList.asWidget());
        serviceMetricsLayout.build();
        servicesWidget = servicesWidget.getParent();
        servicesWidget.setStyleName("fill-layout-width");

        SimpleLayout layout = new SimpleLayout().setTitle("SwitchYard Message Metrics").setHeadline("System")
                .setDescription("Displays message metrics for the SwitchYard subsystem.")
                .addContent("System Message Metrics", _systemMetricsViewer.asWidget())
                .addContent("spacer", new HTMLPanel("&nbsp;")).addContent("Service Message Metrics", servicesWidget);

        return layout.build();
    }

    @Override
    public void setPresenter(RuntimePresenter presenter) {
        _presenter = presenter;
    }

    @Override
    public void setServices(List<ServiceMetrics> serviceMetrics) {
        _servicesList.setData(serviceMetrics);
    }

    @Override
    public void setServiceMetrics(ServiceMetrics serviceMetrics) {
        if (serviceMetrics == null) {
            _serviceMetricsViewer.clear();
            _serviceReferenceMetricsList.setServiceMetrics(null);
            _serviceOperationMetricsList.setServiceMetrics(null);
            return;
        }
        if (_systemMetrics == null) {
            _serviceMetricsViewer.setMessageMetrics(serviceMetrics);
        } else {
            _serviceMetricsViewer.setMessageMetrics(serviceMetrics, _systemMetrics.getTotalCount(),
                    _systemMetrics.getTotalProcessingTime());
        }
        _serviceReferenceMetricsList.setServiceMetrics(serviceMetrics);
        _serviceOperationMetricsList.setServiceMetrics(serviceMetrics);
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
    }

    @Override
    public void setService(ServiceMetrics service) {
        _selectedService = service;
        _servicesList.setSelection(service);
    }

    @Override
    public void clearMetrics() {
        _systemMetricsViewer.clear();
        _serviceMetricsViewer.clear();
    }

}
