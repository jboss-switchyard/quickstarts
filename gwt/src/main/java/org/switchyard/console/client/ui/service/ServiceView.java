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

package org.switchyard.console.client.ui.service;

import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.ballroom.client.layout.RHSContentPanel;
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.model.Service;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * ServiceView
 * 
 * View for SwitchYard service configuration.
 * 
 * @author Rob Cernich
 */
public class ServiceView extends DisposableViewImpl implements ServicePresenter.MyView {

    private ServicePresenter _presenter;
    private ServiceEditor _serviceEditor;

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new RHSContentPanel(Singleton.MESSAGES.header_content_serviceDetails());

        _serviceEditor = new ServiceEditor(_presenter);
        layout.add(_serviceEditor.asWidget());

        return layout;
    }

    @Override
    public void setPresenter(ServicePresenter presenter) {
        this._presenter = presenter;
    }

    @Override
    public void setService(Service service) {
        _serviceEditor.setService(service);
    }

}
