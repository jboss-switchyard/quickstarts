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
package org.switchyard.console.components.client.extension;

import org.switchyard.console.components.client.ui.ComponentConfigurationPresenter;
import org.switchyard.console.components.client.ui.ComponentConfigurationPresenter.ComponentConfigurationView;

import com.google.web.bindery.event.shared.EventBus;

/**
 * ComponentProvider
 * 
 * Interface to be implemented by ComponentProvider extensions. Extenders should
 * consider using {@link BaseComponentProvider}.
 * 
 * @author Rob Cernich
 */
public interface ComponentProvider {

    /**
     * Creates a new presenter for handling {@link Component} system
     * configuration.
     * 
     * @param eventBus the EventBus.
     * @param view the corresponding view.
     * 
     * @return a new presenter.
     */
    public ComponentConfigurationPresenter createConfigurationPresenter(EventBus eventBus,
            ComponentConfigurationView view);

    /**
     * Creates a new view for handling {@link Component} system configuration.
     * 
     * @return a new view.
     */
    public ComponentConfigurationView createConfigurationView();

}
