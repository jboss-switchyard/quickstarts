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

import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
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
        layout.setStyleName("fill-layout-width");

        TextItem versionItem = new TextItem("version", "Version");
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

        layout.add(new ContentGroupLabel("Core Runtime"));
        layout.add(_systemDetailsForm.asWidget());

        layout.add(_componentsList.asWidget());

        layout.add(new ContentGroupLabel("Component Details"));
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
