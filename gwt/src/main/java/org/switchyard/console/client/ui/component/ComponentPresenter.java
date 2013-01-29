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

import org.jboss.as.console.client.Console;
import org.jboss.as.console.spi.SubsystemExtension;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.model.SwitchYardStore;
import org.switchyard.console.client.ui.config.ConfigPresenter;
import org.switchyard.console.components.client.model.Component;
import org.switchyard.console.components.client.ui.ComponentConfigurationPresenter;
import org.switchyard.console.components.client.ui.ComponentConfigurationPresenter.ComponentConfigurationView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

/**
 * ComponentPresenter
 * 
 * Presenter for SwitchYard component configuration.
 * 
 * @author Rob Cernich
 */
public class ComponentPresenter extends Presenter<ComponentPresenter.MyView, ComponentPresenter.MyProxy> {

    /**
     * MyProxy
     * 
     * The proxy type used by this presenter.
     */
    @ProxyCodeSplit
    @NameToken(NameTokens.SYSTEM_CONFIG_PRESENTER)
    @SubsystemExtension(name = "Runtime Details", group = "SwitchYard", key = "switchyard")
    public interface MyProxy extends Proxy<ComponentPresenter>, Place {
    }

    /** The main content slot for this presenter. */
    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MAIN_CONTENT = new GwtEvent.Type<RevealContentHandler<?>>();

    /**
     * MyView
     * 
     * The view type used by this presenter.
     */
    public interface MyView extends View {
        /**
         * @param presenter the presenter associated with the view.
         */
        void setPresenter(ComponentPresenter presenter);
    }

    /**
     * PresenterFactory
     * 
     * A factory for creating ComponentConfigurationPresenter objects.
     * 
     * @author Rob Cernich
     */
    public interface PresenterFactory {
        /**
         * @param componentName the component.
         * @return a ComponentConfigurationPresenter specific to the component.
         */
        public ComponentConfigurationPresenter create(String componentName);
    }

    /**
     * ViewFactory
     * 
     * A factory for creating ComponentConfigurationView objects.
     * 
     * @author Rob Cernich
     */
    public interface ViewFactory {
        /**
         * @param componentName the component.
         * @return a ComponentConfigurationView specific to the component.
         */
        public ComponentConfigurationView create(String componentName);
    }

    private final PlaceManager _placeManager;
    private final SwitchYardStore _switchYardStore;
    private final PresenterFactory _factory;
    private ComponentConfigurationPresenter _presenterWidget;

    /**
     * Create a new ComponentPresenter.
     * 
     * @param eventBus the injected EventBus.
     * @param view the injected MyView.
     * @param proxy the injected MyProxy.
     * @param placeManager the injected PlaceManager.
     * @param switchYardStore the injected SwitchYardStore.
     * @param factory the PresenterFactory for specialized component presenters.
     */
    @Inject
    public ComponentPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
            SwitchYardStore switchYardStore, PresenterFactory factory) {
        super(eventBus, view, proxy);

        _placeManager = placeManager;
        _switchYardStore = switchYardStore;
        _factory = factory;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onReset() {
        super.onReset();

        releasePresenterWidget();

        loadComponent(_placeManager.getCurrentPlaceRequest().getParameter(NameTokens.COMPONENT_NAME_PARAM, null));
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, ConfigPresenter.TYPE_COMPONENT_CONTENT, this);
    }

    private void loadComponent(String componentName) {
        if (componentName == null) {
            return;
        }
        _switchYardStore.loadComponent(componentName, new AsyncCallback<Component>() {

            @Override
            public void onSuccess(Component component) {
                _presenterWidget = _factory.create(component.getName());
                _presenterWidget.bind();
                ComponentPresenter.this.setInSlot(TYPE_MAIN_CONTENT, _presenterWidget, false);
                _presenterWidget.setComponent(component);
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Unknown error", caught.getMessage());
            }
        });
    }

    private void releasePresenterWidget() {
        if (_presenterWidget == null) {
            return;
        }
        _presenterWidget.unbind();
        _presenterWidget = null;
    }

}
