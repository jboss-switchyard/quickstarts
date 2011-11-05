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

package org.switchyard.console.client.gin;

import org.jboss.as.console.client.auth.CurrentUser;
import org.jboss.as.console.client.auth.LoggedInGatekeeper;
import org.jboss.as.console.client.auth.SignInPagePresenter;
import org.jboss.as.console.client.auth.SignInPageView;
import org.jboss.as.console.client.core.ApplicationProperties;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.DefaultPlaceManager;
import org.jboss.as.console.client.core.Footer;
import org.jboss.as.console.client.core.Header;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.message.MessageBar;
import org.jboss.as.console.client.core.message.MessageCenter;
import org.jboss.as.console.client.core.message.MessageCenterView;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.HandlerMapping;
import org.jboss.as.console.client.shared.dispatch.InvocationMetrics;
import org.jboss.as.console.client.shared.dispatch.impl.DMRHandler;
import org.jboss.as.console.client.shared.dispatch.impl.DispatchAsyncImpl;
import org.jboss.as.console.client.shared.dispatch.impl.HandlerRegistry;
import org.switchyard.console.client.bridge.MainLayoutViewImpl;
import org.switchyard.console.client.model.SwitchYardStore;
import org.switchyard.console.client.model.SwitchYardStoreImpl;
import org.switchyard.console.client.ui.application.ApplicationPresenter;
import org.switchyard.console.client.ui.application.ApplicationView;
import org.switchyard.console.client.ui.component.ComponentConfigurationPresenterFactory;
import org.switchyard.console.client.ui.component.ComponentConfigurationViewFactory;
import org.switchyard.console.client.ui.component.ComponentPresenter;
import org.switchyard.console.client.ui.component.ComponentView;
import org.switchyard.console.client.ui.config.ConfigPresenter;
import org.switchyard.console.client.ui.config.ConfigView;
import org.switchyard.console.client.ui.main.MainPresenter;
import org.switchyard.console.client.ui.main.MainViewImpl;
import org.switchyard.console.client.ui.service.ServicePresenter;
import org.switchyard.console.client.ui.service.ServiceView;
import org.switchyard.console.components.client.extension.DefaultComponentProvider;
import org.switchyard.console.components.client.extension.DefaultComponentProviderImpl;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.DefaultProxyFailureHandler;
import com.gwtplatform.mvp.client.RootPresenter;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyFailureHandler;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;

/**
 * SwitchYardClientModule
 * 
 * SwitchYard Ginjector bindings.
 * 
 * @author Rob Cernich
 */
public class SwitchYardClientModule extends AbstractPresenterModule {

    protected void configure() {
        // main layout
        bind(Header.class).to(org.switchyard.console.client.bridge.Header.class).in(Singleton.class);
        bind(Footer.class).in(Singleton.class);

        // supporting components
        bind(MessageBar.class).in(Singleton.class);
        bind(MessageCenter.class).in(Singleton.class);
        bind(MessageCenterView.class).in(Singleton.class);

        // ----------------------------------------------------------------------

        bind(PlaceManager.class).to(DefaultPlaceManager.class).in(Singleton.class);
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(ProxyFailureHandler.class).to(DefaultProxyFailureHandler.class).in(Singleton.class);
        bind(Gatekeeper.class).to(LoggedInGatekeeper.class);
        bind(BootstrapContext.class).to(org.switchyard.console.client.bridge.BootstrapContext.class)
                .in(Singleton.class);
        bind(ApplicationProperties.class).to(org.switchyard.console.client.bridge.BootstrapContext.class).in(
                Singleton.class);

        bind(TokenFormatter.class).to(ParameterTokenFormatter.class).in(Singleton.class);
        bind(RootPresenter.class).asEagerSingleton();
        bind(CurrentUser.class).in(Singleton.class);

        // sign in
        bindPresenter(SignInPagePresenter.class, SignInPagePresenter.MyView.class, SignInPageView.class,
                SignInPagePresenter.MyProxy.class);

        // main layout
        bindPresenter(MainLayoutPresenter.class, MainLayoutPresenter.MainLayoutView.class, MainLayoutViewImpl.class,
                MainLayoutPresenter.MainLayoutProxy.class);

        // ----------------------------------------------------------------------

        bind(DispatchAsync.class).to(DispatchAsyncImpl.class).in(Singleton.class);
        bind(HandlerMapping.class).to(HandlerRegistry.class).in(Singleton.class);
        bind(DMRHandler.class).in(Singleton.class);
        bind(InvocationMetrics.class).in(Singleton.class);

        // ----------------------------------------------------------------------

        // switchyard application

        bind(SwitchYardStore.class).to(SwitchYardStoreImpl.class).in(Singleton.class);
        bindPresenter(MainPresenter.class, MainPresenter.MyView.class, MainViewImpl.class, MainPresenter.MyProxy.class);
        bindPresenter(ConfigPresenter.class, ConfigPresenter.MyView.class, ConfigView.class,
                ConfigPresenter.MyProxy.class);
        bindPresenter(ApplicationPresenter.class, ApplicationPresenter.MyView.class, ApplicationView.class,
                ApplicationPresenter.MyProxy.class);
        bindPresenter(ServicePresenter.class, ServicePresenter.MyView.class, ServiceView.class,
                ServicePresenter.MyProxy.class);

        // components
        bindPresenter(ComponentPresenter.class, ComponentPresenter.MyView.class, ComponentView.class,
                ComponentPresenter.MyProxy.class);
        bindPresenterWidgetFactory(ComponentPresenter.PresenterFactory.class,
                ComponentConfigurationPresenterFactory.class, ComponentPresenter.ViewFactory.class,
                ComponentConfigurationViewFactory.class);

        bind(DefaultComponentProvider.class).to(DefaultComponentProviderImpl.class);
    }

}
