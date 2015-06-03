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
package org.switchyard.console.client.ui.common;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.Tab;
import com.gwtplatform.mvp.client.TabData;

/**
 * GWTPTab
 * 
 * <p/>
 * Tab implementation for use with GWT Platform tab presenters.
 */
public class GWTPTab implements Tab {

    private final TabData _tabData;
    private String _historyToken;

    /**
     * Create a new GWTPTab.
     * 
     * @param tabData the tab data
     * @param historyToken the history location
     */
    public GWTPTab(TabData tabData, String historyToken) {
        _tabData = tabData;
        _historyToken = historyToken;
    }

    @Override
    public void activate() {
    }

    @Override
    public Widget asWidget() {
        throw new UnsupportedOperationException(
                "GWTPTab is intended to work with GWTPTabPanel, which does not support using custom widgets in the tab bar."); //$NON-NLS-1$
    }

    @Override
    public void deactivate() {
    }

    @Override
    public float getPriority() {
        return _tabData.getPriority();
    }

    @Override
    public String getText() {
        return _tabData.getLabel();
    }

    @Override
    public void setTargetHistoryToken(String historyToken) {
        _historyToken = historyToken;
    }

    /**
     * @return the history token associated with this tab's place.
     */
    public String getTargetHistoryToken() {
        return _historyToken;
    }

    @Override
    public void setText(String text) {
        throw new UnsupportedOperationException("Cannot update label text for GWTPTab."); //$NON-NLS-1$
    }

}
