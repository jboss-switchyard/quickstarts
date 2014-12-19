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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.remote.client.api.RemoteJmsRuntimeEngineBuilder;
import org.kie.remote.client.api.RemoteRestRuntimeEngineBuilder;
import org.kie.remote.client.api.RemoteRuntimeEngineBuilder;
import org.kie.remote.client.jaxb.AcceptedClientCommands;
import org.kie.services.client.api.RemoteRuntimeEngineFactory;
import org.kie.services.client.api.command.RemoteConfiguration;
import org.kie.services.client.api.command.RemoteRuntimeEngine;
import org.switchyard.common.type.reflect.Access;
import org.switchyard.common.type.reflect.FieldAccess;
import org.switchyard.component.common.knowledge.config.model.ExtraJaxbClassModel;
import org.switchyard.component.common.knowledge.config.model.ExtraJaxbClassesModel;
import org.switchyard.component.common.knowledge.config.model.RemoteJmsModel;
import org.switchyard.component.common.knowledge.config.model.RemoteModel;
import org.switchyard.component.common.knowledge.config.model.RemoteRestModel;

/**
 * RemoteConfigurationBuilder.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class RemoteConfigurationBuilder extends KnowledgeBuilder {

    private static Set<Class<?>> acceptedCommands = new HashSet<Class<?>>();
    static {
        Access<Set<Class<?>>> access = new FieldAccess<Set<Class<?>>>(AcceptedClientCommands.class, "acceptedCommands");
        acceptedCommands.addAll(access.read(null));
        acceptedCommands.add(BatchExecutionCommandImpl.class);
        acceptedCommands = Collections.unmodifiableSet(acceptedCommands);
        access.write(null, acceptedCommands);
    }

    private final RemoteConfiguration _remoteConfiguration;

    /**
     * Creates a new RemoteConfigurationBuilder.
     * @param classLoader classLoader
     * @param remoteModel remoteModel
     */
    public RemoteConfigurationBuilder(ClassLoader classLoader, RemoteModel remoteModel) {
        super(classLoader);
        _remoteConfiguration = buildRemoteConfiguration(remoteModel);
    }

    private RemoteConfiguration buildRemoteConfiguration(RemoteModel remoteModel) {
        RuntimeEngine engine = null;
        if (remoteModel instanceof RemoteJmsModel) {
            RemoteJmsRuntimeEngineBuilder builder = RemoteRuntimeEngineFactory.newJmsBuilder();
            InitialContext ctx = configRemoteJms(builder, (RemoteJmsModel)remoteModel);
            try {
                engine = builder.build();
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
            RemoteRestRuntimeEngineBuilder builder = RemoteRuntimeEngineFactory.newRestBuilder();
            configRemoteRest(builder, (RemoteRestModel)remoteModel);
            engine = builder.build();
        }
        RemoteConfiguration config = null;
        if (engine instanceof RemoteRuntimeEngine) {
            Access<RemoteConfiguration> configAccess = new FieldAccess<RemoteConfiguration>(RemoteRuntimeEngine.class, "config");
            config = configAccess.isReadable() ? configAccess.read(engine) : null;
        }
        return config;
    }

    private InitialContext configRemoteJms(RemoteJmsRuntimeEngineBuilder builder, RemoteJmsModel model) {
        configRemote(builder, model);
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

    @SuppressWarnings("deprecation")
    private void configRemoteRest(RemoteRestRuntimeEngineBuilder builder, RemoteRestModel model) {
        configRemote(builder, model);
        try {
            builder.addUrl(new URL(model.getUrl()));
        } catch (MalformedURLException mue) {
            throw new RuntimeException(mue);
        }
        builder.useFormBasedAuth(model.isUseFormBasedAuth());
    }

    private void configRemote(RemoteRuntimeEngineBuilder<?, ?> builder, RemoteModel model) {
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
            ClassLoader loader = getClassLoader();
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

    /**
     * Builds a RemoteConfiguration.
     * @return a RemoteConfiguration
     */
    public RemoteConfiguration build() {
        return _remoteConfiguration.clone();
    }

}
