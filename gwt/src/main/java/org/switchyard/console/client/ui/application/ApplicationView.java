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

import java.util.List;

import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.switchyard.console.client.model.Application;
import org.switchyard.console.client.ui.widgets.LocalNameFormItem;
import org.switchyard.console.client.ui.widgets.NamespaceFormItem;

import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;

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
    private ApplicationTransformationsEditor _transformationsEditor;
    private ApplicationsList _applicationsList;
    private Application _selectedApplication;

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
        _transformationsEditor = new ApplicationTransformationsEditor(_presenter);

        TabPanel tabpanel = new TabPanel();
        tabpanel.addStyleName("default-tabpanel");

        tabpanel.add(_servicesEditor.asWidget(), "Services");
        tabpanel.add(_transformationsEditor.asWidget(), "Transformers");

        tabpanel.selectTab(0);

        applicationDetailsPanel.add(new ContentGroupLabel("Application Details"));
        applicationDetailsPanel.add(formWidget);
        applicationDetailsPanel.add(tabpanel);

        SimpleLayout layout = new SimpleLayout()
                .setTitle("SwitchYard Applications")
                .setHeadline("Applications")
                .setDescription(
                        "Displays a list of deployed SwitchYard applications.  Select an application to see more details.")
                .addContent("Applications", _applicationsList.asWidget())
                .addContent("Application Details", applicationDetailsPanel);
        return layout.build();
    }

    @Override
    public void setPresenter(ApplicationPresenter presenter) {
        this._presenter = presenter;
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
        _servicesEditor.setApplication(application);
        _transformationsEditor.setApplication(application);
    }

}
