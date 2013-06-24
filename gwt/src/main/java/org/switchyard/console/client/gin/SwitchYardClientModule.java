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

package org.switchyard.console.client.gin;

import org.switchyard.console.client.model.SwitchYardStore;
import org.switchyard.console.client.model.SwitchYardStoreImpl;
import org.switchyard.console.client.ui.application.ApplicationPresenter;
import org.switchyard.console.client.ui.application.ApplicationView;
import org.switchyard.console.client.ui.artifacts.ArtifactPresenter;
import org.switchyard.console.client.ui.artifacts.ArtifactReferencesView;
import org.switchyard.console.client.ui.component.ComponentConfigurationPresenterFactory;
import org.switchyard.console.client.ui.component.ComponentConfigurationViewFactory;
import org.switchyard.console.client.ui.component.ComponentPresenter;
import org.switchyard.console.client.ui.config.ConfigPresenter;
import org.switchyard.console.client.ui.config.ConfigView;
import org.switchyard.console.client.ui.metrics.MetricsPresenter;
import org.switchyard.console.client.ui.metrics.MetricsView;
import org.switchyard.console.client.ui.reference.ReferencePresenter;
import org.switchyard.console.client.ui.reference.ReferenceView;
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
        bindPresenter(ArtifactPresenter.class, ArtifactPresenter.MyView.class, ArtifactReferencesView.class,
                ArtifactPresenter.MyProxy.class);
        bindPresenter(ServicePresenter.class, ServicePresenter.MyView.class, ServiceView.class,
                ServicePresenter.MyProxy.class);
        bindPresenter(ReferencePresenter.class, ReferencePresenter.MyView.class, ReferenceView.class,
                ReferencePresenter.MyProxy.class);
        bindPresenter(MetricsPresenter.class, MetricsPresenter.MyView.class, MetricsView.class,
                MetricsPresenter.MyProxy.class);
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
