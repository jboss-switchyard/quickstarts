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
package org.switchyard.component.clojure.deploy;

import javax.xml.namespace.QName;

import org.switchyard.component.clojure.config.model.ClojureComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.ServiceHandler;

/**
 * Activator for implemenations.clojure.
 * 
 * @author Daniel Bevenius
 *
 */
public class ClojureActivator extends BaseActivator {
    
    static final String[] TYPES = new String[] {"clojure"};
    
    /**
     * Sole constructor .
     */
    public ClojureActivator() {
        super(TYPES);
    }
    
    @Override
    public ServiceHandler activateService(QName name, ComponentModel config) {
        return new ClojureHandler((ClojureComponentImplementationModel)config.getImplementation());
    }

    @Override
    public void deactivateService(QName name, ServiceHandler handler) {
        // Nothing to do here
    }
}
