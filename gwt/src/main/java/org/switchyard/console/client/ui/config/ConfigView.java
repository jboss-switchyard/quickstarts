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

import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.widgets.RHSContentPanel;
import org.switchyard.console.client.Singleton;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

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

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new RHSContentPanel(Singleton.MESSAGES.header_content_switchYardConfiguration());

        _configEditor = new ConfigEditor(_presenter);
        layout.add(_configEditor.asWidget());

        return layout;
    }

    @Override
    public void setPresenter(ConfigPresenter presenter) {
        this._presenter = presenter;
    }

}
