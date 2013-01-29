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

import org.jboss.as.console.spi.GinExtension;
import org.switchyard.console.client.ui.application.ApplicationPresenter;
import org.switchyard.console.client.ui.artifacts.ArtifactPresenter;
import org.switchyard.console.client.ui.config.ConfigPresenter;
import org.switchyard.console.client.ui.metrics.MetricsPresenter;
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
     * @return the MetricsPresenter configured for the module.
     */
    AsyncProvider<MetricsPresenter> getSwitchYardMetricsPresenter();

    /**
     * @return the RuntimePresenter configured for the module.
     */
    AsyncProvider<RuntimePresenter> getSwitchYardRuntimePresenter();

}
