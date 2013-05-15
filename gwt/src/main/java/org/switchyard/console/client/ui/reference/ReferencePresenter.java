/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.console.client.ui.reference;

import java.util.List;

import org.jboss.as.console.client.Console;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.model.Binding;
import org.switchyard.console.client.model.Reference;
import org.switchyard.console.client.model.SwitchYardStore;
import org.switchyard.console.client.ui.runtime.RuntimePresenter;
import org.switchyard.console.client.ui.service.GatewayPresenter;

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
 * ReferencePresenter
 * 
 * Presenter for SwitchYard component configuration.
 */
public class ReferencePresenter extends Presenter<ReferencePresenter.MyView, ReferencePresenter.MyProxy> implements
        GatewayPresenter {

    /**
     * MyProxy
     * 
     * The proxy type used by this presenter.
     */
    @ProxyCodeSplit
    @NameToken(NameTokens.REFERENCES_PRESENTER)
    @TabInfo(container = RuntimePresenter.class, label = NameTokens.REFERENCES_TEXT, priority = 3)
    public interface MyProxy extends TabContentProxyPlace<ReferencePresenter> {
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
        void setPresenter(ReferencePresenter presenter);

        /**
         * @param reference set the reference to be viewed/edited.
         */
        void setReference(Reference reference);

        /**
         * @param references the references deployed on the server.
         */
        void setReferencesList(List<Reference> references);
    }

    private final PlaceManager _placeManager;
    private final SwitchYardStore _switchYardStore;
    private String _applicationName;
    private String _referenceName;

    /**
     * Create a new ReferencePresenter.
     * 
     * @param eventBus the injected EventBus.
     * @param view the injected MyView.
     * @param proxy the injected MyProxy.
     * @param placeManager the injected PlaceManager.
     * @param switchYardStore the injected SwitchYardStore.
     */
    @Inject
    public ReferencePresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
            SwitchYardStore switchYardStore) {
        super(eventBus, view, proxy);

        _placeManager = placeManager;
        _switchYardStore = switchYardStore;
    }

    @Override
    public void startGateway(Binding binding) {
        _switchYardStore.startGateway(binding.getName(), _referenceName, _applicationName, new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void dummy) {
                getEventBus().fireEvent(new ResetPresentersEvent());
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Unknown error", caught.getMessage());
            }
        });
    }

    @Override
    public void stopGateway(Binding binding) {
        _switchYardStore.stopGateway(binding.getName(), _referenceName, _applicationName, new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void dummy) {
                getEventBus().fireEvent(new ResetPresentersEvent());
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Unknown error", caught.getMessage());
            }
        });
    }

    /**
     * Notifies the presenter that the user has selected a reference. The
     * presenter will load the reference details and pass them back to the view
     * to be displayed.
     * 
     * @param reference the selected reference.
     */
    public void onReferenceSelected(Reference reference) {
        PlaceRequest request = new PlaceRequest(NameTokens.REFERENCES_PRESENTER);
        if (reference != null && reference.getName() != null) {
            request = request.with(NameTokens.REFERENCE_NAME_PARAM, URL.encode(reference.getName()));
            if (reference.getApplication() != null) {
                request = request.with(NameTokens.APPLICATION_NAME_PARAM, URL.encode(reference.getApplication()));
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

        _referenceName = _placeManager.getCurrentPlaceRequest().getParameter(NameTokens.REFERENCE_NAME_PARAM, null);
        _applicationName = _placeManager.getCurrentPlaceRequest().getParameter(NameTokens.APPLICATION_NAME_PARAM, null);

        if (_referenceName != null) {
            _referenceName = URL.decode(_referenceName);
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

        loadReferencesList();
        loadReference();
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, RuntimePresenter.TYPE_SET_TAB_CONTENT, this);
    }

    private void loadReferencesList() {
        _switchYardStore.loadReferences(new AsyncCallback<List<Reference>>() {
            @Override
            public void onSuccess(List<Reference> references) {
                getView().setReferencesList(references);
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error("Unknown error", caught.getMessage());
            }
        });
    }

    private void loadReference() {
        if (_referenceName == null || _applicationName == null) {
            getView().setReference(_switchYardStore.getBeanFactory().reference().as());
            return;
        }
        _switchYardStore.loadReference(_referenceName, _applicationName, new AsyncCallback<Reference>() {
            @Override
            public void onSuccess(Reference reference) {
                getView().setReference(reference);
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
