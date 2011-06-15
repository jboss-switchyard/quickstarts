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

package org.switchyard.console.client.ui.config;

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.switchyard.console.client.BeanFactory;
import org.switchyard.console.client.Console;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.ui.main.ApplicationPresenter;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;

/**
 * ConfigPresenter
 * 
 * Presenter for SwitchYard system configuration.
 * 
 * @author Rob Cernich
 */
public class ConfigPresenter extends Presenter<ConfigPresenter.MyView, ConfigPresenter.MyProxy> {

    private final PlaceManager _placeManager;

    private DispatchAsync _dispatcher;

    private BeanFactory _factory;

    /**
     * MyProxy
     * 
     * The proxy type associated with this presenter.
     */
    @ProxyCodeSplit
    @NameToken(NameTokens.SYSTEM_CONFIG_PRESENTER)
    public interface MyProxy extends Proxy<ConfigPresenter>, Place {
    }

    /**
     * MyView
     * 
     * The view type associated with this presenter.
     */
    public interface MyView extends View {
        /**
         * @param presenter
         *            the presenter for the view.
         */
        void setPresenter(ConfigPresenter presenter);
    }

    /**
     * Create a new ConfigPresenter.
     * 
     * @param eventBus
     *            the injected EventBus.
     * @param view
     *            the injected MyView.
     * @param proxy
     *            the injected MyProxy.
     * @param placeManager
     *            the injected PlaceManager.
     * @param dispatcher
     *            the injected DispatchAsync.
     * @param factory
     *            the injected BeanFactory.
     */
    @Inject
    public ConfigPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
            DispatchAsync dispatcher, BeanFactory factory) {
        super(eventBus, view, proxy);

        this._placeManager = placeManager;
        this._dispatcher = dispatcher;
        this._factory = factory;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onReset() {
        super.onReset();

        HTML headerContent = new HTML(new SafeHtmlBuilder().appendEscaped(
                Singleton.MESSAGES.header_content_switchYardConfiguration()).toSafeHtml());
        headerContent.setStylePrimaryName("header-content");
        Console.MODULES.getHeader().setContent(headerContent);
        Console.MODULES.getHeader().highlight(NameTokens.SYSTEM_CONFIG_PRESENTER);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ApplicationPresenter.TYPE_MAIN_CONTENT, this);
    }

}
