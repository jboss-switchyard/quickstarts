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
import org.switchyard.console.client.model.SwitchYardDeployment;
import org.switchyard.console.client.model.SwitchYardModule;
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
 * ApplicationPresenter
 * 
 * Main presenter for SwitchYard console.
 * 
 * @author Rob Cernich
 */
public class ApplicationPresenter extends
        Presenter<ApplicationPresenter.ApplicationView, ApplicationPresenter.ApplicationProxy> {

    private PlaceManager _placeManager;

    private boolean _revealDefault = true;

    private SwitchYardStore _switchYardStore;

    /**
     * ApplicationView
     * 
     * The view type for this presenter.
     */
    public interface ApplicationView extends View {
        /**
         * @param deployments
         *            the deployments to be used by the view.
         */
        void updateDeployments(List<SwitchYardDeployment> deployments);

        /**
         * @param modules
         *            the modules to be used by the view.
         */
        void updateModules(List<SwitchYardModule> modules);
    }

    /**
     * ApplicationProxy
     * 
     * The proxy type for this presenter.
     */
    @ProxyCodeSplit
    @NameToken(NameTokens.SWITCH_YARD_PRESENTER)
    public interface ApplicationProxy extends ProxyPlace<ApplicationPresenter> {
    }

    /** The main content slot for this presenter. */
    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MAIN_CONTENT = new GwtEvent.Type<RevealContentHandler<?>>();

    /**
     * Create a new ApplicationPresenter.
     * 
     * @param eventBus
     *            the injected EventBus.
     * @param view
     *            the injected ApplicationView.
     * @param proxy
     *            the injected ApplicationProxy.
     * @param placeManager
     *            the injected PlaceManager
     * @param deploymentStore
     *            the injected SwitchYardStore.
     */
    @Inject
    public ApplicationPresenter(EventBus eventBus, ApplicationView view, ApplicationProxy proxy,
            PlaceManager placeManager, SwitchYardStore deploymentStore) {
        super(eventBus, view, proxy);
        this._placeManager = placeManager;
        this._switchYardStore = deploymentStore;
    }

    /**
     * Load a default sub page upon first reveal and highlight navigation
     * sections in subsequent requests.
     * 
     * @param request
     *            the place request.
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

        _switchYardStore.loadDeployments(new AsyncCallback<List<SwitchYardDeployment>>() {
            @Override
            public void onSuccess(List<SwitchYardDeployment> result) {
                getView().updateDeployments(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Unknown error", caught);
                Console.MODULES.getMessageCenter().notify(
                        new Message("Unknown error", caught.getMessage(), Message.Severity.Error));
            }
        });

        _switchYardStore.loadModules(new AsyncCallback<List<SwitchYardModule>>() {
            @Override
            public void onSuccess(List<SwitchYardModule> result) {
                getView().updateModules(result);
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
