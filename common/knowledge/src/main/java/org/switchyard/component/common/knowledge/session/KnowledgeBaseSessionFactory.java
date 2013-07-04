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
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.persistence.jpa.KieStoreServices;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.switchyard.ServiceDomain;
import org.switchyard.SwitchYardException;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.util.Channels;
import org.switchyard.component.common.knowledge.util.Configurations;
import org.switchyard.component.common.knowledge.util.Environments;
import org.switchyard.component.common.knowledge.util.Listeners;
import org.switchyard.component.common.knowledge.util.Loggers;
import org.switchyard.component.common.knowledge.util.Resources;

/**
 * A Base-based KnowledgeSessionFactory.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@SuppressWarnings("deprecation")
class KnowledgeBaseSessionFactory extends KnowledgeSessionFactory {

    private static final String LINE_SEPARATOR;
    static {
        String lineSeparator;
        try {
            lineSeparator = System.getProperty("line.separator", "\n");
        } catch (Throwable t) {
            lineSeparator = "\n";
        }
        LINE_SEPARATOR = lineSeparator;
    }

    private final KieBase _base;
    private final KieSessionConfiguration _sessionConfiguration;

    KnowledgeBaseSessionFactory(KnowledgeComponentImplementationModel model, ClassLoader loader, ServiceDomain domain, Properties propertyOverrides) {
        super(model, loader, domain, propertyOverrides);
        _base = newBase();
        _sessionConfiguration = Configurations.getSessionConfiguration(getModel(), getLoader(), getPropertyOverrides());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeSession newStatelessSession() {
        StatelessKieSession stateless = _base.newStatelessKieSession(_sessionConfiguration);
        KnowledgeDisposal loggersDisposal = Loggers.registerLoggersForDisposal(getModel(), getLoader(), stateless);
        Listeners.registerListeners(getModel(), getLoader(), stateless);
        return new KnowledgeSession(stateless, loggersDisposal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeSession newStatefulSession(Map<String, Object> environmentOverrides) {
        Environment env = Environments.getEnvironment(environmentOverrides);
        KieSession stateful = _base.newKieSession(_sessionConfiguration, env);
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
        KieStoreServices kieStoreServices = KieServices.Factory.get().getStoreServices();
        Environment env = Environments.getEnvironment(environmentOverrides);
        KieSession stateful = null;
        if (sessionId != null) {
            stateful = kieStoreServices.loadKieSession(sessionId, _base, _sessionConfiguration, env);
        }
        if (stateful == null) {
            stateful = kieStoreServices.newKieSession(_base, _sessionConfiguration, env);
        }
        KnowledgeDisposal loggersDisposal = Loggers.registerLoggersForDisposal(getModel(), getLoader(), stateful);
        Listeners.registerListeners(getModel(), getLoader(), stateful);
        // channels are only meaningful for stateful sessions
        Channels.registerChannels(getModel(), getLoader(), stateful, getDomain());
        return new KnowledgeSession(stateful, true, loggersDisposal);
    }

    private KieBase newBase() {
        KieBaseConfiguration baseConfiguration = Configurations.getBaseConfiguration(getModel(), getLoader(), getPropertyOverrides());
        KnowledgeBase base = KnowledgeBaseFactory.newKnowledgeBase(baseConfiguration);
        KnowledgeBuilderConfiguration builderConfiguration = Configurations.getBuilderConfiguration(getModel(), getLoader(), getPropertyOverrides());
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder(base, builderConfiguration);
        Resources.addResources(getModel(), getLoader(), builder);
        try {
            return builder.newKnowledgeBase();
        } catch (Throwable t) {
            // NOTE: Logging can also be enabled on org.drools.builder.impl.KnowledgeBuilderImpl
            StringBuilder sb = new StringBuilder("Problem creating knowledge base");
            String tm = t.getMessage();
            if (tm != null) {
                sb.append(": ");
                sb.append(tm.trim());
            }
            KnowledgeBuilderErrors errors = builder.getErrors();
            for (KnowledgeBuilderError error : errors) {
                sb.append(LINE_SEPARATOR);
                sb.append(error.toString().trim());
            }
            throw new SwitchYardException(sb.toString(), t);
        }
    }

}
