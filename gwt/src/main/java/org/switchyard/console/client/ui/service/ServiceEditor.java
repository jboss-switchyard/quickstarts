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

import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.model.Service;
import org.switchyard.console.client.ui.widgets.ClickableTextItem;
import org.switchyard.console.client.ui.widgets.ClickableTextItem.ValueAdapter;
import org.switchyard.console.client.ui.widgets.LocalNameFormItem;
import org.switchyard.console.client.ui.widgets.NamespaceFormItem;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

/**
 * ServiceEditor
 * 
 * Editor for SwitchYard service configuration.
 * 
 * @author Rob Cernich
 */
public class ServiceEditor {

    private ServicePresenter _presenter;

    private Form<Service> _implementationDetailsForm;
    private GatewaysList _gatewaysList;

    private Service _service;

    /**
     * Create a new ServiceEditor.
     * 
     * @param presenter the associated presenter.
     */
    public ServiceEditor(ServicePresenter presenter) {
        this._presenter = presenter;
    }

    /**
     * @return this editor as a Widget.
     */
    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        layout.add(createImplementationDetailsPanel());
        layout.add(createGatewayDetailsPanel());

        return layout;
    }

    /**
     * @param service the service to be edited.
     */
    public void setService(Service service) {
        _service = service;

        if (service.getInterface() == null) {
            // XXX: workaround to ensure interface field in the form gets set.
            service.setInterface("");
        }

        _implementationDetailsForm.clearValues();
        _implementationDetailsForm.edit(service);
        _gatewaysList.setData(service.getGateways());
    }

    private Widget createImplementationDetailsPanel() {
        TextItem nameItem = new LocalNameFormItem("name_1", "Service Name");
        TextItem namespaceItem = new NamespaceFormItem("name_2", "Service Namespace");
        ClickableTextItem<String> applicationItem = new ClickableTextItem<String>("application", "Application",
                new ValueAdapter<String>() {
                    @Override
                    public String getText(String value) {
                        return NameTokens.parseQName(value)[1];
                    }

                    @Override
                    public String getTargetHistoryToken(String value) {
                        return createApplicationLink(value);
                    }
                });
        TextItem interfaceItem = new TextItem("interface", "Interface") {
            @Override
            public void setValue(String value) {
                if (value == null || value.length() == 0) {
                    value = "<inherited>";
                }
                super.setValue(value);
            }
        };
        ClickableTextItem<String> implementationItem = new ClickableTextItem<String>("promotedService",
                "Promoted Service", new ValueAdapter<String>() {
                    @Override
                    public String getText(String value) {
                        return NameTokens.parseQName(value)[1];
                    }

                    @Override
                    public String getTargetHistoryToken(String value) {
                        if (_service == null || _service.getApplication() == null) {
                            return createApplicationLink(null);
                        }
                        return createApplicationLink(_service.getApplication());
                    }
                });

        _implementationDetailsForm = new Form<Service>(Service.class);
        _implementationDetailsForm.setNumColumns(2);
        _implementationDetailsForm.setFields(nameItem, applicationItem, namespaceItem);
        _implementationDetailsForm.setFieldsInGroup("Implementation Details", implementationItem, interfaceItem);

        VerticalPanel implementationDetailsLayout = new VerticalPanel();
        implementationDetailsLayout.setStyleName("fill-layout-width");
        implementationDetailsLayout.add(new ContentGroupLabel("Service Details"));
        implementationDetailsLayout.add(_implementationDetailsForm.asWidget());

        return implementationDetailsLayout;
    }

    private String createApplicationLink(String applicationName) {
        PlaceRequest request = new PlaceRequest(NameTokens.APPLICATIONS_PRESENTER);
        if (applicationName != null) {
            request = request.with(NameTokens.APPLICATION_NAME_PARAM, URL.encode(applicationName));
        }
        return _presenter.getPlaceManager().buildRelativeHistoryToken(request, -1);
    }

    private Widget createGatewayDetailsPanel() {
        _gatewaysList = new GatewaysList();
        return _gatewaysList.asWidget();
    }

}
