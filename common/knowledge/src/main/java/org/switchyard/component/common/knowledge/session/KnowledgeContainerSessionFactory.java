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
package org.switchyard.component.common.knowledge.session;

import java.util.Map;
import java.util.Properties;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.persistence.jpa.KieStoreServices;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;
import org.switchyard.ServiceDomain;
import org.switchyard.SwitchYardException;
import org.switchyard.component.common.knowledge.config.model.ContainerModel;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.util.Channels;
import org.switchyard.component.common.knowledge.util.Configurations;
import org.switchyard.component.common.knowledge.util.Containers;
import org.switchyard.component.common.knowledge.util.Disposals;
import org.switchyard.component.common.knowledge.util.Environments;
import org.switchyard.component.common.knowledge.util.Listeners;
import org.switchyard.component.common.knowledge.util.Loggers;

/**
 * A Container-based KnowledgeSessionFactory.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
class KnowledgeContainerSessionFactory extends KnowledgeSessionFactory {

    private final ContainerModel _containerModel;
    private final KieContainer _kieContainer;

    KnowledgeContainerSessionFactory(KnowledgeComponentImplementationModel model, ClassLoader loader, ServiceDomain domain, Properties propertyOverrides) {
        super(model, loader, domain, propertyOverrides);
        _containerModel = Containers.getContainerModel(model);
        _kieContainer = Containers.getContainer(_containerModel);
        registerScannerForDisposal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeSession newStatelessSession() {
        StatelessKieSession stateless = newStatelessKieSession();
        KnowledgeDisposal loggersDisposal = Loggers.registerLoggersForDisposal(getModel(), getLoader(), stateless);
        Listeners.registerListeners(getModel(), getLoader(), stateless);
        return new KnowledgeSession(stateless, loggersDisposal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeSession newStatefulSession(Map<String, Object> environmentOverrides) {
        KieSession stateful = newKieSession(environmentOverrides);
        KnowledgeDisposal loggersDisposal = Loggers.registerLoggersForDisposal(getModel(), getLoader(), stateful);
        Listeners.registerListeners(getModel(), getLoader(), stateful);
        // channels are only meaningful for stateful sessions
        Channels.registerChannels(getModel(), getLoader(), stateful, getDomain());
        return new KnowledgeSession(stateful, false, loggersDisposal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeSession getPersistentSession(Map<String, Object> environmentOverrides, Integer sessionId) {
        KieSession stateful = getPersistentKieSession(environmentOverrides, sessionId);
        KnowledgeDisposal loggersDisposal = Loggers.registerLoggersForDisposal(getModel(), getLoader(), stateful);
        Listeners.registerListeners(getModel(), getLoader(), stateful);
        // channels are only meaningful for stateful sessions
        Channels.registerChannels(getModel(), getLoader(), stateful, getDomain());
        return new KnowledgeSession(stateful, true, loggersDisposal);
    }

    private StatelessKieSession newStatelessKieSession() {
        if (_containerModel != null) {
            String sessionName = _containerModel.getSessionName();
            if (sessionName != null) {
                return _kieContainer.newStatelessKieSession(sessionName);
            }
            String baseName = _containerModel.getBaseName();
            if (baseName != null) {
                KieSessionConfiguration sessionConfiguration = Configurations.getSessionConfiguration(getModel(), getLoader(), getPropertyOverrides());
                return _kieContainer.getKieBase(baseName).newStatelessKieSession(sessionConfiguration);
            }
        }
        return _kieContainer.newStatelessKieSession();
    }

    private KieSession newKieSession(Map<String, Object> environmentOverrides) {
        Environment environment = Environments.getEnvironment(environmentOverrides);
        if (_containerModel != null) {
            String sessionName = _containerModel.getSessionName();
            if (sessionName != null) {
                return _kieContainer.newKieSession(sessionName, environment);
            }
            String baseName = _containerModel.getBaseName();
            if (baseName != null) {
                KieSessionConfiguration sessionConfiguration = Configurations.getSessionConfiguration(getModel(), getLoader(), getPropertyOverrides());
                return _kieContainer.getKieBase(baseName).newKieSession(sessionConfiguration, environment);
            }
        }
        return _kieContainer.newKieSession(environment);
    }

    private KieSession getPersistentKieSession(Map<String, Object> environmentOverrides, Integer sessionId) {
        if (_containerModel != null) {
            String baseName = _containerModel.getBaseName();
            if (baseName != null) {
                KieStoreServices kieStoreServices = KieServices.Factory.get().getStoreServices();
                KieBase base = _kieContainer.getKieBase(baseName);
                KieSessionConfiguration sessionConfiguration = Configurations.getSessionConfiguration(getModel(), getLoader(), getPropertyOverrides());
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
        throw new SwitchYardException("manifest container baseName required in configuration for persistent sessions");
    }

    private void registerScannerForDisposal() {
        if (_containerModel != null && _containerModel.isScan()) {
            Long scanInterval = _containerModel.getScanInterval();
            if (scanInterval == null) {
                scanInterval = Long.valueOf(60000);
            }
            long si = scanInterval.longValue();
            if (si < 1) {
                throw new IllegalArgumentException("container scanInterval must be positive");
            }
            KieScanner scanner = KieServices.Factory.get().newKieScanner(_kieContainer);
            addDisposals(Disposals.newDisposal(scanner));
            scanner.start(si);
        }
    }

}
