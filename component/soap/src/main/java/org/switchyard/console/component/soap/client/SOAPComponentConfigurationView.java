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
package org.switchyard.console.component.soap.client;

import java.util.Collections;

import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.forms.DefaultGroupRenderer;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.RenderMetaData;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.switchyard.console.components.client.ui.BaseComponentConfigurationView;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * SOAPComponentConfigurationView
 * 
 * Customized component configuration view for the SOAP component.
 * 
 * @author Rob Cernich
 */
public class SOAPComponentConfigurationView extends BaseComponentConfigurationView {

    private static final String SOCKET_ADDR = "socketAddr";

    private TextItem _socketAddr;

    @SuppressWarnings("rawtypes")
    @Override
    protected Widget createComponentDetailsWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        layout.add(new ContentGroupLabel("Configured Properties"));

        String title = "Socket Address (" + SOCKET_ADDR + ")";
        _socketAddr = new TextItem(SOCKET_ADDR, title) {
            @Override
            public void setValue(String value) {
                if (value == null || value.length() == 0) {
                    value = "<not set>";
                }
                super.setValue(value);
            }
        };

        RenderMetaData metaData = new RenderMetaData();
        metaData.setNumColumns(1);
        metaData.setTitleWidth(title.length());
        layout.add(new DefaultGroupRenderer().render(metaData, "null",
                Collections.<String, FormItem> singletonMap(SOCKET_ADDR, _socketAddr)));

        return layout;
    }

    @Override
    protected void updateComponentDetails() {
        if (getComponent() == null || getComponent().getProperties() == null) {
            _socketAddr.setValue(null);
        } else {
            _socketAddr.setValue(getComponent().getProperties().get(SOCKET_ADDR));
        }
    }

    @Override
    protected String getComponentName() {
        return "SOAP";
    }

}
