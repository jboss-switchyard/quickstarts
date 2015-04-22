/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.camel.component.quartz.QuartzComponent;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.common.type.Classes;
import org.switchyard.component.camel.common.deploy.BaseBindingActivator;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.deploy.ServiceHandler;

/**
 * Camel quartz activator.
 */
public class CamelQuartzActivator extends BaseBindingActivator {

    /**
     * Creates new activator instance.
     * 
     * @param context Camel context.
     * @param types Activation types.
     */
    public CamelQuartzActivator(SwitchYardCamelContext context, String[] types) {
        super(context, types);
    }

    @Override
    public ServiceHandler activateBinding(QName serviceName, BindingModel config) {
        // SWITCHYARD-1970 - custom quartz.properties support
        probeQuartzProperties();

        return super.activateBinding(serviceName, config);
    }

    private void probeQuartzProperties() {
        try {
            URL props = Classes.getResource("org/quartz/quartz.properties");
            if (props == null) {
                return;
            }
        } catch (IOException e) {
            return;
        }

        Set<QuartzComponent> components = getCamelContext().getRegistry().findByType(QuartzComponent.class);
        if (components.isEmpty()) {
            QuartzComponent quartz = getCamelContext().getInjector().newInstance(QuartzComponent.class);
            getCamelContext().getWritebleRegistry().put("quartz", quartz);
            components.add(quartz);
        }
        for (QuartzComponent quartz : components) {
            if (quartz.getPropertiesFile() == null) {
                quartz.setPropertiesFile("org/quartz/quartz.properties");
            }
        }
    }

}
