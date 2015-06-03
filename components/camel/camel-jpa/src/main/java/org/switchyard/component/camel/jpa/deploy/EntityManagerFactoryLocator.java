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
package org.switchyard.component.camel.jpa.deploy;

import java.util.Map;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;

import org.jboss.logging.Logger;

/**
 * EntityManagerFactory locator for Camel JPA component.
 */
public final class EntityManagerFactoryLocator {

    private static Logger _logger = Logger.getLogger(EntityManagerFactoryLocator.class);

    /**
     * OSGi specific PersistenceProvider JNDI name.
     */
    public static final String OSGI_PERSISTENCE_PROVIDER_JNDI_NAME = "osgi:service/javax.persistence.spi.PersistenceProvider";

    /**
     * OSGi specific EntityManagerFactory JNDI name.
     */
    public static final String OSGI_ENTITY_MANAGER_FACTORY_JNDI_NAME = "osgi:service/javax.persistence.EntityManagerFactory";

    private EntityManagerFactoryLocator() {
    }

    /**
     * Lookup EntityManagerFactory from JNDI.
     * @param unitName JPA unit name.
     * @param props A Map of properties for use by the persistence provider.
     * @return EntityManagerFactory
     */
    public static EntityManagerFactory locateEntityManagerFactory(String unitName, Map<?,?> props) {
        InitialContext ic = null;
        try {
            ic = new InitialContext();
        } catch (Exception e) {
            _logger.debug(e);
            return null;
        }

        try {
            EntityManagerFactory emf = (EntityManagerFactory) lookupInJndi(ic, OSGI_ENTITY_MANAGER_FACTORY_JNDI_NAME);
            if (emf != null) {
                return emf;
            }

            PersistenceProvider pp = (PersistenceProvider) lookupInJndi(ic, OSGI_PERSISTENCE_PROVIDER_JNDI_NAME);
            if (pp != null) {
                return pp.createEntityManagerFactory(unitName, props);
            }
        } finally {
            try {
                ic.close();
            } catch (Exception e) {
                _logger.debug(e);
            }
        }
        return null;
    }

    private static Object lookupInJndi(InitialContext ic, String name) {
        try {
            return ic.lookup(name);
        } catch (Exception e) {
            _logger.debug(e);
            return null;
        }
    }
}
