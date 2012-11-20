/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.switchyard.component.common.knowledge.util;

import java.util.Map;
import java.util.Properties;

import org.drools.persistence.jpa.KnowledgeStoreServiceImpl;
import org.kie.KieBase;
import org.kie.KieServices;
import org.kie.builder.ReleaseId;
import org.kie.persistence.jpa.KieStoreServices;
import org.kie.runtime.Environment;
import org.kie.runtime.KieContainer;
import org.kie.runtime.KieSession;
import org.kie.runtime.KieSessionConfiguration;
import org.kie.runtime.StatelessKieSession;
import org.switchyard.component.common.knowledge.config.model.ContainerModel;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.ManifestModel;
import org.switchyard.exception.SwitchYardException;

/**
 * Container functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Containers {

    /**
     * Converts a colon (':') separated string into a ReleaseId.
     * @param releaseId the string
     * @return the ReleaseId
     */
    public static ReleaseId toReleaseId(String releaseId) {
        if (releaseId != null) {
            String[] gav = releaseId.split(":", 3);
            String groupId = gav.length > 0 ? gav[0] : null;
            String artifactId = gav.length > 1 ? gav[1] : null;
            String version = gav.length > 2 ? gav[2] : null;
            return KieServices.Factory.get().newReleaseId(groupId, artifactId, version);
        }
        return null;
    }

    /**
     * Creates a new stateless session.
     * @param model the model
     * @param propertiesOverrides any property overrides
     * @return the stateless session
     */
    public static StatelessKieSession newStatelessSession(KnowledgeComponentImplementationModel model, Properties propertiesOverrides) {
        if (model != null) {
            ManifestModel manifestModel = model.getManifest();
            if (manifestModel != null) {
                ContainerModel containerModel = manifestModel.getContainer();
                if (containerModel != null) {
                    KieContainer kieContainer = getContainer(containerModel);
                    String sessionName = containerModel.getSessionName();
                    if (sessionName != null) {
                        return kieContainer.newStatelessKieSession(sessionName);
                    }
                    String baseName = containerModel.getBaseName();
                    if (baseName != null) {
                        KieSessionConfiguration sessionConfiguration = Configurations.getSessionConfiguration(model, propertiesOverrides);
                        return kieContainer.getKieBase(baseName).newStatelessKieSession(sessionConfiguration);
                    }
                    return kieContainer.newStatelessKieSession();
                }
            }
        }
        return getContainer(null).newStatelessKieSession();
    }

    /**
     * Creates a new stateful session.
     * @param model the model
     * @param propertiesOverrides any property overrides
     * @param environmentOverrides any environment overrides
     * @return the stateful session
     */
    public static KieSession newStatefulSession(KnowledgeComponentImplementationModel model, Properties propertiesOverrides, Map<String, Object> environmentOverrides) {
        Environment environment = Environments.getEnvironment(environmentOverrides);
        if (model != null) {
            ManifestModel manifestModel = model.getManifest();
            if (manifestModel != null) {
                ContainerModel containerModel = manifestModel.getContainer();
                if (containerModel != null) {
                    KieContainer kieContainer = getContainer(containerModel);
                    String sessionName = containerModel.getSessionName();
                    if (sessionName != null) {
                        return kieContainer.newKieSession(sessionName, environment);
                    }
                    String baseName = containerModel.getBaseName();
                    if (baseName != null) {
                        KieSessionConfiguration sessionConfiguration = Configurations.getSessionConfiguration(model, propertiesOverrides);
                        return kieContainer.getKieBase(baseName).newKieSession(sessionConfiguration, environment);
                    }
                    return kieContainer.newKieSession(environment);
                }
            }
        }
        return getContainer(null).newKieSession(environment);
    }

    /**
     * Gets a persistent session.
     * @param model the model
     * @param propertiesOverrides any property overrides
     * @param environmentOverrides any environment overrides
     * @param sessionId the session id
     * @return the persistent session.
     */
    public static KieSession getPersistentSession(KnowledgeComponentImplementationModel model, Properties propertiesOverrides, Map<String, Object> environmentOverrides, Integer sessionId) {
        if (model != null) {
            ManifestModel manifestModel = model.getManifest();
            if (manifestModel != null) {
                ContainerModel containerModel = manifestModel.getContainer();
                if (containerModel != null) {
                    KieContainer kieContainer = getContainer(containerModel);
                    String baseName = containerModel.getBaseName();
                    if (baseName != null) {
                        // TODO: change back once KieServicesImpl.getStoreServices() stops failing trying to get an UNREGISTERED KieStoreServices.
                        //KieStoreServices kieStoreServices = KieServices.Factory.get().getStoreServices();
                        KieStoreServices kieStoreServices = new KnowledgeStoreServiceImpl();
                        KieBase base = kieContainer.getKieBase(baseName);
                        KieSessionConfiguration sessionConfiguration = Configurations.getSessionConfiguration(model, propertiesOverrides);
                        Environment environment = Environments.getEnvironment(environmentOverrides);
                        KieSession session = null;
                        if (sessionId != null) {
                            session = kieStoreServices.loadKieSession(sessionId, base, sessionConfiguration, environment);
                        }
                        if (session == null) {
                            session = kieStoreServices.newKieSession(base, sessionConfiguration, environment);
                        }
                        return session;
                    }
                }
            }
        }
        throw new SwitchYardException("manifest container baseName required in configuration for persistent sessions");
    }

    private static KieContainer getContainer(ContainerModel containerModel) {
        KieServices kieServices = KieServices.Factory.get();
        if (containerModel != null) {
            ReleaseId rid = containerModel.getReleaseId();
            if (rid != null) {
                return kieServices.newKieContainer(rid);
            }
        }
        return kieServices.getKieClasspathContainer();
    }

    private Containers() {}

}
