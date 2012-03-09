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

import org.switchyard.console.client.model.SwitchYardStore;
import org.switchyard.console.client.model.SwitchYardStoreImpl;
import org.switchyard.console.client.ui.application.ApplicationPresenter;
import org.switchyard.console.client.ui.application.ApplicationView;
import org.switchyard.console.client.ui.component.ComponentConfigurationPresenterFactory;
import org.switchyard.console.client.ui.component.ComponentConfigurationViewFactory;
import org.switchyard.console.client.ui.component.ComponentPresenter;
import org.switchyard.console.client.ui.config.ConfigPresenter;
import org.switchyard.console.client.ui.config.ConfigView;
import org.switchyard.console.client.ui.runtime.RuntimePresenter;
import org.switchyard.console.client.ui.runtime.RuntimeView;
import org.switchyard.console.client.ui.service.ServicePresenter;
import org.switchyard.console.client.ui.service.ServiceView;
import org.switchyard.console.components.client.extension.ComponentProviders;
import org.switchyard.console.components.client.extension.DefaultComponentProvider;
import org.switchyard.console.components.client.extension.DefaultComponentProviderImpl;

import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

/**
 * SwitchYardClientModule
 * 
 * SwitchYard Ginjector bindings.
 * 
 * @author Rob Cernich
 */
public class SwitchYardClientModule extends AbstractPresenterModule {

    protected void configure() {
        // switchyard application
        bind(SwitchYardStore.class).to(SwitchYardStoreImpl.class).in(Singleton.class);
        bindPresenter(ConfigPresenter.class, ConfigPresenter.MyView.class, ConfigView.class,
                ConfigPresenter.MyProxy.class);
        bindPresenter(ApplicationPresenter.class, ApplicationPresenter.MyView.class, ApplicationView.class,
                ApplicationPresenter.MyProxy.class);
        bindPresenter(ServicePresenter.class, ServicePresenter.MyView.class, ServiceView.class,
                ServicePresenter.MyProxy.class);
        bindPresenter(RuntimePresenter.class, RuntimePresenter.MyView.class, RuntimeView.class,
                RuntimePresenter.MyProxy.class);

        // components
        bindPresenterWidgetFactory(ComponentPresenter.PresenterFactory.class,
                ComponentConfigurationPresenterFactory.class, ComponentPresenter.ViewFactory.class,
                ComponentConfigurationViewFactory.class);

        bind(ComponentProviders.class).in(Singleton.class);
        bind(DefaultComponentProvider.class).to(DefaultComponentProviderImpl.class);
    }

}
