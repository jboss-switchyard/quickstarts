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

package org.switchyard.console.client.ui.config;

import java.util.List;

import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.model.SystemDetails;
import org.switchyard.console.components.client.extension.ComponentProviders;
import org.switchyard.console.components.client.model.Component;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;

/**
 * ConfigEditor
 * 
 * Editor widget for SwitchYard system configuration.
 * 
 * @author Rob Cernich
 */
public class ConfigEditor {

    private ConfigPresenter _presenter;

    private Form<SystemDetails> _systemDetailsForm;
    private ComponentsList _componentsList;
    private Panel _componentDetails;
    private ComponentProviders _componentProviders;

    /**
     * Create a new ConfigEditor.
     * 
     * @param componentProviders the instance providing component type specific
     *            functionality.
     */
    public ConfigEditor(ComponentProviders componentProviders) {
        _componentProviders = componentProviders;
    }

    /**
     * @return this editor as a Widget.
     */
    public Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width"); //$NON-NLS-1$

        TextItem versionItem = new TextItem("version", Singleton.MESSAGES.label_version()); //$NON-NLS-1$
        _systemDetailsForm = new Form<SystemDetails>(SystemDetails.class);
        _systemDetailsForm.setFields(versionItem);

        _componentsList = new ComponentsList(_componentProviders);
        _componentsList.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                Component selected = _componentsList.getSelection();
                _presenter.onComponentSelected(selected);
            }
        });

        _componentDetails = new SimplePanel();

        layout.add(new ContentGroupLabel(Singleton.MESSAGES.label_coreRuntime())); //$NON-NLS-1$
        layout.add(_systemDetailsForm.asWidget());

        layout.add(_componentsList.asWidget());

        layout.add(new ContentGroupLabel(Singleton.MESSAGES.label_componentDetails()));
        layout.add(_componentDetails);

        return layout;
    }

    /**
     * @param presenter the presenter managing the view.
     */
    public void setPresenter(ConfigPresenter presenter) {
        _presenter = presenter;
    }

    /**
     * @param systemDetails the new system details
     */
    public void setSystemDetails(SystemDetails systemDetails) {
        _systemDetailsForm.edit(systemDetails);
    }

    /**
     * @param components the components installed in the runtime.
     */
    public void setComponents(List<Component> components) {
        _componentsList.setData(components);
    }

    /**
     * @param content component specific content.
     */
    public void setComponentContent(Widget content) {
        _componentDetails.clear();
        if (content != null) {
            _componentDetails.add(content);
        }
    }
}
