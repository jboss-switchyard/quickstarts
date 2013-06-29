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
package org.switchyard.as7.extension.services;

import java.util.ArrayList;
import java.util.List;

import org.jboss.as.naming.context.NamespaceContextSelector;
import org.jboss.logging.Logger;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;
import org.switchyard.as7.extension.deployment.SwitchYardDeployment;
import org.switchyard.deploy.Component;

/**
 * The SwitchYard service associated with deployments.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SwitchYardService implements Service<SwitchYardDeployment> {

    private static final Logger LOG = Logger.getLogger("org.switchyard");

    /**
     * Represents a SwitchYard service name.
     */
    public static final ServiceName SERVICE_NAME = ServiceName.of("SwitchYardService");

    private final InjectedValue<NamespaceContextSelector> _namespaceSelector = new InjectedValue<NamespaceContextSelector>();

    private final List<InjectedValue<Component>> _components = new ArrayList<InjectedValue<Component>>();
    private SwitchYardDeployment _switchyardDeployment;

    /**
     * Constructs a SwitchYard service.
     * 
     * @param switchyardDeployment the deployment instance
     */
    public SwitchYardService(SwitchYardDeployment switchyardDeployment) {
        _switchyardDeployment = switchyardDeployment;
    }

    @Override
    public SwitchYardDeployment getValue() throws IllegalStateException, IllegalArgumentException {
        return _switchyardDeployment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start(StartContext context) throws StartException {
        try {
            NamespaceContextSelector.pushCurrentSelector(_namespaceSelector.getValue());
            LOG.info("Starting SwitchYard service");
            List<Component> components = new ArrayList<Component>();
            for (InjectedValue<Component> component : _components) {
                components.add(component.getValue());
            }
            _switchyardDeployment.start(components);
        } catch (Exception e) {
            try {
                _switchyardDeployment.stop();
            } catch (Exception ex) {
                LOG.error(ex);
            }
            throw new StartException(e);
        } finally {
            NamespaceContextSelector.popCurrentSelector();
        }
    }

    @Override
    public void stop(StopContext context) {
        _switchyardDeployment.stop();
    }

    /**
     * Injection point for NamespaceContextSelector.
     * 
     * @return the NamespaceContextSelector
     */
    public InjectedValue<NamespaceContextSelector> getNamespaceSelector() {
        return _namespaceSelector;
    }

    /**
     * Injection point for Component.
     * 
     * @return the component added
     */
    public InjectedValue<Component> getComponent() {
        InjectedValue<Component> component = new InjectedValue<Component>();
        if (!_components.contains(component)) {
            _components.add(component);
        }
        return component;
    }
}
