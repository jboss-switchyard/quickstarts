/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.console.client.ui.reference;

import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.model.Reference;
import org.switchyard.console.client.ui.service.GatewaysList;
import org.switchyard.console.client.ui.widgets.ClickableTextItem;
import org.switchyard.console.client.ui.widgets.ClickableTextItem.ValueAdapter;
import org.switchyard.console.client.ui.widgets.LocalNameFormItem;
import org.switchyard.console.client.ui.widgets.NamespaceFormItem;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

/**
 * ReferenceEditor
 * 
 * Editor for SwitchYard reference configuration.
 */
public class ReferenceEditor {

    private ReferencePresenter _presenter;

    private Form<Reference> _implementationDetailsForm;
    private GatewaysList _gatewaysList;

    private Reference _reference;

    /**
     * Create a new ReferenceEditor.
     */
    public ReferenceEditor() {
        _gatewaysList = new GatewaysList();
    }

    /**
     * @param presenter the current presenter.
     */
    public void setPresenter(ReferencePresenter presenter) {
        _presenter = presenter;
        _gatewaysList.setPresenter(_presenter);
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
     * @param reference the reference to be edited.
     */
    public void setReference(Reference reference) {
        _reference = reference;

        if (reference.getInterface() == null) {
            // XXX: workaround to ensure interface field in the form gets set.
            reference.setInterface("");
        }

        _implementationDetailsForm.clearValues();
        _implementationDetailsForm.edit(reference);
        _gatewaysList.setData(reference.getGateways());
    }

    private Widget createImplementationDetailsPanel() {
        TextItem nameItem = new LocalNameFormItem("name_1", "Name");
        TextItem namespaceItem = new NamespaceFormItem("name_2", "Namespace");
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

        _implementationDetailsForm = new Form<Reference>(Reference.class);
        _implementationDetailsForm.setNumColumns(2);
        _implementationDetailsForm.setFields(nameItem, applicationItem, namespaceItem, interfaceItem);

        VerticalPanel implementationDetailsLayout = new VerticalPanel();
        implementationDetailsLayout.setStyleName("fill-layout-width");
        implementationDetailsLayout.add(new ContentGroupLabel("Reference Details"));
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
        return _gatewaysList.asWidget();
    }

}
