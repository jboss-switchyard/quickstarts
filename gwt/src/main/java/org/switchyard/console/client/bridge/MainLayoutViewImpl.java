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

package org.switchyard.console.client.bridge;

import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.message.Message;
import org.switchyard.console.client.Console;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * The main console layout that builds on GWT 2.1 layout panels.
 * 
 * Specialized for SwitchYard.
 * 
 * @author Heiko Braun
 */
public class MainLayoutViewImpl extends ViewImpl implements MainLayoutPresenter.MainLayoutView {

    private DockLayoutPanel _panel;

    private LayoutPanel _headerPanel;
    private LayoutPanel _mainContentPanel;
    private LayoutPanel _footerPanel;

    private org.jboss.as.console.client.core.Header _header;

    /**
     * Create a new MainLayoutViewImpl.
     * 
     * This was overridden simply to reset the header height.
     */
    @Inject
    public MainLayoutViewImpl() {

        _mainContentPanel = new LayoutPanel();
        _mainContentPanel.setStyleName("_mainContent _panel");

        _headerPanel = new LayoutPanel();
        _headerPanel.setStyleName("_header _panel");

        _footerPanel = new LayoutPanel();
        _footerPanel.setStyleName("_footer _panel");

        _panel = new DockLayoutPanel(Style.Unit.PX);
        _panel.setStyleName("_panel");
        DOM.setElementAttribute(_panel.getElement(), "id", "switchyardContainer");
        
        // XXX: SwitchYard specialization
        _panel.addNorth(_headerPanel, 200);
        _panel.addSouth(_footerPanel, 30);
        _panel.add(_mainContentPanel);

        _header = Console.MODULES.getHeader();
        getHeaderPanel().add(_header.asWidget());

        getFooterPanel().add(Console.MODULES.getFooter().asWidget());
    }

    @Override
    public Widget asWidget() {
        return _panel;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == MainLayoutPresenter.TYPE_MainContent) {
            if (content != null) {
                setMainContent(content);
            }
        } else {
            Console.MODULES.getMessageCenter().notify(new Message("Unknown slot requested:" + slot));
        }
    }

    /**
     * Set the main window content (i.e. the content between the header and the
     * footer).
     * 
     * @param content the content.
     */
    public void setMainContent(Widget content) {
        _mainContentPanel.clear();

        if (content != null) {
            _mainContentPanel.add(content);
        }
    }

    /**
     * @return the header panel.
     */
    public LayoutPanel getHeaderPanel() {
        return _headerPanel;
    }

    /**
     * @return the footer panel.
     */
    public LayoutPanel getFooterPanel() {
        return _footerPanel;
    }

}
