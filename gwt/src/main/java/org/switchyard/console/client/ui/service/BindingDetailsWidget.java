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
package org.switchyard.console.client.ui.service;

import org.switchyard.console.client.model.Binding;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * BindingDetailsWidget
 * 
 * Provides a widget for displaying a {@link Binding}.
 * 
 * @author Rob Cernich
 */
public class BindingDetailsWidget {

    private Element _bindingConfiguration;

    /**
     * Create a new BindingDetailsWidget.
     */
    public BindingDetailsWidget() {
    }

    /**
     * @return the widget
     */
    public Widget asWidget() {
        _bindingConfiguration = DOM.createElement("pre");

        Element code = DOM.createElement("code");
        code.appendChild(_bindingConfiguration);

        HTML html = new HTML();
        html.getElement().appendChild(code);
        html.setSize("100%", "100%");

        ScrollPanel panel = new ScrollPanel();
        panel.setStyleName("fill-layout-width");
        panel.add(html);
        panel.setSize("100%", "100%");

        return panel;
    }

    /**
     * Updates the widget with the information from the specified binding.
     * 
     * @param binding the binding
     */
    public void setBinding(Binding binding) {
        _bindingConfiguration.setInnerText(binding.getConfiguration());
    }

}
