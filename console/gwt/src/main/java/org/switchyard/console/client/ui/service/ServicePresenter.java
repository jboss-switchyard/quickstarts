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

package org.switchyard.console.client.ui.service;

import java.util.List;
import java.util.Map;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.spi.AccessControl;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;
import org.switchyard.console.client.Messages;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.model.Binding;
import org.switchyard.console.client.model.Service;
import org.switchyard.console.client.model.SwitchYardStore;
import org.switchyard.console.client.model.Throttling;
import org.switchyard.console.client.ui.runtime.RuntimePresenter;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.TabInfo;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ResetPresentersEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.TabContentProxyPlace;

/**
 * ServicePresenter
 * 
 * Presenter for SwitchYard component configuration.
 * 
 * @author Rob Cernich
 */
public class ServicePresenter extends Presenter<ServicePresenter.MyView, ServicePresenter.MyProxy> implements
        GatewayPresenter {

    /**
     * MyProxy
     * 
     * The proxy type used by this presenter.
     */
    @ProxyCodeSplit
    @NameToken(NameTokens.SERVICES_PRESENTER)
    @AccessControl(resources = {"/{selected.host}/{selected.server}/subsystem=switchyard" })
    public interface MyProxy extends TabContentProxyPlace<ServicePresenter> {
    }

    @TabInfo(container = RuntimePresenter.class, priority = 2)
    static String getLabel(Messages messages) {
        return messages.label_services();
    }

    /**
     * MyView
     * 
     * The view type used by this presenter.
     */
    public interface MyView extends View {
        /**
         * @param presenter the presenter associated with the view.
         */
        void setPresenter(ServicePresenter presenter);

        /**
         * @param service set the service to be viewed/edited.
         */
        void setService(Service service);

        /**
         * @param services the services deployed on the server.
         */
        void setServicesList(List<Service> services);
    }

    private final PlaceManager _placeManager;
    private final SwitchYardStore _switchYardStore;
    private String _applicationName;
    private String _serviceName;

    /**
     * Create a new ServicePresenter.
     * 
     * @param eventBus the injected EventBus.
     * @param view the injected MyView.
     * @param proxy the injected MyProxy.
     * @param placeManager the injected PlaceManager.
     * @param switchYardStore the injected SwitchYardStore.
     */
    @Inject
    public ServicePresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
            SwitchYardStore switchYardStore) {
        super(eventBus, view, proxy);

        _placeManager = placeManager;
        _switchYardStore = switchYardStore;
    }

    @Override
    public void startGateway(Binding binding) {
        _switchYardStore.startGateway(binding.getName(), _serviceName, _applicationName, new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void dummy) {
                getEventBus().fireEvent(new ResetPresentersEvent());
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error(Singleton.MESSAGES.error_unknown(), caught.getMessage());
            }
        });
    }

    @Override
    public void stopGateway(Binding binding) {
        _switchYardStore.stopGateway(binding.getName(), _serviceName, _applicationName, new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void dummy) {
                getEventBus().fireEvent(new ResetPresentersEvent());
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error(Singleton.MESSAGES.error_unknown(), caught.getMessage());
            }
        });
    }

    /**
     * Updates the throttling configuration for the specified service.
     * 
     * @param service the service
     * @param throttling the new throttling configuration
     * @param changeset what changed from the original configuration
     */
    public void updateThrottling(Service service, Throttling throttling, Map<String, Object> changeset) {
        _switchYardStore.updateThrottling(service,
                _switchYardStore.processChangeSet(Throttling.class, throttling, changeset, false),
                new AsyncCallback<Void>() {
                    @Override
                    public void onSuccess(Void dummy) {
                        getEventBus().fireEvent(new ResetPresentersEvent());
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        Console.error(Singleton.MESSAGES.error_unknown(), caught.getMessage());
                    }
                });
    }

    /**
     * Notifies the presenter that the user has selected a service. The
     * presenter will load the service details and pass them back to the view to
     * be displayed.
     * 
     * @param service the selected service.
     */
    public void onServiceSelected(Service service) {
        PlaceRequest request = new PlaceRequest(NameTokens.SERVICES_PRESENTER);
        if (service != null && service.getName() != null) {
            request = request.with(NameTokens.SERVICE_NAME_PARAM, URL.encode(service.getName()));
            if (service.getApplication() != null) {
                request = request.with(NameTokens.APPLICATION_NAME_PARAM, URL.encode(service.getApplication()));
            }
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

        _serviceName = _placeManager.getCurrentPlaceRequest().getParameter(NameTokens.SERVICE_NAME_PARAM, null);
        _applicationName = _placeManager.getCurrentPlaceRequest().getParameter(NameTokens.APPLICATION_NAME_PARAM, null);

        if (_serviceName != null) {
            _serviceName = URL.decode(_serviceName);
        }
        if (_applicationName != null) {
            _applicationName = URL.decode(_applicationName);
        }
    }

    @Override
    protected void onReveal() {
        super.onReveal();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                fireEvent(new LHSHighlightEvent(NameTokens.RUNTIME_OPERATIONS_PRESENTER));
            }
        });
    }

    @Override
    protected void onReset() {
        super.onReset();

        loadServicesList();
        loadService();
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, RuntimePresenter.TYPE_SET_TAB_CONTENT, this);
    }

    private void loadServicesList() {
        _switchYardStore.loadServices(new AsyncCallback<List<Service>>() {
            @Override
            public void onSuccess(List<Service> services) {
                getView().setServicesList(services);
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error(Singleton.MESSAGES.error_unknown(), caught.getMessage());
            }
        });
    }

    private void loadService() {
        if (_serviceName == null || _applicationName == null) {
            getView().setService(null);
            return;
        }
        _switchYardStore.loadService(_serviceName, _applicationName, new AsyncCallback<Service>() {

            @Override
            public void onSuccess(Service service) {
                getView().setService(service);
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error(Singleton.MESSAGES.error_unknown(), caught.getMessage());
            }
        });
    }

    protected PlaceManager getPlaceManager() {
        return _placeManager;
    }

}
