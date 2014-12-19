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
package org.switchyard.component.common.knowledge.runtime;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.event.kiebase.KieBaseEventListener;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.Context;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.runtime.manager.Disposable;
import org.kie.internal.runtime.manager.DisposeListener;
import org.kie.internal.runtime.manager.context.CorrelationKeyContext;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.knowledge.CommonKnowledgeLogger;
import org.switchyard.component.common.knowledge.config.builder.ChannelBuilder;
import org.switchyard.component.common.knowledge.config.builder.LoggerBuilder;
import org.switchyard.component.common.knowledge.config.items.ExtendedRegisterableItemsFactory;
import org.switchyard.component.common.knowledge.config.manifest.ContainerManifest;

/**
 * KnowledgeRuntimeManager.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class KnowledgeRuntimeManager implements RuntimeManager {

    private final KieServices _kieServices;
    private final ClassLoader _classLoader;
    private final KnowledgeRuntimeManagerType _type;
    private final QName _serviceDomainName;
    private final QName _serviceName;
    private final RuntimeManager _runtimeManager;
    private final List<ChannelBuilder> _channelBuilders;
    private final List<LoggerBuilder> _loggerBuilders;
    private final boolean _persistent;
    private final Set<Long> _sessionIds = Collections.synchronizedSet(new LinkedHashSet<Long>());

    /**
     * Creates a new KnowledgeRuntimeManager.
     * @param classLoader the classLoader
     * @param type the type
     * @param serviceDomainName the serviceDomainName
     * @param serviceName the serviceName
     * @param runtimeManager the runtimeManager
     * @param persistent if persistent
     * @param channelBuilders the channelBuilders
     * @param loggerBuilders the loggerBuilders
     */
    KnowledgeRuntimeManager(
            ClassLoader classLoader,
            KnowledgeRuntimeManagerType type,
            QName serviceDomainName,
            QName serviceName,
            RuntimeManager runtimeManager,
            boolean persistent,
            List<ChannelBuilder> channelBuilders,
            List<LoggerBuilder> loggerBuilders) {
        _kieServices = KieServices.Factory.get();
        _classLoader = classLoader;
        _type = type;
        _serviceDomainName = serviceDomainName;
        _serviceName = serviceName;
        _runtimeManager = runtimeManager;
        _persistent = persistent;
        _channelBuilders = channelBuilders;
        _loggerBuilders = loggerBuilders;
    }

    /**
     * Gets the type.
     * @return the type
     */
    public KnowledgeRuntimeManagerType getType() {
        return _type;
    }

    /**
     * Gets the serviceDomainName.
     * @return serviceDomainName
     */
    public QName getServiceDomainName() {
        return _serviceDomainName;
    }

    /**
     * Gets the serviceName.
     * @return the serviceName
     */
    public QName getServiceName() {
        return _serviceName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return _runtimeManager.getIdentifier();
    }

    /**
     * If persistent.
     * @return if persistent
     */
    public boolean isPersistent() {
        return _persistent;
    }

    /**
     * Gets a RuntimeEngine for an undefined {@link Context}.
     * @return the RuntimeEngine
     */
    public RuntimeEngine getRuntimeEngine() {
        return getRuntimeEngine((Context<?>)null);
    }

    /**
     * Gets a RuntimeEngine for a {@link Context} associated with the processInstanceId.
     * @param processInstanceId the processInstanceId
     * @return the RuntimeEngine
     */
    public RuntimeEngine getRuntimeEngine(Long processInstanceId) {
        return getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
    }

    /**
     * Gets a RuntimeEngine for a {@link Context} associated with the correlationKey.
     * @param correlationKey the correlationKey
     * @return the RuntimeEngine
     */
    public RuntimeEngine getRuntimeEngine(CorrelationKey correlationKey) {
        return getRuntimeEngine(CorrelationKeyContext.get(correlationKey));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuntimeEngine getRuntimeEngine(Context<?> context) {
        KnowledgeRuntimeEngine runtimeEngine;
        if (context == null || _type != KnowledgeRuntimeManagerType.PER_PROCESS_INSTANCE) {
            context = EmptyContext.get();
        }
        final ClassLoader origTCCL = Classes.setTCCL(_classLoader);
        try {
            RuntimeEngine wrapped = _runtimeManager.getRuntimeEngine(context);
            initRuntimeEngine(wrapped);
            runtimeEngine = new KnowledgeRuntimeEngine(wrapped, _persistent);
        } finally {
            Classes.setTCCL(origTCCL);
        }
        return runtimeEngine;
    }

    private void initRuntimeEngine(final RuntimeEngine runtimeEngine) {
        // NOTE: RemoteRuntimeEngines are not Disposable, which is fine considering
        // none of these Listeners are allowed to be added in remote cases anyways.
        if (runtimeEngine instanceof Disposable) {
            final Disposable disposable = (Disposable)runtimeEngine;
            final KieSession session = runtimeEngine.getKieSession();
            if (session != null) {
                final Long sessionId = Long.valueOf(session.getIdentifier());
                synchronized (_sessionIds) {
                    if (!_sessionIds.contains(sessionId)) {
                        _sessionIds.add(sessionId);
                        disposable.addDisposeListener(new DisposeListener() {
                            @Override
                            public void onDispose(RuntimeEngine runtime) {
                                synchronized (_sessionIds) {
                                    _sessionIds.remove(sessionId);
                                }
                            }
                        });
                        final Environment environment = session.getEnvironment();
                        ExtendedRegisterableItemsFactory extendedRIF = ExtendedRegisterableItemsFactory.Env.removeFromEnvironment(environment);
                        if (extendedRIF != null) {
                            List<KieBaseEventListener> baseListeners = extendedRIF.getKieBaseEventListeners(runtimeEngine);
                            for (KieBaseEventListener baseListener : baseListeners) {
                                session.getKieBase().addEventListener(baseListener);
                            }
                        }
                        for (ChannelBuilder builder : _channelBuilders) {
                            final String name = builder.getChannelName();
                            Channel channel = builder.build();
                            if (name != null && channel != null) {
                                session.registerChannel(name, channel);
                                disposable.addDisposeListener(new DisposeListener() {
                                    @Override
                                    public void onDispose(RuntimeEngine runtime) {
                                        session.unregisterChannel(name);
                                    }
                                });
                            }
                        }
                        for (LoggerBuilder builder : _loggerBuilders) {
                            final KieRuntimeLogger logger = builder.build(session);
                            disposable.addDisposeListener(new DisposeListener() {
                                @Override
                                public void onDispose(RuntimeEngine runtime) {
                                    try {
                                        logger.close();
                                    } catch (Throwable t) {
                                        CommonKnowledgeLogger.ROOT_LOGGER.problemClosingKieRuntimeLogger(t.getMessage());
                                    }
                                }
                            });
                        }
                        ContainerManifest containerManifest = ContainerManifest.removeFromEnvironment(environment);
                        if (containerManifest != null && containerManifest.isScan()) {
                            KieContainer kieContainer = containerManifest.getKieContainer();
                            final ReleaseId releaseId = containerManifest.getReleaseId();
                            final KieScanner scanner = _kieServices.newKieScanner(kieContainer);
                            //final KieScanner scanner = new KnowledgeScanner(kieContainer);
                            disposable.addDisposeListener(new DisposeListener() {
                                @Override
                                public void onDispose(RuntimeEngine runtime) {
                                    try {
                                        scanner.stop();
                                        scanner.shutdown();
                                    } catch (Throwable t) {
                                        CommonKnowledgeLogger.ROOT_LOGGER.problemStopppingKieScanner(t.getMessage());
                                    } finally {
                                        if (releaseId != null) {
                                            // fix for SWITCHYARD-2241
                                            _kieServices.getRepository().removeKieModule(releaseId);
                                        }
                                    }
                                }
                            });
                            scanner.start(containerManifest.getScanInterval().longValue());
                        }
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disposeRuntimeEngine(RuntimeEngine runtime) {
        if (runtime instanceof KnowledgeRuntimeEngine) {
            runtime = ((KnowledgeRuntimeEngine)runtime).getWrapped();
        }
        _runtimeManager.disposeRuntimeEngine(runtime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        _runtimeManager.close();
    }

    /* attempt at fixing SWITCHYARD-2240
    private static final class KnowledgeScanner extends KieRepositoryScannerImpl {
        private ReleaseId _originalReleaseId = null;
        private KnowledgeScanner(KieContainer kieContainer) {
            setKieContainer(kieContainer);
        }
        @Override
        public synchronized void setKieContainer(KieContainer kieContainer) {
            _originalReleaseId = kieContainer != null ? kieContainer.getReleaseId() : null;
            super.setKieContainer(kieContainer);
        }
        @Override
        @SuppressWarnings("unchecked")
        public synchronized void scanNow() {
            Set<ReleaseId> releaseIds = new HashSet<ReleaseId>();
            try {
                Method scanForUpdates = KieRepositoryScannerImpl.class.getDeclaredMethod("scanForUpdates", new Class[0]);
                scanForUpdates.setAccessible(true);
                Map<DependencyDescriptor, Artifact> updatedArtifacts = (Map<DependencyDescriptor, Artifact>)scanForUpdates.invoke(this, (Object[])null);
                if (!updatedArtifacts.isEmpty()) {
                    if (_originalReleaseId != null) {
                        releaseIds.add(_originalReleaseId);
                    }
                    for (DependencyDescriptor dd : updatedArtifacts.keySet()) {
                        releaseIds.add(dd.getReleaseId());
                        releaseIds.add(dd.getArtifactReleaseId());
                    }
                    //KieRepository kieRepository = KieServices.Factory.get().getRepository();
                    //for (ReleaseId releaseId : releaseIds) {
                    //    kieRepository.removeKieModule(releaseId);
                    //}
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            super.scanNow();
            //KieRepository kieRepository = KieServices.Factory.get().getRepository();
            //for (ReleaseId releaseId : releaseIds) {
            //    kieRepository.removeKieModule(releaseId);
            //}
        }
    }
    */

}
