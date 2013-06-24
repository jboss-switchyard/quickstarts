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
    public void changeTab(Tab tab, TabData tabData, String historyToken) {
        _tabPanel.changeTab(tab, tabData, historyToken);
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
