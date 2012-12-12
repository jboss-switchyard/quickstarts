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
package org.switchyard.console.components.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.switchyard.console.components.client.model.Component;
import org.switchyard.console.components.client.ui.ComponentConfigurationPresenter.ComponentConfigurationView;

import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * BaseComponentConfigurationView
 * 
 * Base implementation for ComponentConfigurationView. This implementation
 * provides controls for displaying: name, activation types and a table for
 * properties.
 * 
 * Extenders may override {@link #createComponentDetailsWidget()} and
 * {@link #updateComponentDetails()} to provide customized controls for
 * editing/viewing the component's configuration.
 * 
 * @author Rob Cernich
 */
public class BaseComponentConfigurationView extends ViewImpl implements ComponentConfigurationView {

    private Label _componentNameLabel;
    private Widget _widget;
    private ComponentConfigurationPresenter _presenter;
    private Component _component;
    private ListDataProvider<Entry<String, String>> _propertiesData;

    @Override
    public Widget asWidget() {
        if (_widget == null) {
            VerticalPanel layout = new VerticalPanel();
            layout.setStyleName("fill-layout-width");

            _componentNameLabel = createComponentNameLabel();
            if (_componentNameLabel != null) {
                layout.add(_componentNameLabel);
            }

            Widget details = createComponentDetailsWidget();
            if (details != null) {
                layout.add(details);
            }

            _widget = layout;
        }
        return _widget;
    }

    @Override
    public void setPresenter(ComponentConfigurationPresenter presenter) {
        _presenter = presenter;
    }

    @Override
    public void setComponent(Component component) {
        _component = component;
        updateControls();
    }

    /**
     * @return the presenter managing the view.
     */
    protected ComponentConfigurationPresenter getPresenter() {
        return _presenter;
    }

    /**
     * @return the component being edited/viewed.
     */
    protected Component getComponent() {
        return _component;
    }

    /**
     * @return a control for viewing the component's name.
     */
    protected Label createComponentNameLabel() {
        return new ContentGroupLabel("");
    }

    /**
     * @return a control for viewing the component's activation types.
     */
    protected Label createComponentTypeLabel() {
        return new ContentGroupLabel("");
    }

    /**
     * @return a control for viewing configuration details.
     */
    @SuppressWarnings("unchecked")
    protected Widget createComponentDetailsWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        layout.add(new ContentGroupLabel("Configured Properties"));

        DefaultCellTable<Entry<String, String>> table = new DefaultCellTable<Entry<String, String>>(5);
        TextColumn<Entry<String, String>> nameColumn = new TextColumn<Entry<String, String>>() {
            @Override
            public String getValue(Entry<String, String> object) {
                return object.getKey();
            }
        };
        nameColumn.setSortable(true);

        TextColumn<Entry<String, String>> valueColumn = new TextColumn<Entry<String, String>>() {
            @Override
            public String getValue(Entry<String, String> object) {
                return object.getValue();
            }
        };

        table.addColumn(nameColumn, "Name");
        table.addColumn(valueColumn, "Value");

        layout.add(table);

        _propertiesData = new ListDataProvider<Entry<String, String>>();
        _propertiesData.addDataDisplay(table);

        return layout;
    }

    /**
     * Update controls after the component has been changed/modified.
     */
    protected void updateControls() {
        updateComponentName();
        updateComponentDetails();
    }

    /**
     * Update the name controls.
     */
    protected void updateComponentName() {
        if (_componentNameLabel == null) {
            return;
        }
        _componentNameLabel.setText(getComponentNameLabelText());
    }

    /**
     * Update the component details controls.
     */
    protected void updateComponentDetails() {
        Component component = getComponent();
        List<Entry<String, String>> properties;
        if (component == null || component.getProperties() == null) {
            properties = Collections.emptyList();
        } else {
            properties = new ArrayList<Entry<String, String>>(component.getProperties().entrySet());
        }
        _propertiesData.setList(properties);
    }

    /**
     * @return the component name to be displayed in the name controls; default
     *         is {@link Component#getName()}.
     */
    protected String getComponentName() {
        String name;
        if (getComponent() == null) {
            name = null;
        } else {
            name = getComponent().getName();
        }
        return name;
    }

    private String getComponentNameLabelText() {
        return "Name: " + getComponentName();
    }

}
