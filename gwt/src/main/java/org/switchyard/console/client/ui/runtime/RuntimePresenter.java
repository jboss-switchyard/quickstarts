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

package org.switchyard.console.client.ui.runtime;

import org.jboss.as.console.client.plugins.RuntimeGroup;
import org.jboss.as.console.client.shared.state.ServerSelectionChanged;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.spi.RuntimeExtension;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;
import org.switchyard.console.client.NameTokens;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.RequestTabsHandler;
import com.gwtplatform.mvp.client.TabContainerPresenter;
import com.gwtplatform.mvp.client.TabView;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.RequestTabs;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

/**
 * RuntimePresenter
 * 
 * Presenter for SwitchYard application.
 * 
 * @author Rob Cernich
 */
public class RuntimePresenter extends TabContainerPresenter<RuntimePresenter.MyView, RuntimePresenter.MyProxy>
        implements ServerSelectionChanged.ChangeListener {

    /**
     * MyProxy
     * 
     * The proxy type associated with this presenter.
     */
    @ProxyCodeSplit
    @NameToken(NameTokens.RUNTIME_OPERATIONS_PRESENTER)
    @RuntimeExtension(name = NameTokens.RUNTIME_TEXT, group = RuntimeGroup.OPERATiONS, key = NameTokens.SUBSYSTEM)
    public interface MyProxy extends Proxy<RuntimePresenter> {
    }

    /**
     * MyView
     * 
     * The view type associated with this presenter.
     */
    public interface MyView extends TabView {
        /**
         * Refresh the tab view.
         */
        // public void refreshTabs();
    }

    /**
     * This will be the event sent to our "unknown" child presenters, in order
     * for them to register their tabs.
     */
    @RequestTabs
    public static final Type<RequestTabsHandler> TYPE_REQUEST_TABS = new Type<RequestTabsHandler>();

    // XXX: gwtp 0.7+
    // /**
    // * Fired by child proxie's when their tab content is changed.
    // */
    // @ChangeTab
    // public static final Type<ChangeTabHandler> TYPE_ChangeTab = new
    // Type<ChangeTabHandler>();

    /**
     * Use this in leaf presenters, inside their {@link #revealInParent} method.
     */
    @ContentSlot
    public static final Type<RevealContentHandler<?>> TYPE_SET_TAB_CONTENT = new Type<RevealContentHandler<?>>();

    private final RevealStrategy _revealStrategy;

    /**
     * Create a new RuntimePresenter.
     * 
     * @param eventBus the injected EventBus.
     * @param view the injected MyView.
     * @param proxy the injected MyProxy.
     * @param revealStrategy the RevealStrategy
     */
    @Inject
    public RuntimePresenter(EventBus eventBus, MyView view, MyProxy proxy, RevealStrategy revealStrategy) {
        super(eventBus, view, proxy, TYPE_SET_TAB_CONTENT, TYPE_REQUEST_TABS);

        _revealStrategy = revealStrategy;
    }

    @Override
    public void onServerSelectionChanged(boolean isRunning) {
        // getView().refreshTabs();
    }

    @Override
    protected void onBind() {
        super.onBind();
        // getEventBus().addHandler(ServerSelectionEvent.TYPE, this);
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
    protected void revealInParent() {
        _revealStrategy.revealInRuntimeParent(this);
    }

}
