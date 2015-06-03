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
package org.switchyard.component.common.knowledge.config.builder;

import javax.persistence.EntityManagerFactory;

import org.switchyard.ServiceDomain;
import org.switchyard.component.common.knowledge.persistence.EntityManagerFactoryLoader;
import org.switchyard.component.common.knowledge.persistence.NoopEntityManagerFactory;
import org.switchyard.component.common.knowledge.persistence.OSGiEntityManagerFactoryLoader;

/**
 * EntityManagerFactoryBuilder.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class EntityManagerFactoryBuilder extends KnowledgeBuilder {

    private static final EntityManagerFactoryLoader EMF_LOADER;
    static {
        // hacky way to integrate with OSGi.
        EntityManagerFactoryLoader loader = new EntityManagerFactoryLoader();
        try {
            loader = new OSGiEntityManagerFactoryLoader();
        } catch (Exception e) {
            // above fails if OSGi types are not available.
            e.fillInStackTrace();
        }
        EMF_LOADER = loader;
    }

    private final boolean _persistent;

    /**
     * Creates a new EntityManagerFactoryBuilder.
     * @param serviceDomain serviceDomain
     * @param persistent persistent
     */
    public EntityManagerFactoryBuilder(ServiceDomain serviceDomain, boolean persistent) {
        super(null, serviceDomain);
        _persistent = persistent;
    }

    /**
     * Builds an EntityManagerFactory.
     * @return an EntityManagerFactory
     */
    public EntityManagerFactory build() {
        if (_persistent) {
            return EMF_LOADER.getEntityManagerFactory(getServiceDomain());
        } else {
            return new NoopEntityManagerFactory();
        }
    }

}
