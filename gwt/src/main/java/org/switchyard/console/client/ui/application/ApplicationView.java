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

import java.util.ArrayList;
import java.util.List;

import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.viewframework.builder.OneToOneLayout;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.ballroom.client.widgets.forms.Form;
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
        applicationDetailsPanel.setStyleName("fill-layout-width");

        _applicationDetailsForm = new Form<Application>(Application.class);
        // XXX: '_' included in names to workaround bug in form builder
        _applicationDetailsForm.setFields(new LocalNameFormItem("name_1", "Application Name"), new NamespaceFormItem(
                "name_2", "Application Namespace"));
        Widget formWidget = _applicationDetailsForm.asWidget();
        formWidget.getElement().setAttribute("style", "margin:15px");

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
                .setHeadline("Application Details")
                .setDescription(
                        "Displays details for a specific application.  Select an application to see its implementation details.")
                .setMaster(null, formWidget).addDetail("Services", _servicesEditor.asWidget())
                .addDetail("References", _referencesEditor.asWidget())
                .addDetail("Properties", _propertiesEditor.asWidget())
                .addDetail("Artifacts", _artifactReferencesList.asWidget())
                .addDetail("Transformers", _transformationsEditor.asWidget())
                .addDetail("Validators", _validatorsList.asWidget());
        applicationDetailsLayout.build();
        formWidget.getParent().setStyleName("fill-layout-width");

        /* disable updating "key" field. */
        _propertiesEditor.getPropertyTable().getColumn(0).setFieldUpdater(null);

        SimpleLayout layout = new SimpleLayout()
                .setPlain(true)
                .setTitle("SwitchYard Applications")
                .setHeadline("Applications")
                .setDescription(
                        "Displays a list of deployed SwitchYard applications.  Select an application to see more details.")
                .addContent("Applications", _applicationsList.asWidget())
                .addContent("Application Details", formWidget.getParent());
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
