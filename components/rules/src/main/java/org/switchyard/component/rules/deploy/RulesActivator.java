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
package org.switchyard.component.rules.deploy;

import javax.xml.namespace.QName;

import org.switchyard.component.rules.config.model.RulesComponentImplementationModel;
import org.switchyard.component.rules.exchange.RulesExchangeHandler;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.ServiceHandler;

/**
 * Activator for the Rules component.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class RulesActivator extends BaseActivator {

    /**
     * Rules component activator type name.
     */
    public static final String RULES_TYPE = "rules";

    /**
     * Constructs a new Activator of type "rules".
     */
    public RulesActivator() {
        super(RULES_TYPE);
    }
   
    /**
    * {@inheritDoc}
    */
    @Override
    public ServiceHandler activateService(QName name, ComponentModel config) {
        return new RulesExchangeHandler((RulesComponentImplementationModel)config.getImplementation(), getServiceDomain(), name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivateService(QName name, ServiceHandler handler) {
        // no-op
    }

}
