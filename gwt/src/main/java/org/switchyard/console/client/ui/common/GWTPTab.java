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
                "GWTPTab is intended to work with GWTPTabPanel, which does not support using custom widgets in the tab bar.");
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
        throw new UnsupportedOperationException("Cannot update label text for GWTPTab.");
    }

}
