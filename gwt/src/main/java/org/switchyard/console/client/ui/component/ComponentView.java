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

package org.switchyard.console.client.ui.component;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * ComponentView
 * 
 * View for SwitchYard module configuration.
 * 
 * @author Rob Cernich
 */
public class ComponentView extends DisposableViewImpl implements ComponentPresenter.MyView {

    private ComponentPresenter _presenter;
    private Panel _mainContentPanel;

    @Override
    public Widget createWidget() {
        VerticalPanel wrapper = new VerticalPanel();
        wrapper.setStyleName("fill-layout-width");
        _mainContentPanel = new SimplePanel();
        wrapper.add(_mainContentPanel);

        return wrapper;
    }

    @Override
    public void setPresenter(ComponentPresenter presenter) {
        this._presenter = presenter;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {
        if (slot == ComponentPresenter.TYPE_MAIN_CONTENT) {
            setMainContent(content);
        } else {
            Console.error("Unknown slot requested:" + slot);
        }
    }

    private void setMainContent(Widget content) {
        _mainContentPanel.clear();

        if (content != null) {
            _mainContentPanel.add(content);
        }
    }
}
