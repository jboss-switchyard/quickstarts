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
