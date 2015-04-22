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

import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.model.ComponentService;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * ImplementationDetailsWidget
 * 
 * Provides a widget for displaying details about a component service's
 * implementation.
 * 
 * @author Rob Cernich
 */
public class ImplementationDetailsWidget {

    private ContentHeaderLabel _serviceNameLabel;
    private ContentHeaderLabel _implementationTypeHeaderLabel;
    private ComponentReferencesList _referencesList;
    private Element _implementationDetails;

    /**
     * Create a new ImplementationDetailsWidget.
     */
    public ImplementationDetailsWidget() {
    }

    /**
     * @return the widget
     */
    public Widget asWidget() {
        FlowPanel content = new FlowPanel();
        content.setStyleName("fill-layout"); //$NON-NLS-1$

        _serviceNameLabel = new ContentHeaderLabel();
        content.add(_serviceNameLabel);

        _implementationTypeHeaderLabel = new ContentHeaderLabel();
        content.add(_implementationTypeHeaderLabel);

        _referencesList = new ComponentReferencesList();
        content.add(_referencesList.asWidget());

        _implementationDetails = DOM.createElement("pre"); //$NON-NLS-1$

        Element code = DOM.createElement("code"); //$NON-NLS-1$
        code.appendChild(_implementationDetails);

        HTML html = new HTML();
        html.getElement().appendChild(code);

        content.add(new ContentGroupLabel(Singleton.MESSAGES.label_rawConfiguration()));
        content.add(html);

        ScrollPanel panel = new ScrollPanel();
        panel.add(content);

        return panel;
    }

    /**
     * Updates the widget with the information from the specified service.
     * 
     * @param service the service
     */
    public void setService(ComponentService service) {
        _serviceNameLabel.setText(service.localName());
        _implementationTypeHeaderLabel.setText(Singleton.MESSAGES.label_implementationInstance(service.getImplementation()));
        _referencesList.setData(service.getReferences());
        _implementationDetails.setInnerText(service.getImplementationConfiguration());
    }

}
