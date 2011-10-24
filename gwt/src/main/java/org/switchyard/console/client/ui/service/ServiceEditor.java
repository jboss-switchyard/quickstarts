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
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.model.Service;
import org.switchyard.console.client.ui.widgets.ClickableTextItem;
import org.switchyard.console.client.ui.widgets.ClickableTextItem.ValueAdapter;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * ServiceEditor
 * 
 * Editor for SwitchYard service configuration.
 * 
 * @author Rob Cernich
 */
public class ServiceEditor {

    private ServicePresenter _presenter;

    private ContentHeaderLabel _serviceHeaderLabel;
    private ContentHeaderLabel _namespaceHeaderLabel;
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

        ScrollPanel scroll = new ScrollPanel();

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        scroll.add(layout);

        _serviceHeaderLabel = new ContentHeaderLabel();
        layout.add(_serviceHeaderLabel);

        _namespaceHeaderLabel = new ContentHeaderLabel();
        layout.add(_namespaceHeaderLabel);

        layout.add(createImplementationDetailsPanel());
        layout.add(createGatewayDetailsPanel());

        return scroll;
    }

    /**
     * @param service the service to be edited.
     */
    public void setService(Service service) {
        this._service = service;

        if (service.getInterface() == null) {
            // XXX: workaround to ensure interface field in the form gets set.
            service.setInterface("");
        }

        String[] tnsLocal = NameTokens.parseQName(service.getName());
        _serviceHeaderLabel
                .setText(Singleton.MESSAGES.header_editor_service_name(tnsLocal[1]));
        _namespaceHeaderLabel.setText("Namespace: " + tnsLocal[0]);
        _implementationDetailsForm.edit(service);
        _gatewaysList.setService(service);
    }

    private Widget createImplementationDetailsPanel() {
        ClickableTextItem<String> nameItem = new ClickableTextItem<String>("application", "Application",
                new ValueAdapter<String>() {
                    @Override
                    public String getText(String value) {
                        return NameTokens.parseQName(value)[1];
                    }

                    @Override
                    public String getTargetHistoryToken(String value) {
                        return NameTokens.createApplicationLink(value);
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
                        return NameTokens.createApplicationLink(_service.getApplication());
                    }
                });

        _implementationDetailsForm = new Form<Service>(Service.class);
        _implementationDetailsForm.setFields(nameItem, interfaceItem, implementationItem);

        VerticalPanel implementationDetailsLayout = new VerticalPanel();
        implementationDetailsLayout.setStyleName("fill-layout-width");
        implementationDetailsLayout.add(new ContentGroupLabel("Implementation Details"));
        implementationDetailsLayout.add(_implementationDetailsForm.asWidget());

        return implementationDetailsLayout;
    }

    private Widget createGatewayDetailsPanel() {
        _gatewaysList = new GatewaysList();

        VerticalPanel gatewayDetailsLayout = new VerticalPanel();
        gatewayDetailsLayout.setStyleName("fill-layout-width");
        gatewayDetailsLayout.add(new ContentGroupLabel("Gateway Details"));
        gatewayDetailsLayout.add(_gatewaysList.asWidget());

        return gatewayDetailsLayout;
    }

}
