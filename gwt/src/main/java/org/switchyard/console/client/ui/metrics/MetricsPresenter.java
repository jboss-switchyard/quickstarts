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

package org.switchyard.console.client.ui.metrics;

import java.util.List;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.plugins.RuntimeGroup;
import org.jboss.as.console.client.shared.state.ServerSelectionChanged;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.spi.RuntimeExtension;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.model.MessageMetrics;
import org.switchyard.console.client.model.ServiceMetrics;
import org.switchyard.console.client.model.SwitchYardStore;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.ResetPresentersEvent;

/**
 * RuntimePresenter
 * 
 * Presenter for SwitchYard application.
 * 
 * @author Rob Cernich
 */
public class MetricsPresenter extends Presenter<MetricsPresenter.MyView, MetricsPresenter.MyProxy> implements
        ServerSelectionChanged.ChangeListener {

    /**
     * MyProxy
     * 
     * The proxy type associated with this presenter.
     */
    @ProxyCodeSplit
    @NameToken(NameTokens.METRICS_PRESENTER)
    @RuntimeExtension(name = NameTokens.RUNTIME_TEXT, group = RuntimeGroup.METRICS, key = NameTokens.SUBSYSTEM)
    public interface MyProxy extends Proxy<MetricsPresenter>, Place {
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
        void setPresenter(MetricsPresenter presenter);

        /**
         * @param serviceMetrics metrics for all services deployed on the
         *            server.
         */
        void setServices(List<ServiceMetrics> serviceMetrics);

        /**
         * @param referenceMetrics metrics for all references deployed on the
         *            server.
         */
        void setReferences(List<ServiceMetrics> referenceMetrics);

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
    private final SwitchYardStore _switchYardStore;

    /**
     * Create a new RuntimePresenter.
     * 
     * @param eventBus the injected EventBus.
     * @param view the injected MyView.
     * @param proxy the injected MyProxy.
     * @param placeManager the injected PlaceManager.
     * @param revealStrategy the RevealStrategy
     * @param switchYardStore the injected SwitchYardStore.
     */
    @Inject
    public MetricsPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
            RevealStrategy revealStrategy, SwitchYardStore switchYardStore) {
        super(eventBus, view, proxy);

        _placeManager = placeManager;
        _revealStrategy = revealStrategy;
        _switchYardStore = switchYardStore;
    }

    /**
     * Resets system metrics.
     */
    public void resetSystemMetrics() {
        _switchYardStore.resetSystemMetrics(new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                getEventBus().fireEvent(new ResetPresentersEvent());
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Unknown error", caught.getMessage());
            }
        });
    }
    
    /**
     * Resets metrics for the named service/reference.
     * 
     * @param metrics the service/reference metrics.
     */
    public void resetMetrics(ServiceMetrics metrics) {
        _switchYardStore.resetMetrics(metrics.getName(), metrics.getApplication(), new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                getEventBus().fireEvent(new ResetPresentersEvent());
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Unknown error", caught.getMessage());
            }
        });
    }

    @Override
    public void onServerSelectionChanged(boolean isRunning) {
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
        getEventBus().addHandler(ServerSelectionChanged.TYPE, this);
    }

    @Override
    protected void onReveal() {
        super.onReveal();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                fireEvent(new LHSHighlightEvent(NameTokens.METRICS_PRESENTER));
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
        getView().setServices(null);
        getView().setReferences(null);
        getView().clearMetrics();
        loadSystemMetrics();
        loadServicesList();
        loadReferencesList();
    }

    private void loadServicesList() {
        _switchYardStore.loadAllServiceMetrics(new AsyncCallback<List<ServiceMetrics>>() {
            @Override
            public void onSuccess(List<ServiceMetrics> serviceMetrics) {
                getView().setServices(serviceMetrics);
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Unknown error", caught.getMessage());
            }
        });
    }

    private void loadReferencesList() {
        _switchYardStore.loadAllReferenceMetrics(new AsyncCallback<List<ServiceMetrics>>() {
            @Override
            public void onSuccess(List<ServiceMetrics> referenceMetrics) {
                getView().setReferences(referenceMetrics);
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
