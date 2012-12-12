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

package org.switchyard.console.client.ui.service;

import java.util.List;

import org.jboss.as.console.client.Console;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.model.Service;
import org.switchyard.console.client.model.SwitchYardStore;
import org.switchyard.console.client.ui.runtime.RuntimePresenter;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.TabInfo;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.TabContentProxyPlace;

/**
 * ServicePresenter
 * 
 * Presenter for SwitchYard component configuration.
 * 
 * @author Rob Cernich
 */
public class ServicePresenter extends Presenter<ServicePresenter.MyView, ServicePresenter.MyProxy> {

    /**
     * MyProxy
     * 
     * The proxy type used by this presenter.
     */
    @ProxyCodeSplit
    @NameToken(NameTokens.SERVICES_PRESENTER)
    @TabInfo(container = RuntimePresenter.class, label = "Services", priority = 2)
    public interface MyProxy extends TabContentProxyPlace<ServicePresenter> {
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
                Console.error("Unknown error", caught.getMessage());
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void loadService() {
        if (_serviceName == null || _applicationName == null) {
            getView().setService(_switchYardStore.getBeanFactory().service().as());
            return;
        }
        _switchYardStore.loadService(_serviceName, _applicationName, new AsyncCallback<Service>() {

            @Override
            public void onSuccess(Service service) {
                getView().setService(service);
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Unknown error", caught.getMessage());
            }
        });
    }

    protected PlaceManager getPlaceManager() {
        return _placeManager;
    }

}
