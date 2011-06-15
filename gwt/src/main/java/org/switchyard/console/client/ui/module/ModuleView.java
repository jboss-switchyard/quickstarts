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

import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.widgets.RHSContentPanel;
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.model.SwitchYardModule;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * ModuleView
 * 
 * View for SwitchYard module configuration.
 * 
 * @author Rob Cernich
 */
public class ModuleView extends DisposableViewImpl implements ModulePresenter.MyView {

    private ModulePresenter _presenter;
    private ModuleEditor _moduleEditor;

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new RHSContentPanel(Singleton.MESSAGES.header_content_moduleConfiguration());

        _moduleEditor = new ModuleEditor(_presenter);
        layout.add(_moduleEditor.asWidget());

        return layout;
    }

    @Override
    public void setPresenter(ModulePresenter presenter) {
        this._presenter = presenter;
    }

    @Override
    public void setModule(SwitchYardModule module) {
        _moduleEditor.setModule(module);
    }

}
