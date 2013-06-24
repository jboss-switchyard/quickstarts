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

import java.util.Map;

import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.as.console.client.widgets.forms.BlankItem;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.model.Service;
import org.switchyard.console.client.model.Throttling;
import org.switchyard.console.client.ui.widgets.ClickableTextItem;
import org.switchyard.console.client.ui.widgets.ClickableTextItem.ValueAdapter;
import org.switchyard.console.client.ui.widgets.LocalNameFormItem;
import org.switchyard.console.client.ui.widgets.NamespaceFormItem;

import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.TabPanel;
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
    private Form<Throttling> _throttlingDetailsForm;
    private GatewaysList _gatewaysList;
    private FormToolStrip<Throttling> _toolstrip;

    private Service _service;

    /**
     * Create a new ServiceEditor.
     */
    public ServiceEditor() {
        _gatewaysList = new GatewaysList();
    }

    /**
     * @param presenter the current presenter.
     */
    public void setPresenter(ServicePresenter presenter) {
        _presenter = presenter;
        _gatewaysList.setPresenter(_presenter);
    }

    /**
     * @return this editor as a Widget.
     */
    public Widget asWidget() {
        VerticalPanel panel = new VerticalPanel();
        panel.add(new ContentGroupLabel("Service Details"));
        panel.add(new ContentDescription("Displays details for the selected service."));
        panel.add(createImplementationDetailsPanel());

        TabPanel tabs = new TabPanel();
        tabs.setStyleName("default-tabpanel");
        tabs.getElement().setAttribute("style", "margin-top:15px;");
        tabs.add(createGatewayDetailsPanel(), "Gateways");
        tabs.add(createThrottlingDetailsPanel(), "Throttling");
        tabs.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                _toolstrip.doCancel();
            }
        });

        panel.add(tabs);
        tabs.selectTab(0);

        return panel;
    }

    /**
     * @param service the service to be edited.
     */
    public void setService(Service service) {
        _service = service;

        _toolstrip.doCancel();

        _implementationDetailsForm.clearValues();
        _throttlingDetailsForm.clearValues();

        if (service == null) {
            _gatewaysList.setData(null);
        } else {
            if (service.getInterface() == null) {
                // XXX: workaround to ensure interface field in the form gets
                // set.
                service.setInterface("");
            }
            _implementationDetailsForm.edit(service);
            if (service.getThrottling() != null) {
                _throttlingDetailsForm.edit(service.getThrottling());
            }
            _gatewaysList.setData(service.getGateways());
        }
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
        // don't disable as the fields won't display correctly
        // _implementationDetailsForm.setEnabled(false);

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        layout.add(_implementationDetailsForm.asWidget());
        return layout;
    }

    private String createApplicationLink(String applicationName) {
        PlaceRequest request = new PlaceRequest(NameTokens.APPLICATIONS_PRESENTER);
        if (applicationName != null) {
            request = request.with(NameTokens.APPLICATION_NAME_PARAM, URL.encode(applicationName));
        }
        return _presenter.getPlaceManager().buildRelativeHistoryToken(request, -1);
    }

    private Widget createGatewayDetailsPanel() {
        _gatewaysList.setPresenter(_presenter);
        return _gatewaysList.asWidget();
    }

    private Widget createThrottlingDetailsPanel() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        CheckBoxItem enabledItem = new CheckBoxItem("enabled", "Enabled");
        NumberBoxItem maxRequestsItem = new NumberBoxItem("maxRequests", "Maximum Requests") {
            @Override
            public boolean validate(Number value) {
                return super.validate(value) && value.intValue() > 0;
            }
        };
        NumberBoxItem timePeriodItem = new NumberBoxItem("timePeriod", "Time Period (millis)") {
            @Override
            public boolean validate(Number value) {
                return super.validate(value) && value.intValue() > 0;
            }
        };
        timePeriodItem.setEnabled(false);

        _throttlingDetailsForm = new Form<Throttling>(Throttling.class) {
            @SuppressWarnings("rawtypes")
            public FormValidation validate() {
                // copied from parent, but we always validate
                FormValidation outcome = new FormValidation();
                for (Map<String, FormItem> groupItems : formItems.values()) {
                    for (FormItem item : groupItems.values()) {
                        Object value = item.getValue();
                        // ascii or empty string are ok. the later will be
                        // checked in each form item implementation.
                        String stringValue = String.valueOf(value);
                        boolean ascii = stringValue.isEmpty() || stringValue.matches("^[\\u0020-\\u007e]+$");
                        if (!ascii) {
                            outcome.addError(item.getName());
                            item.setErroneous(true);
                        } else {
                            @SuppressWarnings("unchecked")
                            boolean validValue = item.validate(value);
                            if (validValue) {
                                item.setErroneous(false);
                            } else {
                                outcome.addError(item.getName());
                                item.setErroneous(true);
                            }
                        }
                    }
                }
                return outcome;
            }
        };
        _throttlingDetailsForm.setNumColumns(2);
        _throttlingDetailsForm.setFields(enabledItem, new BlankItem(), maxRequestsItem, timePeriodItem);
        _throttlingDetailsForm.setEnabled(false);

        // toolstrip
        _toolstrip = new FormToolStrip<Throttling>(_throttlingDetailsForm,
                new FormToolStrip.FormCallback<Throttling>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        _presenter.updateThrottling(_service, _throttlingDetailsForm.getEditedEntity(), changeset);
                    }

                    @Override
                    public void onDelete(Throttling entity) {
                    }
                });

        _toolstrip.providesDeleteOp(false); // belongs to the top

        layout.add(_toolstrip.asWidget());
        layout.add(_throttlingDetailsForm.asWidget());

        return layout;
    }
}
