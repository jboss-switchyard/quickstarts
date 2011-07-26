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

import org.jboss.as.console.client.auth.SignInPagePresenter;
import org.jboss.as.console.client.core.ApplicationProperties;
import org.jboss.as.console.client.core.BootstrapContext;
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
import org.switchyard.console.client.model.SwitchYardStore;
import org.switchyard.console.client.ui.application.ApplicationPresenter;
import org.switchyard.console.client.ui.component.ComponentPresenter;
import org.switchyard.console.client.ui.config.ConfigPresenter;
import org.switchyard.console.client.ui.main.MainPresenter;
import org.switchyard.console.client.ui.service.ServicePresenter;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.inject.Provider;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyFailureHandler;

/**
 * Overall module configuration.
 * 
 * @see SwitchYardClientModule
 * 
 * @author Rob Cernich
 */
@GinModules(SwitchYardClientModule.class)
public interface SwitchYardGinjector extends Ginjector {

    /**
     * @return the PlaceManager configured for the module.
     */
    PlaceManager getPlaceManager();

    /**
     * @return the EventBus configured for the module.
     */
    EventBus getEventBus();

    /**
     * @return the ProxyFailureHandler configured for the module.
     */
    ProxyFailureHandler getProxyFailureHandler();

    /**
     * @return the Gatekeeper configured for the module.
     */
    Gatekeeper getLoggedInGatekeeper();

    /**
     * @return the BootstrapContext configured for the module.
     */
    BootstrapContext getBootstrapContext();

    /**
     * @return the ApplicationProperties configured for the module.
     */
    ApplicationProperties getAppProperties();

    /**
     * @return the Header configured for the module.
     */
    Header getHeader();

    /**
     * @return the Footer configured for the module.
     */
    Footer getFooter();

    /**
     * @return the MessageBar configured for the module.
     */
    MessageBar getMessageBar();

    /**
     * @return the MessageCenter configured for the module.
     */
    MessageCenter getMessageCenter();

    /**
     * @return the MessageCenterView configured for the module.
     */
    MessageCenterView getMessageCenterView();

    /**
     * @return the DispatchAsync configured for the module.
     */
    DispatchAsync getDispatchAsync();

    /**
     * @return the HandlerMapping configured for the module.
     */
    HandlerMapping getDispatcherHandlerRegistry();

    /**
     * @return the DMRHandler configured for the module.
     */
    DMRHandler getDMRHandler();

    /**
     * @return the InvocationMetrics configured for the module.
     */
    InvocationMetrics getInvocationMetrics();

    /**
     * @return the SignInPagePresenter configured for the module.
     */
    Provider<SignInPagePresenter> getSignInPagePresenter();

    /**
     * @return the MainLayoutPresenter configured for the module.
     */
    AsyncProvider<MainLayoutPresenter> getMainLayoutPresenter();

    /**
     * @return the MainPresenter configured for the module.
     */
    AsyncProvider<MainPresenter> getMainPresenter();

    /**
     * @return the ConfigPresenter configured for the module.
     */
    AsyncProvider<ConfigPresenter> getConfigPresenter();

    /**
     * @return the ComponentPresenter configured for the module.
     */
    AsyncProvider<ComponentPresenter> getComponentPresenter();

    /**
     * @return the ApplicationPresenter configured for the module.
     */
    AsyncProvider<ApplicationPresenter> getApplicationPresenter();

    /**
     * @return the ServicePresenter configured for the module.
     */
    AsyncProvider<ServicePresenter> getServicePresenter();

    /**
     * @return the SwitchYardStore configured for the module.
     */
    SwitchYardStore getSwitchYardDeploymentStore();
}
