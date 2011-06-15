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

package org.switchyard.console.client.ui.module;

import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.model.SwitchYardModule;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * ModuleEditor
 * 
 * Editor for SwitchYard _module configuration.
 * 
 * @author Rob Cernich
 */
public class ModuleEditor {

    private ModulePresenter _presenter;

    private ContentHeaderLabel _headerLabel;

    private SwitchYardModule _module;

    /**
     * Create a new ModuleEditor.
     * 
     * @param presenter
     *            the associated presenter.
     */
    public ModuleEditor(ModulePresenter presenter) {
        this._presenter = presenter;
    }

    /**
     * @return this editor as a Widget.
     */
    public Widget asWidget() {

        ScrollPanel scroll = new ScrollPanel();

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        scroll.add(layout);

        _headerLabel = new ContentHeaderLabel();
        layout.add(_headerLabel);

        layout.add(new Label("TODO: Module specific configuration settings."));

        return scroll;
    }

    /**
     * @param module
     *            the module to be edited.
     */
    public void setModule(SwitchYardModule module) {
        this._module = module;

        _headerLabel.setText(Singleton.MESSAGES.header_editor_moduleEditor(module.getName()));
    }

}
