/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.console.client.ui.runtime;

import org.jboss.as.console.client.core.DisposableViewImpl;
import org.switchyard.console.client.ui.common.GWTPTabPanel;
import org.switchyard.console.client.ui.runtime.RuntimePresenter.MyView;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Tab;
import com.gwtplatform.mvp.client.TabData;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

/**
 * RuntimeView
 * 
 * <p/>
 * View implementation for SwitchYard runtime metrics.
 * 
 * @author Rob Cernich
 */
public class RuntimeView extends DisposableViewImpl implements MyView {

    private GWTPTabPanel _tabPanel;

    /**
     * Create a new RuntimeView.
     * 
     * @param placeManager the place manager.
     */
    @Inject
    public RuntimeView(PlaceManager placeManager) {
        _tabPanel = new GWTPTabPanel(placeManager);
    }

    @Override
    public Widget asWidget() {
        return _tabPanel.asWidget();
    }

    @Override
    public Widget createWidget() {
        return _tabPanel.asWidget();
    }

    @Override
    public Tab addTab(TabData tabData, String historyToken) {
        return _tabPanel.addTab(tabData, historyToken);
    }

    @Override
    public void removeTab(Tab tab) {
        _tabPanel.removeTab(tab);
    }

    @Override
    public void removeTabs() {
        _tabPanel.removeTabs();
    }

    @Override
    public void setActiveTab(Tab tab) {
        _tabPanel.setActiveTab(tab);
    }

    @Override
    public void setInSlot(Object slot, Widget content) {
        if (slot == RuntimePresenter.TYPE_SET_TAB_CONTENT) {
            _tabPanel.setContent(content);
        } else {
            super.setInSlot(slot, content);
        }
    }

}
