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
        _bindingConfiguration = DOM.createElement("pre"); //$NON-NLS-1$

        Element code = DOM.createElement("code"); //$NON-NLS-1$
        code.appendChild(_bindingConfiguration);

        HTML html = new HTML();
        html.getElement().appendChild(code);
        html.setSize("100%", "100%"); //$NON-NLS-1$ //$NON-NLS-2$

        ScrollPanel panel = new ScrollPanel();
        panel.setStyleName("fill-layout-width"); //$NON-NLS-1$
        panel.add(html);
        panel.setSize("100%", "100%"); //$NON-NLS-1$ //$NON-NLS-2$

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
