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

package org.switchyard.console.client.ui.application;

import org.switchyard.console.client.model.Application;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * ApplicationServicesEditor
 * 
 * Editor widget for SwitchYard application details.
 * 
 * @author Rob Cernich
 */
public class ApplicationServicesEditor {

    private ApplicationPresenter _presenter;

    private ApplicationServicesList _applicationServicesList;
    private ComponentServicesList _componentServicesList;

    /**
     * Create a new ApplicationServicesEditor.
     * 
     * @param presenter the associated presenter.
     */
    public ApplicationServicesEditor(ApplicationPresenter presenter) {
        _presenter = presenter;
    }

    /**
     * @return this editor as a Widget.
     */
    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        _applicationServicesList = new ApplicationServicesList(_presenter);
        _componentServicesList = new ComponentServicesList();

        layout.add(_applicationServicesList.asWidget());
        layout.add(_componentServicesList.asWidget());

        _componentServicesList.bind(_applicationServicesList);

        return layout;
    }

    /**
     * @param application the application being edited by this editor.
     */
    public void setApplication(Application application) {
        _applicationServicesList.setApplication(application);
        _componentServicesList.setData(application.getComponentServices());
    }

}
