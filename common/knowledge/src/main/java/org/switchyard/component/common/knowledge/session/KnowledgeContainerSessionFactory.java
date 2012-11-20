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

import org.kie.runtime.KieSession;
import org.kie.runtime.StatelessKieSession;
import org.switchyard.ServiceDomain;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.util.Channels;
import org.switchyard.component.common.knowledge.util.Containers;
import org.switchyard.component.common.knowledge.util.Listeners;
import org.switchyard.component.common.knowledge.util.Loggers;

/**
 * A Container-based KnowledgeSessionFactory.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
class KnowledgeContainerSessionFactory extends KnowledgeSessionFactory {

    KnowledgeContainerSessionFactory(KnowledgeComponentImplementationModel model, ClassLoader loader, ServiceDomain domain, Properties propertyOverrides) {
        super(model, loader, domain, propertyOverrides);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeSession newStatelessSession() {
        StatelessKieSession stateless = Containers.newStatelessSession(getModel(), getPropertyOverrides());
        KnowledgeDisposal loggersDisposal = Loggers.registerLoggersForDisposal(getModel(), getLoader(), stateless);
        Listeners.registerListeners(getModel(), getLoader(), stateless);
        return new KnowledgeSession(stateless, loggersDisposal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeSession newStatefulSession(Map<String, Object> environmentOverrides) {
        KieSession stateful = Containers.newStatefulSession(getModel(), getPropertyOverrides(), environmentOverrides);
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
        KieSession stateful = Containers.getPersistentSession(getModel(), getPropertyOverrides(), environmentOverrides, sessionId);
        KnowledgeDisposal loggersDisposal = Loggers.registerLoggersForDisposal(getModel(), getLoader(), stateful);
        Listeners.registerListeners(getModel(), getLoader(), stateful);
        // channels are only meaningful for stateful sessions
        Channels.registerChannels(getModel(), getLoader(), stateful, getDomain());
        return new KnowledgeSession(stateful, true, loggersDisposal);
    }

}
