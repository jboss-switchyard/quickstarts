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

package org.switchyard.console.client.ui.config;

import java.util.List;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.switchyard.console.client.model.SystemDetails;
import org.switchyard.console.components.client.extension.ComponentProviders;
import org.switchyard.console.components.client.model.Component;

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

        SimpleLayout layout = new SimpleLayout().setTitle("SwitchYard Runtime Details")
                .setHeadline("SwitchYard Runtime").setDescription("Displays details about the SwitchYard runtime.")
                .addContent("Runtime Details", _configEditor.asWidget());
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
    public void setInSlot(Object slot, Widget content) {
        if (slot == ConfigPresenter.TYPE_COMPONENT_CONTENT) {
            _configEditor.setComponentContent(content);
        } else {
            Console.error("Unknown slot requested: " + slot);
        }
    }

}
