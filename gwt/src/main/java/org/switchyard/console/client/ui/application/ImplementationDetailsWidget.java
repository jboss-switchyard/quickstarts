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

import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.switchyard.console.client.NameTokens;
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
        content.setStyleName("fill-layout");

        _serviceNameLabel = new ContentHeaderLabel();
        content.add(_serviceNameLabel);

        _implementationTypeHeaderLabel = new ContentHeaderLabel();
        content.add(_implementationTypeHeaderLabel);

        _referencesList = new ComponentReferencesList();
        content.add(new ContentGroupLabel("References"));
        content.add(_referencesList.asWidget());

        _implementationDetails = DOM.createElement("pre");

        Element code = DOM.createElement("code");
        code.appendChild(_implementationDetails);

        HTML html = new HTML();
        html.getElement().appendChild(code);

        content.add(new ContentGroupLabel("Raw Configuration"));
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
        _serviceNameLabel.setText(NameTokens.parseQName(service.getName())[1]);
        _implementationTypeHeaderLabel.setText(service.getImplementation() + " implementation");
        _referencesList.setService(service);
        _implementationDetails.setInnerText(service.getImplementationConfiguration());
    }

}
