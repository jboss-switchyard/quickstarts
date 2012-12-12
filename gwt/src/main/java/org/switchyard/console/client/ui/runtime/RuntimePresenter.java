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

import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;
import org.switchyard.console.client.NameTokens;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.RequestTabsHandler;
import com.gwtplatform.mvp.client.TabContainerPresenter;
import com.gwtplatform.mvp.client.TabView;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.RequestTabs;
import com.gwtplatform.mvp.client.proxy.Place;
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
        implements ServerSelectionEvent.ServerSelectionListener {

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
    public void onServerSelection(String hostName, final ServerInstance server) {
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
