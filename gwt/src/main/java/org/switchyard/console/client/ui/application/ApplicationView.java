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

import java.util.ArrayList;
import java.util.List;

import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.viewframework.builder.OneToOneLayout;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.model.Application;
import org.switchyard.console.client.ui.artifacts.ArtifactReferencesList;
import org.switchyard.console.client.ui.common.ValidatorsList;
import org.switchyard.console.client.ui.widgets.LocalNameFormItem;
import org.switchyard.console.client.ui.widgets.NamespaceFormItem;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

/**
 * ApplicationView
 * 
 * View for SwitchYard application details.
 * 
 * @author Rob Cernich
 */
public class ApplicationView extends DisposableViewImpl implements ApplicationPresenter.MyView {

    private ApplicationPresenter _presenter;

    private Form<Application> _applicationDetailsForm;
    private ApplicationServicesEditor _servicesEditor;
    private ApplicationReferencesList _referencesEditor;
    private PropertyEditor _propertiesEditor;
    private ArtifactReferencesList _artifactReferencesList;
    private ApplicationTransformationsEditor _transformationsEditor;
    private ApplicationsList _applicationsList;
    private Application _selectedApplication;
    private ValidatorsList _validatorsList;

    @Override
    public Widget createWidget() {
        _applicationsList = new ApplicationsList();
        _applicationsList.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                // prevent infinite recursion
                if (_applicationsList.getSelection() != _selectedApplication) {
                    _presenter.onApplicationSelected(_applicationsList.getSelection());
                }
            }
        });

        VerticalPanel applicationDetailsPanel = new VerticalPanel();
        applicationDetailsPanel.setStyleName("fill-layout-width"); //$NON-NLS-1$

        _applicationDetailsForm = new Form<Application>(Application.class);
        // XXX: '_' included in names to workaround bug in form builder
        _applicationDetailsForm.setFields(new LocalNameFormItem("name_1", Singleton.MESSAGES.label_applicationName()), //$NON-NLS-1$
                new NamespaceFormItem("name_2", Singleton.MESSAGES.label_applicationNamespace())); //$NON-NLS-1$
        Widget formWidget = _applicationDetailsForm.asWidget();
        formWidget.getElement().setAttribute("style", "margin:15px"); //$NON-NLS-1$ //$NON-NLS-2$

        _servicesEditor = new ApplicationServicesEditor(_presenter);
        _referencesEditor = new ApplicationReferencesList(_presenter);
        // read only for now
        _propertiesEditor = new PropertyEditor();
        _artifactReferencesList = new ArtifactReferencesList();
        _transformationsEditor = new ApplicationTransformationsEditor(_presenter);
        _validatorsList = new ValidatorsList();

        _artifactReferencesList.addSelectionChangeHandler(new Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                _presenter.onArtifactSelected(_artifactReferencesList.getSelection());
            }
        });

        // this creates the controls, but we can't use the layout, so we
        // reparent the panel containing the controls
        OneToOneLayout applicationDetailsLayout = new OneToOneLayout()
                .setPlain(true)
                .setHeadline(Singleton.MESSAGES.label_applicationDetails())
                .setDescription(
                        Singleton.MESSAGES.description_applicationDetails())
                .setMaster(null, formWidget).addDetail(Singleton.MESSAGES.label_services(), _servicesEditor.asWidget())
                .addDetail(Singleton.MESSAGES.label_references(), _referencesEditor.asWidget())
                .addDetail(Singleton.MESSAGES.label_properties(), _propertiesEditor.asWidget())
                .addDetail(Singleton.MESSAGES.label_artifacts(), _artifactReferencesList.asWidget())
                .addDetail(Singleton.MESSAGES.label_transformers(), _transformationsEditor.asWidget())
                .addDetail(Singleton.MESSAGES.label_validators(), _validatorsList.asWidget());
        applicationDetailsLayout.build();
        formWidget.getParent().setStyleName("fill-layout-width"); //$NON-NLS-1$

        /* disable updating "key" field. */
        _propertiesEditor.getPropertyTable().getColumn(0).setFieldUpdater(null);

        SimpleLayout layout = new SimpleLayout()
                .setPlain(true)
                .setTitle(Singleton.MESSAGES.label_switchYardApplications())
                .setHeadline(Singleton.MESSAGES.label_applications())
                .setDescription(
                        Singleton.MESSAGES.description_applications())
                .addContent(Singleton.MESSAGES.label_applications(), _applicationsList.asWidget())
                .addContent(Singleton.MESSAGES.label_applicationDetails(), formWidget.getParent());
        return layout.build();
    }

    @Override
    public void setPresenter(ApplicationPresenter presenter) {
        _presenter = presenter;
    }

    @Override
    public void setApplications(List<Application> applications) {
        _applicationsList.setData(applications);
    }

    @Override
    public void setApplication(Application application) {
        _selectedApplication = application;
        _applicationDetailsForm.clearValues();
        _applicationDetailsForm.edit(application);
        _applicationsList.setSelection(application);
        _artifactReferencesList.setData(application == null ? null : application.getArtifacts());
        _servicesEditor.setApplication(application);
        _referencesEditor.setApplication(application);
        _propertiesEditor.setProperties(application == null ? null : application.getName(), application == null
                || application.getProperties() == null ? new ArrayList<PropertyRecord>() : application.getProperties());
        _transformationsEditor.setApplication(application);
        _validatorsList.setData(application == null ? null : application.getValidators());
    }

}
