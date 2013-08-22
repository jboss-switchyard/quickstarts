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

package org.switchyard.console.client.ui.artifacts;

import java.util.List;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.gin.SwitchYardGinjector;
import org.switchyard.console.client.model.Application;
import org.switchyard.console.client.model.ArtifactReference;
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
 * ArtifactPresenter
 * 
 * Presenter for SwitchYard application.
 * 
 * @author Rob Cernich
 */
public class ArtifactPresenter extends Presenter<ArtifactPresenter.MyView, ArtifactPresenter.MyProxy> {

    /**
     * MyProxy
     * 
     * The proxy type associated with this presenter.
     */
    @ProxyCodeSplit
    @NameToken(NameTokens.ARTIFACTS_PRESENTER)
    public interface MyProxy extends TabContentProxyPlace<ArtifactPresenter> {
    }

    @TabInfo(container = RuntimePresenter.class, priority = 4)
    static String getLabel(SwitchYardGinjector ginjector) {
        return ginjector.getMessages().label_artifacts();
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
        void setPresenter(ArtifactPresenter presenter);

        /**
         * @param artifacts referenced by applications.
         */
        void setArtifacts(List<ArtifactReference> artifacts);

        /**
         * @param artifactKey the selected artifact.
         */
        void setSelectedArtifact(String artifactKey);
    }

    private final PlaceManager _placeManager;
    private final RevealStrategy _revealStrategy;
    private final SwitchYardStore _switchYardStore;
    private String _artifactKey;

    /**
     * Create a new ArtifactPresenter.
     * 
     * @param eventBus the injected EventBus.
     * @param view the injected MyView.
     * @param proxy the injected MyProxy.
     * @param placeManager the injected PlaceManager.
     * @param revealStrategy the RevealStrategy
     * @param switchYardStore the injected SwitchYardStore.
     */
    @Inject
    public ArtifactPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
            RevealStrategy revealStrategy, SwitchYardStore switchYardStore) {
        super(eventBus, view, proxy);

        _placeManager = placeManager;
        _revealStrategy = revealStrategy;
        _switchYardStore = switchYardStore;
    }

    /**
     * Navigates to the application page, displaying the details of the
     * application.
     * 
     * @param application the selected application
     */
    public void onApplicationSelected(Application application) {
        if (application == null) {
            Console.error(Singleton.MESSAGES.error_navigateToApplication());
            return;
        }
        _placeManager.revealRelativePlace(
                new PlaceRequest(NameTokens.APPLICATIONS_PRESENTER).with(NameTokens.APPLICATION_NAME_PARAM,
                        URL.encode(application.getName())), -1);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);
        _artifactKey = request.getParameter(NameTokens.ARTIFACT_REFERENCE_KEY_PARAM, null);
        if (_artifactKey != null) {
            _artifactKey = URL.decode(_artifactKey);
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
        loadArtifactReferences();
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, RuntimePresenter.TYPE_SET_TAB_CONTENT, this);
    }

    private void loadArtifactReferences() {
        _switchYardStore.loadArtifactReferences(new AsyncCallback<List<ArtifactReference>>() {
            @Override
            public void onSuccess(List<ArtifactReference> artifacts) {
                getView().setArtifacts(artifacts);
                getView().setSelectedArtifact(_artifactKey);
            }

            @Override
            public void onFailure(Throwable caught) {
                Console.error(Singleton.MESSAGES.error_unknown(), caught.getMessage());
            }
        });
    }

}
