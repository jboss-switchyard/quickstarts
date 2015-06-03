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
package org.switchyard.console.component.rules.client;

import org.switchyard.console.components.client.extension.BaseComponentProvider;
import org.switchyard.console.components.client.extension.ComponentExtension;
import org.switchyard.console.components.client.ui.BaseComponentConfigurationView;
import org.switchyard.console.components.client.ui.ComponentConfigurationPresenter.ComponentConfigurationView;

/**
 * RulesComponentProvider
 * 
 * ComponentProvider for the Rules component.
 * 
 * @author Rob Cernich
 */
@ComponentExtension(displayName = "Rules", componentName = "org.switchyard.component.rules", activationTypes = "rules")
public class RulesComponentProvider extends BaseComponentProvider {

    @Override
    public ComponentConfigurationView createConfigurationView() {
        return new BaseComponentConfigurationView() {
            @Override
            protected String getComponentName() {
                return "Rules";
            }
        };
    }

}
