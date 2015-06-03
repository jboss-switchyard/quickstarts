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
package org.switchyard.component.camel.quartz.deploy;

import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.camel.common.deploy.BaseBindingActivator;
import org.switchyard.component.camel.common.deploy.BaseBindingComponent;
import org.switchyard.component.camel.quartz.model.v2.V2CamelQuartzBindingModel;

/**
 * Quartz binding component.
 */
public class CamelQuartzComponent extends BaseBindingComponent {

    /**
     * Creates new component.
     */
    public CamelQuartzComponent() {
        super("CamelQuartzComponent", V2CamelQuartzBindingModel.QUARTZ);
    }

    @Override
    protected BaseBindingActivator createActivator(SwitchYardCamelContext context, String... types) {
        return new CamelQuartzActivator(context, types);
    }

}
