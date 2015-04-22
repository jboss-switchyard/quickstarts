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
package org.switchyard.console.components.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.switchyard.console.components.client.model.Component;
import org.switchyard.console.components.client.ui.ComponentConfigurationPresenter.ComponentConfigurationView;

import com.google.gwt.core.client.GWT;
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

    private static final Messages MESSAGES = GWT.create(Messages.class);

    private Label _componentNameLabel;
    private Widget _widget;
    private ComponentConfigurationPresenter _presenter;
    private Component _component;
    private ListDataProvider<Entry<String, String>> _propertiesData;

    @Override
    public Widget asWidget() {
        if (_widget == null) {
            VerticalPanel layout = new VerticalPanel();
            layout.setStyleName("fill-layout-width"); //$NON-NLS-1$

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
        return new ContentGroupLabel(""); //$NON-NLS-1$
    }

    /**
     * @return a control for viewing the component's activation types.
     */
    protected Label createComponentTypeLabel() {
        return new ContentGroupLabel(""); //$NON-NLS-1$
    }

    /**
     * @return a control for viewing configuration details.
     */
    @SuppressWarnings("unchecked")
    protected Widget createComponentDetailsWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width"); //$NON-NLS-1$
        layout.add(new ContentGroupLabel(MESSAGES.label_configuredProperties()));

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

        table.addColumn(nameColumn, MESSAGES.label_name());
        table.addColumn(valueColumn, MESSAGES.label_value());

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
        return MESSAGES.label_nameInstance(getComponentName());
    }

}
