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

package org.switchyard.console.client.ui.main;

import java.util.List;

import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.message.Message;
import org.switchyard.console.client.Console;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.model.Application;
import org.switchyard.console.client.model.Component;
import org.switchyard.console.client.model.Service;
import org.switchyard.console.client.model.SwitchYardStore;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

/**
 * MainPresenter
 * 
 * Main presenter for SwitchYard console.
 * 
 * @author Rob Cernich
 */
public class MainPresenter extends Presenter<MainPresenter.MyView, MainPresenter.MyProxy> {

    private PlaceManager _placeManager;

    private boolean _revealDefault = true;

    private SwitchYardStore _switchYardStore;

    /**
     * MyView
     * 
     * The view type for this presenter.
     */
    public interface MyView extends View {
        /**
         * @param applications the applications to be used by the view.
         */
        void updateApplications(List<Application> applications);

        /**
         * @param components the components to be used by the view.
         */
        void updateComponents(List<Component> components);

        /**
         * @param services the services to be used by the view.
         */
        void updateServices(List<Service> services);
    }

    /**
     * MyProxy
     * 
     * The proxy type for this presenter.
     */
    @ProxyCodeSplit
    @NameToken(NameTokens.SWITCH_YARD_PRESENTER)
    public interface MyProxy extends ProxyPlace<MainPresenter> {
    }

    /** The main content slot for this presenter. */
    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MAIN_CONTENT = new GwtEvent.Type<RevealContentHandler<?>>();

    /**
     * Create a new MainPresenter.
     * 
     * @param eventBus the injected EventBus.
     * @param view the injected MyView.
     * @param proxy the injected MyProxy.
     * @param placeManager the injected PlaceManager
     * @param switchYardStore the injected SwitchYardStore.
     */
    @Inject
    public MainPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
            SwitchYardStore switchYardStore) {
        super(eventBus, view, proxy);
        this._placeManager = placeManager;
        this._switchYardStore = switchYardStore;
    }

    /**
     * Load a default sub page upon first reveal and highlight navigation
     * sections in subsequent requests.
     * 
     * @param request the place request.
     */
    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);

        // reveal default sub page
        if (_revealDefault && NameTokens.SWITCH_YARD_PRESENTER.equals(request.getNameToken())) {
            _placeManager.revealRelativePlace(new PlaceRequest(NameTokens.SYSTEM_CONFIG_PRESENTER));
            _revealDefault = false; // only once
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        HTML headerContent = new HTML(new SafeHtmlBuilder().appendEscaped(
                Singleton.MESSAGES.header_content_switchYardConfiguration()).toSafeHtml());
        headerContent.setStylePrimaryName("header-content");
        Console.MODULES.getHeader().setContent(headerContent);
        Console.MODULES.getHeader().highlight(NameTokens.SYSTEM_CONFIG_PRESENTER);

        _switchYardStore.loadApplications(new AsyncCallback<List<Application>>() {
            @Override
            public void onSuccess(List<Application> result) {
                getView().updateApplications(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Unknown error", caught);
                Console.MODULES.getMessageCenter().notify(
                        new Message("Unknown error", caught.getMessage(), Message.Severity.Error));
            }
        });

        _switchYardStore.loadComponents(new AsyncCallback<List<Component>>() {
            @Override
            public void onSuccess(List<Component> result) {
                getView().updateComponents(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Unknown error", caught);
                Console.MODULES.getMessageCenter().notify(
                        new Message("Unknown error", caught.getMessage(), Message.Severity.Error));
            }
        });

        _switchYardStore.loadServices(new AsyncCallback<List<Service>>() {
            @Override
            public void onSuccess(List<Service> result) {
                getView().updateServices(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Unknown error", caught);
                Console.MODULES.getMessageCenter().notify(
                        new Message("Unknown error", caught.getMessage(), Message.Severity.Error));
            }
        });

    }

    @Override
    protected void revealInParent() {
        // reveal in main layout
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_MainContent, this);
    }
}
