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

package org.switchyard.console.client.ui.design;

import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.switchyard.console.client.BeanFactory;
import org.switchyard.console.client.Console;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.model.SwitchYardDeployment;
import org.switchyard.console.client.model.SwitchYardStore;
import org.switchyard.console.client.ui.main.ApplicationPresenter;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
 * PackagePresenter
 * 
 * Presenter for SwitchYard deployment package.
 * 
 * @author Rob Cernich
 */
public class PackagePresenter extends Presenter<PackagePresenter.MyView, PackagePresenter.MyProxy> {

    private final PlaceManager _placeManager;

    private DispatchAsync _dispatcher;

    private BeanFactory _factory;

    private SwitchYardStore _switchYardStore;

    /**
     * MyProxy
     * 
     * The proxy type associated with this presenter.
     */
    @ProxyCodeSplit
    @NameToken(NameTokens.PACKAGE_CONFIG_PRESENTER)
    public interface MyProxy extends Proxy<PackagePresenter>, Place {
    }

    /**
     * MyView
     * 
     * The view type associated with this presenter.
     */
    public interface MyView extends View {
        /**
         * @param presenter
         *            the associated presenter.
         */
        void setPresenter(PackagePresenter presenter);

        /**
         * @param deployment
         *            the deployment being viewed/processed/edited.
         */
        void setDeployment(SwitchYardDeployment deployment);
    }

    /**
     * Create a new PackagePresenter.
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
     * @param switchYardStore
     *            the injected SwitchYardStore.
     */
    @Inject
    public PackagePresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
            DispatchAsync dispatcher, BeanFactory factory, SwitchYardStore switchYardStore) {
        super(eventBus, view, proxy);

        this._placeManager = placeManager;
        this._dispatcher = dispatcher;
        this._factory = factory;
        this._switchYardStore = switchYardStore;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onReset() {
        super.onReset();

        HTML headerContent = new HTML(new SafeHtmlBuilder().appendEscaped("Deployment Configuration").toSafeHtml());
        headerContent.setStylePrimaryName("header-content");
        Console.MODULES.getHeader().setContent(headerContent);
        Console.MODULES.getHeader().highlight(NameTokens.MODULE_CONFIG_PRESENTER);

        loadModule(_placeManager.getCurrentPlaceRequest().getParameter("deployment", null));
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ApplicationPresenter.TYPE_MAIN_CONTENT, this);
    }

    private void loadModule(String deploymentName) {
        _switchYardStore.loadDeployment(deploymentName, new AsyncCallback<SwitchYardDeployment>() {

            @Override
            public void onSuccess(SwitchYardDeployment result) {
                getView().setDeployment(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Unknown error", caught);
                Console.MODULES.getMessageCenter().notify(
                        new Message("Unknown error", caught.getMessage(), Message.Severity.Error));
            }
        });
    }

}
