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
package org.switchyard.component.common.knowledge.session;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.services.client.api.RemoteJmsRuntimeEngineFactory;
import org.kie.services.client.api.RemoteRestRuntimeEngineFactory;
import org.kie.services.client.api.RemoteRuntimeEngineFactory;
import org.kie.services.client.api.builder.RemoteJmsRuntimeEngineFactoryBuilder;
import org.kie.services.client.api.builder.RemoteRestRuntimeEngineFactoryBuilder;
import org.kie.services.client.api.builder.RemoteRuntimeEngineFactoryBuilder;
import org.kie.services.client.api.command.RemoteRuntimeEngine;
import org.kie.services.shared.AcceptedCommands;
import org.switchyard.ServiceDomain;
import org.switchyard.common.type.reflect.Access;
import org.switchyard.common.type.reflect.FieldAccess;
import org.switchyard.component.common.knowledge.config.model.ExtraJaxbClassModel;
import org.switchyard.component.common.knowledge.config.model.ExtraJaxbClassesModel;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.RemoteJmsModel;
import org.switchyard.component.common.knowledge.config.model.RemoteModel;
import org.switchyard.component.common.knowledge.config.model.RemoteRestModel;
import org.switchyard.component.common.knowledge.util.Loggers;

/**
 * A Remote-based KnowledgeSessionFactory.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
@SuppressWarnings("rawtypes")
public class KnowledgeRemoteSessionFactory extends KnowledgeSessionFactory {

    private static Set<Class<? extends Command>> acceptedCommands = new HashSet<Class<? extends Command>>();

    static {
        acceptedCommands.addAll(AcceptedCommands.getSet());
        acceptedCommands.add(BatchExecutionCommandImpl.class);
        acceptedCommands = Collections.unmodifiableSet(acceptedCommands);
        Access<Set<Class<? extends Command>>> access = new FieldAccess<Set<Class<? extends Command>>>(AcceptedCommands.class, "acceptedCommands");
        access.write(null, acceptedCommands);
    }

    private final RemoteRuntimeEngineFactory _remoteRuntimeEngineFactory;

    KnowledgeRemoteSessionFactory(KnowledgeComponentImplementationModel model, ClassLoader loader, ServiceDomain domain, Properties propertyOverrides) {
        super(model, loader, domain, propertyOverrides);
        _remoteRuntimeEngineFactory = buildRemoteRuntimeEngineFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeSession newStatelessSession() {
        RemoteRuntimeEngine remoteRuntimeEngine = _remoteRuntimeEngineFactory.newRuntimeEngine();
        StatelessKieSession stateless = new KnowledgeRemoteStatelessKieSession(remoteRuntimeEngine);
        KnowledgeDisposal loggersDisposal = Loggers.registerLoggersForDisposal(getModel(), getLoader(), stateless);
        return new KnowledgeSession(stateless, true, loggersDisposal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeSession newStatefulSession(Map<String, Object> environmentOverrides) {
        RemoteRuntimeEngine remoteRuntimeEngine = _remoteRuntimeEngineFactory.newRuntimeEngine();
        KieSession stateful = remoteRuntimeEngine.getKieSession();
        KnowledgeDisposal loggersDisposal = Loggers.registerLoggersForDisposal(getModel(), getLoader(), stateful);
        return new KnowledgeSession(stateful, true, false, loggersDisposal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeSession getPersistentSession(Map<String, Object> environmentOverrides, Integer sessionId) {
        RemoteRuntimeEngine remoteRuntimeEngine = _remoteRuntimeEngineFactory.newRuntimeEngine();
        KieSession stateful = remoteRuntimeEngine.getKieSession();
        KnowledgeDisposal loggersDisposal = Loggers.registerLoggersForDisposal(getModel(), getLoader(), stateful);
        return new KnowledgeSession(stateful, true, true, loggersDisposal);
    }

    private RemoteRuntimeEngineFactory buildRemoteRuntimeEngineFactory() {
        RemoteRuntimeEngineFactory factory = null;
        ClassLoader loader = getLoader();
        RemoteModel remoteModel = getModel().getManifest().getRemote();
        if (remoteModel instanceof RemoteJmsModel) {
            RemoteJmsRuntimeEngineFactoryBuilder builder = RemoteJmsRuntimeEngineFactory.newBuilder();
            InitialContext ctx = configRemoteJms(builder, (RemoteJmsModel)remoteModel, loader);
            try {
                factory = builder.build();
            } finally {
                if (ctx != null) {
                    try {
                        ctx.close();
                    } catch (Exception e) {
                        e.getMessage(); // ignore but keep checkstyle happy
                    }
                }
            }
        } else if (remoteModel instanceof RemoteRestModel) {
            RemoteRestRuntimeEngineFactoryBuilder builder = RemoteRestRuntimeEngineFactory.newBuilder();
            configRemoteRest(builder, (RemoteRestModel)remoteModel, loader);
            factory = builder.build();
        }
        return factory;
    }

    private InitialContext configRemoteJms(RemoteJmsRuntimeEngineFactoryBuilder builder, RemoteJmsModel model, ClassLoader loader) {
        configRemote(builder, model, loader);
        InitialContext ctx = null;
        String hostName = model.getHostName();
        if (hostName != null) {
            builder.addHostName(hostName);
            try {
                Properties props = new Properties();
                props.setProperty(InitialContext.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
                Integer remotingPort = model.getRemotingPort();
                if (remotingPort == null) {
                    // default EAP remoting port
                    remotingPort = Integer.valueOf(4447);
                }
                props.setProperty(InitialContext.PROVIDER_URL, "remote://"+ hostName + ":" + remotingPort);
                String userName = model.getUserName();
                if (userName != null) {
                    props.setProperty(InitialContext.SECURITY_PRINCIPAL, userName);
                }
                String password = model.getPassword();
                if (password != null) {
                    props.setProperty(InitialContext.SECURITY_CREDENTIALS, password);
                }
                ctx = new InitialContext(props);
                builder.addRemoteInitialContext(ctx);
            } catch (NamingException ne) {
                throw new RuntimeException(ne);
            }
        } else {
            try {
                ctx = new InitialContext();
                builder.addRemoteInitialContext(ctx);
            } catch (NamingException ne) {
                throw new RuntimeException(ne);
            }
        }
        Integer messagingPort = model.getMessagingPort();
        if (messagingPort == null) {
            // default EAP messaging port
            messagingPort = Integer.valueOf(5455);
        }
        builder.addJmsConnectorPort(messagingPort);
        builder.useSsl(model.isUseSsl());
        String keystorePassword = model.getKeystorePassword();
        if (keystorePassword != null) {
            builder.addKeystorePassword(keystorePassword);
            // same by default; can be overridden below
            builder.addTruststorePassword(keystorePassword);
        }
        String keystoreLocation = model.getKeystoreLocation();
        if (keystoreLocation != null) {
            builder.addKeystoreLocation(keystoreLocation);
            // same by default; can be overridden below
            builder.addTruststoreLocation(keystoreLocation);
        }
        String truststorePassword = model.getTruststorePassword();
        if (truststorePassword != null) {
            builder.addTruststorePassword(truststorePassword);
        }
        String truststoreLocation = model.getTruststoreLocation();
        if (truststoreLocation != null) {
            builder.addTruststoreLocation(truststoreLocation);
        }
        return ctx;
    }

    private void configRemoteRest(RemoteRestRuntimeEngineFactoryBuilder builder, RemoteRestModel model, ClassLoader loader) {
        configRemote(builder, model, loader);
        try {
            builder.addUrl(new URL(model.getUrl()));
        } catch (MalformedURLException mue) {
            throw new RuntimeException(mue);
        }
        builder.useFormBasedAuth(model.isUseFormBasedAuth());
    }

    private void configRemote(RemoteRuntimeEngineFactoryBuilder<?, ?> builder, RemoteModel model, ClassLoader loader) {
        builder.addDeploymentId(model.getDeploymentId());
        String userName = model.getUserName();
        if (userName != null) {
            builder.addUserName(userName);
        }
        String password = model.getPassword();
        if (password != null) {
            builder.addPassword(password);
        }
        Integer timeout = model.getTimeout();
        if (timeout != null) {
            builder.addTimeout(timeout.intValue());
        }
        ExtraJaxbClassesModel extraJaxbClasses = model.getExtraJaxbClasses();
        if (extraJaxbClasses != null) {
            Set<Class<?>> clazzes = new LinkedHashSet<Class<?>>();
            for (ExtraJaxbClassModel extraJaxbClass : extraJaxbClasses.getExtraJaxbClasses()) {
                Class<?> clazz = extraJaxbClass.getClazz(loader);
                if (clazz != null) {
                    clazzes.add(clazz);
                }
            }
            int cs = clazzes.size();
            if (cs > 0) {
                builder.addExtraJaxbClasses(clazzes.toArray(new Class<?>[cs]));
            }
        }
        builder.addExtraJaxbClasses(acceptedCommands.toArray(new Class<?>[acceptedCommands.size()]));
    }

}
