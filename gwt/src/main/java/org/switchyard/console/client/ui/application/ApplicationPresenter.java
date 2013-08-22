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

package org.switchyard.console.client.ui.application;

import java.util.List;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.gin.SwitchYardGinjector;
import org.switchyard.console.client.model.Application;
import org.switchyard.console.client.model.ArtifactReference;
import org.switchyard.console.client.model.Reference;
import org.switchyard.console.client.model.Service;
import org.switchyard.console.client.model.SwitchYardStore;
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
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.TabContentProxyPlace;

/**
 * ApplicationPresenter
 * 
 * Presenter for SwitchYard application.
 * 
 * @author Rob Cernich
 */
public class ApplicationPresenter extends Presenter<ApplicationPresenter.MyView, ApplicationPresenter.MyProxy>
        implements PropertyManagement {

    /**
     * MyProxy
     * 
     * The proxy type associated with this presenter.
     */
    @ProxyCodeSplit
    @NameToken(NameTokens.APPLICATIONS_PRESENTER)
    public interface MyProxy extends TabContentProxyPlace<ApplicationPresenter> {
    }

    @TabInfo(container = RuntimePresenter.class, priority = 1)
    static String getLabel(SwitchYardGinjector ginjector) {
        return ginjector.getMessages().label_applications();
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
        void setPresenter(ApplicationPresenter presenter);

        /**
         * @param applications the applications deployed on the server.
         */
        void setApplications(List<Application> applications);

        /**
         * @param application the application being viewed/processed/edited.
         */
        void setApplication(Application application);
    }

    private final PlaceManager _placeManager;
    private final SwitchYardStore _switchYardStore;
    private String _applicationName;

    /**
     * Create a new ApplicationPresenter.
     * 
     * @param eventBus the injected EventBus.
     * @param view the injected MyView.
     * @param proxy the injected MyProxy.
     * @param placeManager the injected PlaceManager.
     * @param switchYardStore the injected SwitchYardStore.
     */
    @Inject
    public ApplicationPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
            SwitchYardStore switchYardStore) {
        super(eventBus, view, proxy);

        _placeManager = placeManager;
        _switchYardStore = switchYardStore;
    }

    /**
     * Notifies the presenter that the user has selected an application. The
     * presenter will load the application details and pass them back to the
     * view to be displayed.
     * 
     * @param application the selected application.
     */
    public void onApplicationSelected(Application application) {
        PlaceRequest request = new PlaceRequest(NameTokens.APPLICATIONS_PRESENTER);
        if (application != null) {
            request = request.with(NameTokens.APPLICATION_NAME_PARAM, URL.encode(application.getName()));
        }
        _placeManager.revealRelativePlace(request, -1);
    }

    /**
     * Notifies the presenter that the user has selected an artifact reference.
     * The presenter will navigate to the artifacts page.
     * 
     * @param artifact the selected artifact.
     */
    public void onArtifactSelected(ArtifactReference artifact) {
        PlaceRequest request = new PlaceRequest(NameTokens.ARTIFACTS_PRESENTER);
        if (artifact != null) {
            request = request.with(NameTokens.ARTIFACT_REFERENCE_KEY_PARAM, URL.encode(artifact.key()));
        }
        _placeManager.revealRelativePlace(request, -1);
    }

    /**
     * Notifies the presenter that the user wishes to view details about a
     * specific service.
     * 
     * @param service the service.
     * @param application the application containing the service.
     */
    public void onNavigateToService(Service service, Application application) {
        if (service == null || application == null) {
            Console.error(Singleton.MESSAGES.error_navigateToService());
            return;
        }
        _placeManager.revealRelativePlace(
                new PlaceRequest(NameTokens.SERVICES_PRESENTER).with(NameTokens.SERVICE_NAME_PARAM,
                        URL.encode(service.getName())).with(NameTokens.APPLICATION_NAME_PARAM,
                        URL.encode(application.getName())), -1);
    }

    /**
     * Notifies the presenter that the user wishes to view details about a
     * specific reference.
     * 
     * @param reference the reference.
     * @param application the application containing the reference.
     */
    public void onNavigateToReference(Reference reference, Application application) {
        if (reference == null || application == null) {
            Console.error(Singleton.MESSAGES.error_navigateToReference());
            return;
        }
        _placeManager.revealRelativePlace(
                new PlaceRequest(NameTokens.REFERENCES_PRESENTER).with(NameTokens.REFERENCE_NAME_PARAM,
                        URL.encode(reference.getName())).with(NameTokens.APPLICATION_NAME_PARAM,
                        URL.encode(application.getName())), -1);
    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {
    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        if (reference == null) {
            return;
        }
        _switchYardStore.setApplicationProperty(reference, prop, new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error(Singleton.MESSAGES.error_unknown(), caught.getMessage());
            }
        });
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
    }

    @Override
    public void closePropertyDialoge() {
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);
        _applicationName = request.getParameter(NameTokens.APPLICATION_NAME_PARAM, null);
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

        loadApplicationsList();
        loadApplication();
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, RuntimePresenter.TYPE_SET_TAB_CONTENT, this);
    }

    private void loadApplicationsList() {
        _switchYardStore.loadApplications(new AsyncCallback<List<Application>>() {
            @Override
            public void onSuccess(List<Application> applications) {
                getView().setApplications(applications);
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error(Singleton.MESSAGES.error_unknown(), caught.getMessage());
            }
        });
    }

    private void loadApplication() {
        if (_applicationName == null) {
            getView().setApplication(_switchYardStore.getBeanFactory().application().as());
            return;
        }
        _switchYardStore.loadApplication(_applicationName, new AsyncCallback<Application>() {

            @Override
            public void onSuccess(Application result) {
                getView().setApplication(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error(Singleton.MESSAGES.error_unknown(), caught.getMessage());
            }
        });
    }

}
