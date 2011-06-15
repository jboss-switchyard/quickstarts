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

import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.switchyard.console.client.Singleton;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * ConfigEditor
 * 
 * Editor widget for SwitchYard system configuration.
 * 
 * @author Rob Cernich
 */
public class ConfigEditor {

    private ConfigPresenter _presenter;

    /**
     * Create a new ConfigEditor.
     * 
     * @param presenter
     *            the associated presenter.
     */
    public ConfigEditor(ConfigPresenter presenter) {
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

        layout.add(new ContentHeaderLabel(Singleton.MESSAGES.header_editor_switchYardConfiguration()));

        layout.add(new Label("TODO: generic SwitchYard configuration settings."));

        return scroll;
    }

}
