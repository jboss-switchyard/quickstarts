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
        layout.setStyleName("fill-layout-width"); //$NON-NLS-1$

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
