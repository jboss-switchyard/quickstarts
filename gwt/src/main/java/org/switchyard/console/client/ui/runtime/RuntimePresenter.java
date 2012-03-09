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

package org.switchyard.console.client.ui.runtime;

import java.util.List;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.model.MessageMetrics;
import org.switchyard.console.client.model.Service;
import org.switchyard.console.client.model.ServiceMetrics;
import org.switchyard.console.client.model.SwitchYardStore;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;

/**
 * RuntimePresenter
 * 
 * Presenter for SwitchYard application.
 * 
 * @author Rob Cernich
 */
public class RuntimePresenter extends Presenter<RuntimePresenter.MyView, RuntimePresenter.MyProxy> implements
        ServerSelectionEvent.ServerSelectionListener {

    /**
     * MyProxy
     * 
     * The proxy type associated with this presenter.
     */
    @ProxyCodeSplit
    @NameToken(NameTokens.RUNTIME_PRESENTER)
    public interface MyProxy extends Proxy<RuntimePresenter>, Place {
    }

    /**
     * MyView
     * 
     * The view type associated with this presenter.
     */
    public interface MyView extends View {
        /**
         * @param presenter the associated presenter.
         */
        void setPresenter(RuntimePresenter presenter);

        /**
         * @param services the services deployed on the server.
         */
        void setServices(List<Service> services);

        /**
         * @param service set the selected service
         */
        void setService(Service service);

        /**
         * @param serviceMetrics the metrics for the selected service.
         */
        void setServiceMetrics(ServiceMetrics serviceMetrics);

        /**
         * @param systemMetrics the metrics for the system.
         */
        void setSystemMetrics(MessageMetrics systemMetrics);

        /**
         * Clear the view.
         */
        void clearMetrics();
    }

    private final PlaceManager _placeManager;
    private final RevealStrategy _revealStrategy;
    private final CurrentServerSelection _serverSelection;
    private final SwitchYardStore _switchYardStore;
    private String _serviceName;

    /**
     * Create a new RuntimePresenter.
     * 
     * @param eventBus the injected EventBus.
     * @param view the injected MyView.
     * @param proxy the injected MyProxy.
     * @param placeManager the injected PlaceManager.
     * @param revealStrategy the RevealStrategy
     * @param serverSelection the server selection
     * @param switchYardStore the injected SwitchYardStore.
     */
    @Inject
    public RuntimePresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
            RevealStrategy revealStrategy, CurrentServerSelection serverSelection, SwitchYardStore switchYardStore) {
        super(eventBus, view, proxy);

        _placeManager = placeManager;
        _revealStrategy = revealStrategy;
        _serverSelection = serverSelection;
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
        PlaceRequest request = new PlaceRequest(NameTokens.RUNTIME_PRESENTER);
        if (service != null) {
            request = request.with(NameTokens.SERVICE_NAME_PARAM, URL.encode(service.getName()));
        }
        _placeManager.revealRelativePlace(request, -1);
    }

    @Override
    public void onServerSelection(String hostName, final ServerInstance server) {
        getView().clearMetrics();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                if (isVisible()) {
                    loadMetrics();
                }
            }
        });
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(ServerSelectionEvent.TYPE, this);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);
        _serviceName = request.getParameter(NameTokens.SERVICE_NAME_PARAM, null);
        if (_serviceName != null) {
            _serviceName = URL.decode(_serviceName);
        }
    }

    @Override
    protected void onReveal() {
        super.onReveal();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                fireEvent(new LHSHighlightEvent("unused", NameTokens.RUNTIME_TEXT,
                        NameTokens.SUBSYSTEM_DOMAIN_TREE_CATEGORY));
                fireEvent(new LHSHighlightEvent("unused", NameTokens.RUNTIME_TEXT,
                        NameTokens.SUBSYSTEM_STANDALONE_TREE_CATEGORY));
            }
        });
    }

    @Override
    protected void onReset() {
        super.onReset();
        loadMetrics();
    }

    @Override
    protected void revealInParent() {
        _revealStrategy.revealInRuntimeParent(this);
    }

    private void loadMetrics() {
        if (!_serverSelection.isActive()) {
            Console.warning(Console.CONSTANTS.common_err_server_not_active());
            getView().setServices(null);
            getView().clearMetrics();
            return;
        }
        loadSystemMetrics();
        loadServicesList();
        loadServiceMetrics();
    }

    private void loadServicesList() {
        _switchYardStore.loadServices(new AsyncCallback<List<Service>>() {
            @Override
            public void onSuccess(List<Service> services) {
                getView().setServices(services);
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Unknown error", caught.getMessage());
            }
        });
    }

    private void loadServiceMetrics() {
        if (_serviceName == null) {
            getView().setServiceMetrics(_switchYardStore.getBeanFactory().serviceMetrics().as());
            return;
        }
        _switchYardStore.loadServiceMetrics(_serviceName, new AsyncCallback<ServiceMetrics>() {

            @Override
            public void onSuccess(ServiceMetrics result) {
                Service service = _switchYardStore.getBeanFactory().service().as();
                service.setName(result.getName());

                getView().setService(service);
                getView().setServiceMetrics(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Unknown error", caught.getMessage());
            }
        });
    }

    private void loadSystemMetrics() {
        _switchYardStore.loadSystemMetrics(new AsyncCallback<MessageMetrics>() {

            @Override
            public void onSuccess(MessageMetrics result) {
                getView().setSystemMetrics(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Unknown error", caught.getMessage());
            }
        });
    }

}
