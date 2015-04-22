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

import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.switchyard.ServiceDomain;
import org.switchyard.component.common.knowledge.config.manifest.RemoteManifest;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.runtime.KnowledgeRuntimeManagerType;
import org.switchyard.component.common.knowledge.runtime.remote.RemoteRuntimeManager;

/**
 * RuntimeManagerBuilder.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class RuntimeManagerBuilder extends KnowledgeBuilder {

    private final RuntimeManagerFactory _runtimeManagerFactory;
    private final RuntimeEnvironmentBuilder _runtimeEnvironmentBuilder;

    /**
     * Creates a RuntimeManagerBuilder.
     * @param classLoader classLoader
     * @param serviceDomain serviceDomain
     * @param implementationModel implementationModel
     */
    public RuntimeManagerBuilder(ClassLoader classLoader, ServiceDomain serviceDomain, KnowledgeComponentImplementationModel implementationModel) {
        super(classLoader, serviceDomain);
        _runtimeManagerFactory = RuntimeManagerFactory.Factory.get();
        _runtimeEnvironmentBuilder = new RuntimeEnvironmentBuilder(getClassLoader(), serviceDomain, implementationModel);
    }

    /**
     * Builds a RuntimeManager.
     * @param type type
     * @param identifier identifier
     * @return a RuntimeManager
     */
    public RuntimeManager build(KnowledgeRuntimeManagerType type, String identifier) {
        final RuntimeManager runtimeManager;
        final RuntimeEnvironment runtimeEnvironment = _runtimeEnvironmentBuilder.build();
        final RemoteManifest remoteManifest = RemoteManifest.removeFromEnvironment(runtimeEnvironment.getEnvironment());
        if (remoteManifest != null) {
            runtimeManager = new RemoteRuntimeManager(remoteManifest.buildConfiguration(), identifier);
        } else {
            switch (type) {
                case SINGLETON:
                    runtimeManager = _runtimeManagerFactory.newSingletonRuntimeManager(runtimeEnvironment, identifier);
                    break;
                case PER_REQUEST:
                    runtimeManager = _runtimeManagerFactory.newPerRequestRuntimeManager(runtimeEnvironment, identifier);
                    break;
                case PER_PROCESS_INSTANCE:
                    runtimeManager = _runtimeManagerFactory.newPerProcessInstanceRuntimeManager(runtimeEnvironment, identifier);
                    break;
                default:
                    runtimeManager = null;
                    break;
            }
        }
        return runtimeManager;
    }

}
