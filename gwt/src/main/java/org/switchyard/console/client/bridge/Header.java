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

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.message.MessageBar;
import org.switchyard.console.client.NameTokens;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

/**
 * Top level header, gives access to main applications.
 * 
 * Specialized for SwitchYard.
 * 
 * @author Heiko Braun
 */
public class Header extends org.jboss.as.console.client.core.Header {

    private HTMLPanel _linksPane;
    private String _currentHighlightedSection = null;

    private SimplePanel _headlineContainer;

    /**
     * Create a new Header.
     * 
     * @param messageBar the message bar
     * @param bootstrap the context
     */
    @Inject
    public Header(MessageBar messageBar, BootstrapContext bootstrap) {
        super(messageBar, bootstrap);
    }

    /**
     * @wbp.parser.entryPoint
     * 
     * @return this header, as a Widget.
     */
    @Override
    public Widget asWidget() {

        VerticalPanel outerLayout = new VerticalPanel();
        outerLayout.setStyleName("header");
        outerLayout.setHeight("150px");

        outerLayout.add(getLogoSection());
        outerLayout.add(getContentSection());
        outerLayout.add(getLinksSection());

        return outerLayout;
    }

    private Widget getContentSection() {
        HorizontalPanel contentLayout = new HorizontalPanel();
        contentLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        contentLayout.setStyleName("fill-layout-width");
        contentLayout.setHeight("34px");

        _headlineContainer = new SimplePanel();
        _headlineContainer.setStyleName("fill-layout");
        contentLayout.add(_headlineContainer);

        HTML settingsLink = new HTML(Console.CONSTANTS.common_label_settings());
        settingsLink.setStylePrimaryName("cross-reference");
        DOM.setStyleAttribute(settingsLink.getElement(), "paddingRight", "20px");
        DOM.setStyleAttribute(settingsLink.getElement(), "color", "#4A5D75");
        // settingsLink.getElement().getParentElement().setAttribute("style",
        // "padding-right:20px;color:#4A5D75");
        settingsLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Console.MODULES.getPlaceManager().revealPlace(new PlaceRequest(NameTokens.SettingsPresenter));
            }
        });
        contentLayout.add(settingsLink);
        contentLayout.setCellHorizontalAlignment(settingsLink, HasHorizontalAlignment.ALIGN_RIGHT);
        contentLayout.setCellWidth(settingsLink, "50%");
        // settingsLink.getElement().getParentElement().setAttribute("style",
        // "text-align:right; padding-right:20px;color:#4A5D75");

        return contentLayout;
    }

    private Widget getLogoSection() {

        Image logo = new Image("images/switchyard-banner.png", 0, 0, 905, 110);
        logo.setStyleName("header-logo");
        return logo;
    }

    private Widget getLinksSection() {
        _linksPane = new HTMLPanel(createLinks());
        _linksPane.getElement().setId("header-links-section");

        // TODO: any sections?
        String[][] sections = {};

        for (String[] section : sections) {
            final String name = section[0];
            final String id = "header-" + name;
            HTML widget = new HTML(section[1]);

            widget.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Console.MODULES.getPlaceManager().revealPlace(new PlaceRequest(name));
                }
            });
            _linksPane.add(widget, id);

        }
        return _linksPane;
    }

    private String createLinks() {

        // TODO: any sections?
        String[][] sections = {};

        SafeHtmlBuilder headerString = new SafeHtmlBuilder();

        if (sections.length > 0) {
            headerString.appendHtmlConstant("<table class='header-links' cellpadding=0 cellspacing=0 border=0>");
            headerString.appendHtmlConstant("<tr id='header-links-ref'>");

            headerString
                    .appendHtmlConstant("<td style=\"width:1px;height:25px\"><img src=\"images/header/header_bg_line.png\"/></td>");
            for (String[] section : sections) {

                final String name = section[0];
                final String id = "header-" + name;
                String styleClass = "header-link";
                String styleAtt = "vertical-align:middle; text-align:center";

                String td = "<td style='" + styleAtt + "' width='100px' id='" + id + "'" + " class='" + styleClass
                        + "'></td>";
                headerString.appendHtmlConstant(td);
                // headerString.append(title);

                headerString
                        .appendHtmlConstant("<td style=\"width: 1px;\"><img src=\"images/header/header_bg_line.png\"/></td>");
            }

            headerString.appendHtmlConstant("</tr></table>");
        }

        return headerString.toSafeHtml().asString();
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        String historyToken = event.getValue();
        if (historyToken.equals(_currentHighlightedSection)) {
            return;
        } else {
            _currentHighlightedSection = historyToken;
        }

        if (historyToken.indexOf("/") != -1) {
            highlight(historyToken.substring(0, historyToken.indexOf("/")));
        } else {
            highlight(historyToken);
        }
    }

    @Override
    public void highlight(String name) {

        com.google.gwt.user.client.Element target = _linksPane.getElementById("header-links-ref");
        if (target != null) { // standalone doesn't provide any top level links
            NodeList<Node> childNodes = target.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node n = childNodes.getItem(i);
                if (Node.ELEMENT_NODE == n.getNodeType()) {
                    Element element = (Element) n;
                    if (element.getId().equals("header-" + name)) {
                        element.addClassName("header-link-selected");
                    } else {
                        element.removeClassName("header-link-selected");
                    }
                }
            }
        }
    }

    @Override
    public void setContent(Widget content) {
        _headlineContainer.clear();
        _headlineContainer.add(content);
    }
}
