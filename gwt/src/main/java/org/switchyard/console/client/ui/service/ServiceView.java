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

    @Override
    public Widget createWidget() {
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

        _serviceEditor = new ServiceEditor(_presenter);

        SimpleLayout layout = new SimpleLayout()
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
