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

package org.switchyard.console.client.ui.config;

import java.util.List;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.model.SwitchYardStore;
import org.switchyard.console.client.model.SystemDetails;
import org.switchyard.console.client.ui.component.ComponentPresenter.PresenterFactory;
import org.switchyard.console.components.client.model.Component;
import org.switchyard.console.components.client.ui.ComponentConfigurationPresenter;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

/**
 * ConfigPresenter
 * 
 * Presenter for SwitchYard system configuration.
 * 
 * @author Rob Cernich
 */
public class ConfigPresenter extends Presenter<ConfigPresenter.MyView, ConfigPresenter.MyProxy> {

    /**
     * MyProxy
     * 
     * The proxy type associated with this presenter.
     */
    @ProxyCodeSplit
    @NameToken(NameTokens.SYSTEM_CONFIG_PRESENTER)
    public interface MyProxy extends Proxy<ConfigPresenter>, Place {
    }

    /** The slot where component specific details are displayed. */
    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_COMPONENT_CONTENT = new GwtEvent.Type<RevealContentHandler<?>>();

    /**
     * MyView
     * 
     * The view type associated with this presenter.
     */
    public interface MyView extends View {
        /**
         * @param presenter the presenter for the view.
         */
        void setPresenter(ConfigPresenter presenter);

        /**
         * @param systemDetails details of the SwitchYard system.
         */
        void setSystemDetails(SystemDetails systemDetails);

        /**
         * @param components the installed components.
         */
        void setComponents(List<Component> components);
    }

    private final PlaceManager _placeManager;
    private final RevealStrategy _revealStrategy;
    private final SwitchYardStore _switchYardStore;
    private final PresenterFactory _factory;
    private String _componentName;
    private ComponentConfigurationPresenter _presenterWidget;

    /**
     * Create a new ConfigPresenter.
     * 
     * @param eventBus the injected EventBus.
     * @param view the injected MyView.
     * @param proxy the injected MyProxy.
     * @param placeManager the injected PlaceManager.
     * @param revealStrategy the RevealStrategy
     * @param switchYardStore the injected SwitchYardStore.
     * @param factory the PresenterFactory for specialized component presenters.
     */
    @Inject
    public ConfigPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
            RevealStrategy revealStrategy, SwitchYardStore switchYardStore, PresenterFactory factory) {
        super(eventBus, view, proxy);

        _placeManager = placeManager;
        _revealStrategy = revealStrategy;
        _switchYardStore = switchYardStore;
        _factory = factory;
    }

    /**
     * Notifies the presenter that the user wishes to view details about a
     * specific component. The presenter will load the details and pass them
     * back to the view to be displayed.
     * 
     * @param component the selected component.
     */
    public void onComponentSelected(Component component) {
        clearComponentContent();

        PlaceRequest request = new PlaceRequest(NameTokens.SYSTEM_CONFIG_PRESENTER);
        if (component != null) {
            request = request.with(NameTokens.COMPONENT_NAME_PARAM, URL.encode(component.getName()));
        }
        _placeManager.revealRelativePlace(request, -1);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);
        _componentName = request.getParameter(NameTokens.COMPONENT_NAME_PARAM, null);
        if (_componentName != null) {
            _componentName = URL.decode(_componentName);
        }
    }

    @Override
    protected void onReveal() {
        super.onReveal();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                fireEvent(new LHSHighlightEvent("unused", NameTokens.SYSTEM_CONFIG_TEXT,
                        NameTokens.SUBSYSTEM_TREE_CATEGORY));
            }
        });
    }

    @Override
    protected void onReset() {
        super.onReset();

        loadSystemDetails();
        loadComponentsList();
        loadComponent();
    }

    @Override
    protected void onHide() {
        super.onHide();
        clearComponentContent();
    }

    @Override
    protected void revealInParent() {
        _revealStrategy.revealInParent(this);
    }

    private void loadSystemDetails() {
        _switchYardStore.loadSystemDetails(new AsyncCallback<SystemDetails>() {

            @Override
            public void onSuccess(SystemDetails systemDetails) {
                getView().setSystemDetails(systemDetails);
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Unknown error", caught.getMessage());
            }
        });
    }

    private void loadComponentsList() {
        _switchYardStore.loadComponents(new AsyncCallback<List<Component>>() {
            @Override
            public void onSuccess(List<Component> components) {
                getView().setComponents(components);
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Unknown error", caught.getMessage());
            }
        });
    }

    private void loadComponent() {
        if (_componentName == null) {
            clearComponentContent();
            return;
        }
        _switchYardStore.loadComponent(_componentName, new AsyncCallback<Component>() {
            @Override
            public void onSuccess(Component component) {
                _presenterWidget = _factory.create(component.getName());
                _presenterWidget.bind();
                setInSlot(TYPE_COMPONENT_CONTENT, _presenterWidget, false);
                _presenterWidget.setComponent(component);
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Unknown error", caught.getMessage());
            }
        });
    }

    private void clearComponentContent() {
        clearSlot(TYPE_COMPONENT_CONTENT);
        releasePresenterWidget();
    }

    private void releasePresenterWidget() {
        if (_presenterWidget == null) {
            return;
        }
        _presenterWidget.unbind();
        _presenterWidget = null;
    }

}
