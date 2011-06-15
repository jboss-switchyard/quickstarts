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

import org.jboss.as.console.client.core.message.Message;
import org.switchyard.console.client.Console;
import org.switchyard.console.client.model.SwitchYardDeployment;
import org.switchyard.console.client.model.SwitchYardModule;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * ApplicationViewImpl
 * 
 * Main application view for SwitchYard console. Provides navigator and content
 * widgets.
 * 
 * @author Rob Cernich
 */
public class ApplicationViewImpl extends ViewImpl implements ApplicationPresenter.ApplicationView {

    private ApplicationPresenter _presenter;

    private SplitLayoutPanel _layout;
    private LayoutPanel _contentCanvas;
    private ApplicationNavigator _lhsNavigation;

    /**
     * Create a new ApplicationViewImpl.
     */
    public ApplicationViewImpl() {
        super();

        _layout = new SplitLayoutPanel(4);

        _contentCanvas = new LayoutPanel();
        _lhsNavigation = new ApplicationNavigator();

        _layout.addWest(_lhsNavigation.asWidget(), 240);
        _layout.add(_contentCanvas);

    }

    @Override
    public Widget asWidget() {
        return _layout;
    }

    @Override
    public void updateDeployments(List<SwitchYardDeployment> deployments) {
        _lhsNavigation.updateDeployments(deployments);
    }

    @Override
    public void updateModules(List<SwitchYardModule> modules) {
        _lhsNavigation.updateModules(modules);
    }

    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == ApplicationPresenter.TYPE_MAIN_CONTENT) {
            if (content != null) {
                setContent(content);
            }
        } else {
            Console.MODULES.getMessageCenter().notify(new Message("Unknown slot requested:" + slot));
        }
    }

    private void setContent(Widget newContent) {
        _contentCanvas.clear();
        _contentCanvas.add(newContent);
    }

}
