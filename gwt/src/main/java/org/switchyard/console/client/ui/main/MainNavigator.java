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

package org.switchyard.console.client.ui.main;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.widgets.stack.DisclosureStackPanel;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.model.Application;
import org.switchyard.console.client.model.Service;
import org.switchyard.console.components.client.extension.ComponentProviders;
import org.switchyard.console.components.client.internal.ComponentExtensionManager.ComponentProviderProxy;
import org.switchyard.console.components.client.model.Component;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Navigator for SwitchYard console.
 * 
 * @author Rob Cernich
 */
public class MainNavigator {

    private VerticalPanel _stack;
    private LayoutPanel _layout;
    private LHSNavTree _applicationsTree;
    private LHSNavTree _servicesTree;
    private LHSNavTree _commonTree;
    private TreeItem _componentsTree;
    private ComponentProviders _componentProviders;

    /**
     * Create a new MainNavigator.
     * 
     * @param componentProviders the ComponentProviders
     */
    public MainNavigator(ComponentProviders componentProviders) {
        super();

        _componentProviders = componentProviders;

        _layout = new LayoutPanel();
        // _layout.getElement().setAttribute("style",
        // "width:99%;border-right:1px solid #E0E0E0");
        _layout.setStyleName("fill-_layout");

        _stack = new VerticalPanel();
        _stack.setStyleName("fill-_layout-width");
        _stack.setWidth("100%");

        // ----------------------------------------------------

        _applicationsTree = new LHSNavTree("switchyard");
        DisclosurePanel applicationsPanel = new DisclosureStackPanel("Applications").asWidget();
        applicationsPanel.setContent(_applicationsTree);
        _stack.add(applicationsPanel);

        // ----------------------------------------------------

        _servicesTree = new LHSNavTree("switchyard");
        DisclosurePanel servicesPanel = new DisclosureStackPanel("Services").asWidget();
        servicesPanel.setContent(_servicesTree);
        _stack.add(servicesPanel);

        // ----------------------------------------------------

        _componentsTree = new TreeItem("Components");

        // ----------------------------------------------------

        _commonTree = new LHSNavTree("switchyard");
        DisclosurePanel commonPanel = new DisclosureStackPanel("System").asWidget();
        commonPanel.setContent(_commonTree);

        TreeItem[] commonItems = new TreeItem[] {new LHSNavTreeItem("Details", "switchyard/system"), _componentsTree };

        for (TreeItem item : commonItems) {
            _commonTree.addItem(item);
        }

        _stack.add(commonPanel);

        _layout.add(new ScrollPanel(_stack));
    }

    /**
     * @return this navigator as a Widget.
     */
    public Widget asWidget() {
        return _layout;
    }

    /**
     * @param applications the applications to be set within the Applications
     *            section.
     */
    public void updateApplications(List<Application> applications) {
        LHSNavTreeItem item = (LHSNavTreeItem) _applicationsTree.getSelectedItem();
        _applicationsTree.removeItems();

        for (Application application : applications) {
            final String applicationName = application.getName();
            String token = NameTokens.createApplicationLink(applicationName);
            final LHSNavTreeItem link = new LHSNavTreeItem(NameTokens.parseQName(applicationName)[1], token);
            link.setKey(applicationName);
            _applicationsTree.addItem(link);

            link.setSelected(item != null && link.getKey().equals(item.getKey()));
        }
    }

    /**
     * @param services the services to be set within the Services section.
     */
    public void updateServices(List<Service> services) {
        LHSNavTreeItem item = (LHSNavTreeItem) _servicesTree.getSelectedItem();
        _servicesTree.removeItems();

        for (Service service : services) {
            final String serviceName = service.getName();
            final String applicationName = service.getApplication();
            final String token = NameTokens.createServiceLink(serviceName, applicationName);
            final LHSNavTreeItem link = new LHSNavTreeItem(NameTokens.parseQName(serviceName)[1], token);
            link.setKey(applicationName + ":" + serviceName);
            _servicesTree.addItem(link);

            link.setSelected(item != null && link.getKey().equals(item.getKey()));
        }
    }

    /**
     * @param components the components to be set within the Components section.
     */
    public void updateComponents(List<Component> components) {
        Collections.sort(components, new Comparator<Component>() {
            @Override
            public int compare(Component o1, Component o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        LHSNavTreeItem item = (LHSNavTreeItem) _commonTree.getSelectedItem();
        _componentsTree.removeItems();

        for (Component component : components) {
            final String componentName = component.getName();
            final String displayName;
            final ComponentProviderProxy extension = _componentProviders
                    .getExtensionProviderByComponentName(componentName);
            if (extension == null) {
                displayName = componentName;
            } else {
                displayName = extension.getDisplayName();
            }
            String token = NameTokens.createComponentLink(componentName);
            final LHSNavTreeItem link = new LHSNavTreeItem(displayName, token);
            link.setKey(componentName);
            _componentsTree.addItem(link);

            link.setSelected(item != null && link.getKey().equals(item.getKey()));
        }
    }
}
