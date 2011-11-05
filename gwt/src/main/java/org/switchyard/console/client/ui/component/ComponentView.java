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

package org.switchyard.console.client.ui.component;

import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.ballroom.client.layout.RHSContentPanel;
import org.switchyard.console.client.Console;
import org.switchyard.console.client.Singleton;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
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
        LayoutPanel wrapper = new RHSContentPanel(Singleton.MESSAGES.header_content_componentConfiguration());
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
            if (content != null) {
                setMainContent(content);
            }
        } else {
            Console.MODULES.getMessageCenter().notify(new Message("Unknown slot requested:" + slot));
        }
    }

    private void setMainContent(Widget content) {
        _mainContentPanel.clear();

        if (content != null) {
            _mainContentPanel.add(content);
        }
    }
}
