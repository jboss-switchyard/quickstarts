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

import java.util.List;

import org.jboss.as.console.client.widgets.DisclosureStackHeader;
import org.jboss.as.console.client.widgets.LHSNavTree;
import org.jboss.as.console.client.widgets.LHSNavTreeItem;
import org.switchyard.console.client.model.SwitchYardDeployment;
import org.switchyard.console.client.model.SwitchYardModule;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Navigator for SwitchYard console.
 * 
 * @author Rob Cernich
 */
public class ApplicationNavigator {

    private VerticalPanel _stack;

    private LayoutPanel _layout;

    private LHSNavTree _deploymentsTree;

    private LHSNavTree _modulesTree;

    /**
     * Create a new ApplicationNavigator.
     */
    public ApplicationNavigator() {
        super();

        _layout = new LayoutPanel();
        _layout.getElement().setAttribute("style", "width:99%;border-right:1px solid #E0E0E0");
        _layout.setStyleName("fill-_layout");

        _stack = new VerticalPanel();
        _stack.setStyleName("fill-_layout-width");

        // ----------------------------------------------------

        _deploymentsTree = new LHSNavTree("switchyard");
        DisclosurePanel deploymentsPanel = new DisclosureStackHeader("Deployments").asWidget();
        deploymentsPanel.setContent(_deploymentsTree);
        _stack.add(deploymentsPanel);

        // ----------------------------------------------------

        _modulesTree = new LHSNavTree("switchyard");
        DisclosurePanel modulesPanel = new DisclosureStackHeader("Modules").asWidget();
        modulesPanel.setContent(_modulesTree);
        _stack.add(modulesPanel);

        // ----------------------------------------------------

        Tree commonTree = new LHSNavTree("switchyard");
        DisclosurePanel commonPanel = new DisclosureStackHeader("General Configuration").asWidget();
        commonPanel.setContent(commonTree);

        LHSNavTreeItem[] commonItems = new LHSNavTreeItem[] {new LHSNavTreeItem("Properties", "switchyard/system")};

        for (LHSNavTreeItem item : commonItems) {
            commonTree.addItem(item);
        }

        _stack.add(commonPanel);

        _layout.add(_stack);

    }

    /**
     * @return this navigator as a Widget.
     */
    public Widget asWidget() {
        return _layout;
    }

    /**
     * @param deployments
     *            the deployments to be set within the Deployments section.
     */
    public void updateDeployments(List<SwitchYardDeployment> deployments) {
        _deploymentsTree.removeItems();

        String parentPlace = "switchyard/deployment;deployment=";
        for (SwitchYardDeployment deployment : deployments) {
            if (true) { // !deployment.isDisabled())
                final String key = deployment.getName().toLowerCase().replace(" ", "_");
                String token = parentPlace + deployment.getName();
                final LHSNavTreeItem link = new LHSNavTreeItem(deployment.getName(), token);
                link.setKey(key);
                _deploymentsTree.addItem(link);
            }
        }
    }

    /**
     * @param modules
     *            the modules to be set within the Modules section.
     */
    public void updateModules(List<SwitchYardModule> modules) {
        _modulesTree.removeItems();

        String parentPlace = "switchyard/module;module=";
        for (SwitchYardModule module : modules) {
            if (true) { // !deployment.isDisabled())
                final String key = module.getName().toLowerCase().replace(" ", "_");
                String token = parentPlace + key;
                final LHSNavTreeItem link = new LHSNavTreeItem(module.getName(), token);
                link.setKey(key);
                _modulesTree.addItem(link);
            }
        }
    }
}
