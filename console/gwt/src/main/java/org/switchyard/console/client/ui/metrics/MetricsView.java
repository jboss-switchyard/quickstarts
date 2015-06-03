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

import java.util.List;

import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.layout.SimpleLayout;
import org.switchyard.console.client.Singleton;
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
        _servicesList = new ServiceMetricsList(Singleton.MESSAGES.label_serviceMetrics());
        _referencesList = new ServiceMetricsList(Singleton.MESSAGES.label_referenceMetrics()) {
            @Override
            protected MetricsDetailsWidget createDetailsWidget() {
                return new ReferenceDetailsWidget();
            }
        };
    }

    @Override
    public Widget createWidget() {
        final Button resetButton = new Button(Singleton.MESSAGES.label_resetAllMetrics(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                _presenter.resetSystemMetrics();
            }
        });
        SimpleLayout layout = new SimpleLayout().setTitle(Singleton.MESSAGES.label_switchYardMessageMetrics()).setHeadline(Singleton.MESSAGES.label_system())
                .setDescription(Singleton.MESSAGES.description_systemMetrics())
                .addContent(Singleton.MESSAGES.label_systemMessageMetrics(), _systemMetricsViewer.asWidget())
                .addContent("reset", resetButton) //$NON-NLS-1$
                .addContent("spacer", new HTMLPanel("&nbsp;")) //$NON-NLS-1$ //$NON-NLS-2$
                .addContent(Singleton.MESSAGES.label_serviceMessageMetrics(), _servicesList.asWidget())
                .addContent(Singleton.MESSAGES.label_referenceMessageMetrics(), _referencesList.asWidget());

        final Widget result = layout.build();
        // hackery, prevent button from filling the row
        resetButton.getElement().removeClassName("fill-layout-width"); //$NON-NLS-1$
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
