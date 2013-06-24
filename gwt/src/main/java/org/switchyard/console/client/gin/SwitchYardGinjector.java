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

import org.jboss.as.console.spi.GinExtension;
import org.switchyard.console.client.ui.application.ApplicationPresenter;
import org.switchyard.console.client.ui.artifacts.ArtifactPresenter;
import org.switchyard.console.client.ui.config.ConfigPresenter;
import org.switchyard.console.client.ui.metrics.MetricsPresenter;
import org.switchyard.console.client.ui.reference.ReferencePresenter;
import org.switchyard.console.client.ui.runtime.RuntimePresenter;
import org.switchyard.console.client.ui.service.ServicePresenter;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * Overall module configuration.
 * 
 * @see SwitchYardClientModule
 * 
 * @author Rob Cernich
 */
@GinModules(SwitchYardClientModule.class)
@GinExtension("org.switchyard.console.SwitchYard")
public interface SwitchYardGinjector extends Ginjector {

    /**
     * @return the ConfigPresenter configured for the module.
     */
    AsyncProvider<ConfigPresenter> getConfigPresenter();

    /**
     * @return the ApplicationPresenter configured for the module.
     */
    AsyncProvider<ApplicationPresenter> getApplicationPresenter();

    /**
     * @return the ArtifactPresenter configured for the module.
     */
    AsyncProvider<ArtifactPresenter> getArtifactPresenter();

    /**
     * @return the ServicePresenter configured for the module.
     */
    AsyncProvider<ServicePresenter> getServicePresenter();

    /**
     * @return the ReferencePresenter configured for the module.
     */
    AsyncProvider<ReferencePresenter> getReferencePresenter();

    /**
     * @return the MetricsPresenter configured for the module.
     */
    AsyncProvider<MetricsPresenter> getSwitchYardMetricsPresenter();

    /**
     * @return the RuntimePresenter configured for the module.
     */
    AsyncProvider<RuntimePresenter> getSwitchYardRuntimePresenter();

}
