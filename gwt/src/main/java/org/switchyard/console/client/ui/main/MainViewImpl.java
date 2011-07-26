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
import org.switchyard.console.client.model.Application;
import org.switchyard.console.client.model.Component;
import org.switchyard.console.client.model.Service;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * MainViewImpl
 * 
 * Main application view for SwitchYard console. Provides navigator and content
 * widgets.
 * 
 * @author Rob Cernich
 */
public class MainViewImpl extends ViewImpl implements MainPresenter.MyView {

    private MainPresenter _presenter;

    private SplitLayoutPanel _layout;
    private LayoutPanel _contentCanvas;
    private MainNavigator _lhsNavigation;

    /**
     * Create a new MainViewImpl.
     */
    public MainViewImpl() {
        super();

        _layout = new SplitLayoutPanel(4);

        _contentCanvas = new LayoutPanel();
        _lhsNavigation = new MainNavigator();

        _layout.addWest(_lhsNavigation.asWidget(), 240);
        _layout.add(_contentCanvas);

    }

    @Override
    public Widget asWidget() {
        return _layout;
    }

    @Override
    public void updateApplications(List<Application> applications) {
        _lhsNavigation.updateApplications(applications);
    }

    @Override
    public void updateComponents(List<Component> components) {
        _lhsNavigation.updateComponents(components);
    }

    @Override
    public void updateServices(List<Service> services) {
        _lhsNavigation.updateServices(services);
    }

    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == MainPresenter.TYPE_MAIN_CONTENT) {
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
