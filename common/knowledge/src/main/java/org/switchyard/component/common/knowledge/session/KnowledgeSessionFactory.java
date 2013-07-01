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
package org.switchyard.component.common.knowledge.session;

import java.util.Map;
import java.util.Properties;

import org.switchyard.ServiceDomain;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.ManifestModel;
import org.switchyard.config.model.resource.ResourcesModel;

/**
 * The main KnowledgeSession factory.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class KnowledgeSessionFactory extends KnowledgeDisposer {

    private final KnowledgeComponentImplementationModel _model;
    private final ClassLoader _loader;
    private final ServiceDomain _domain;
    private final Properties _propertyOverrides;

    /**
     * Constructs a new knowledge session factory.
     * @param model the model
     * @param loader the loader
     * @param domain the domain
     * @param propertyOverrides any property overrides
     */
    protected KnowledgeSessionFactory(KnowledgeComponentImplementationModel model, ClassLoader loader, ServiceDomain domain, Properties propertyOverrides) {
        _model = model;
        _loader = loader;
        _domain = domain;
        _propertyOverrides = propertyOverrides;
    }

    /**
     * Gets the model.
     * @return the model
     */
    public KnowledgeComponentImplementationModel getModel() {
        return _model;
    }

    /**
     * Gets the class loader.
     * @return the class loader
     */
    public ClassLoader getLoader() {
        return _loader;
    }

    /**
     * Gets the service domain.
     * @return the service domain
     */
    public ServiceDomain getDomain() {
        return _domain;
    }

    /**
     * Gets the property overrides.
     * @return the property overrides
     */
    public Properties getPropertyOverrides() {
        return _propertyOverrides;
    }

    /**
     * Creates a new stateless session.
     * @return the session
     */
    public abstract KnowledgeSession newStatelessSession();

    /**
     * Creates a new stateful session.
     * @param environmentOverrides any environment overrides
     * @return the session
     */
    public abstract KnowledgeSession newStatefulSession(Map<String, Object> environmentOverrides);

    /**
     * Gets a persistent session.
     * @param environmentOverrides any environment overrides
     * @param sessionId the session id
     * @return the session
     */
    public abstract KnowledgeSession getPersistentSession(Map<String, Object> environmentOverrides, Integer sessionId);

    /**
     * Creates a new session factory.
     * @param model the model
     * @param loader the class loader
     * @param domain the service domain
     * @param propertyOverrides any property overrides
     * @return the session factory
     */
    public static KnowledgeSessionFactory newSessionFactory(KnowledgeComponentImplementationModel model, ClassLoader loader, ServiceDomain domain, Properties propertyOverrides) {
        ManifestModel manifestModel = model.getManifest();
        if (manifestModel != null) {
            if (manifestModel.getContainer() == null) {
                ResourcesModel resourcesModel = manifestModel.getResources();
                if (resourcesModel != null) {
                    return new KnowledgeBaseSessionFactory(model, loader, domain, propertyOverrides);
                }
            }
        }
        return new KnowledgeContainerSessionFactory(model, loader, domain, propertyOverrides);
    }

}
