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

package org.switchyard.console.client.ui.service;

import java.util.List;

import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.switchyard.console.client.model.Service;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;

/**
 * ServiceView
 * 
 * View for SwitchYard service configuration.
 * 
 * @author Rob Cernich
 */
public class ServiceView extends DisposableViewImpl implements ServicePresenter.MyView {

    private ServicePresenter _presenter;
    private ServicesList _servicesList;
    private ServiceEditor _serviceEditor;
    private Service _selectedService;

    /**
     * Create a new ServiceView.
     */
    public ServiceView() {
        super();
        _servicesList = new ServicesList();
        _servicesList.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                // prevent infinite recursion
                if (_servicesList.getSelection() != _selectedService) {
                    _presenter.onServiceSelected(_servicesList.getSelection());
                }
            }
        });

        _serviceEditor = new ServiceEditor();

    }

    @Override
    public Widget createWidget() {
        SimpleLayout layout = new SimpleLayout()
                .setPlain(true)
                .setTitle("SwitchYard Services")
                .setHeadline("Services")
                .setDescription(
                        "Displays a list of deployed SwitchYard services.  Select a service to see more details.")
                .addContent("Services", _servicesList.asWidget())
                .addContent("Service Details", _serviceEditor.asWidget());

        return layout.build();
    }

    @Override
    public void setPresenter(ServicePresenter presenter) {
        _presenter = presenter;
        _serviceEditor.setPresenter(presenter);
    }

    @Override
    public void setServicesList(List<Service> services) {
        _servicesList.setData(services);
    }

    @Override
    public void setService(Service service) {
        _selectedService = service;
        _servicesList.setSelection(service);
        _serviceEditor.setService(service);
    }

}
