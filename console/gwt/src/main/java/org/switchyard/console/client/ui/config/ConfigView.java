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

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.layout.SimpleLayout;
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.model.SystemDetails;
import org.switchyard.console.components.client.extension.ComponentProviders;
import org.switchyard.console.components.client.model.Component;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * ConfigView
 * 
 * View implementation for SwitchYard system configuration.
 * 
 * @author Rob Cernich
 */
public class ConfigView extends DisposableViewImpl implements ConfigPresenter.MyView {

    private ConfigPresenter _presenter;
    private ConfigEditor _configEditor;
    @Inject
    private ComponentProviders _componentProviders;

    @Override
    public Widget createWidget() {
        _configEditor = new ConfigEditor(_componentProviders);
        _configEditor.setPresenter(_presenter);

        SimpleLayout layout = new SimpleLayout().setTitle(Singleton.MESSAGES.label_switchYardRuntimeDetails())
                .setHeadline(Singleton.MESSAGES.label_switchYardRuntime())
                .setDescription(Singleton.MESSAGES.description_switchYardRuntime())
                .addContent(Singleton.MESSAGES.label_runtimeDetails(), _configEditor.asWidget());
        return layout.build();
    }

    @Override
    public void setPresenter(ConfigPresenter presenter) {
        _presenter = presenter;
        if (_configEditor != null) {
            _configEditor.setPresenter(presenter);
        }
    }

    @Override
    public void setSystemDetails(SystemDetails systemDetails) {
        _configEditor.setSystemDetails(systemDetails);
    }

    @Override
    public void setComponents(List<Component> components) {
        _configEditor.setComponents(components);
    }

    @Override
    public void setInSlot(Object slot, IsWidget content) {
        if (slot == ConfigPresenter.TYPE_COMPONENT_CONTENT) {
            _configEditor.setComponentContent(content == null ? null : content.asWidget());
        } else {
            Console.error("Unknown slot requested: " + slot); //$NON-NLS-1$
        }
    }

}
