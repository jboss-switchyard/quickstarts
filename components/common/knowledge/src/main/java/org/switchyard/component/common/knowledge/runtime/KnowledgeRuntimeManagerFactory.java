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

import static org.switchyard.deploy.ServiceDomainManager.ROOT_DOMAIN;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.namespace.QName;

import org.kie.api.runtime.manager.RuntimeManager;
import org.switchyard.ServiceDomain;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.knowledge.config.builder.ChannelBuilder;
import org.switchyard.component.common.knowledge.config.builder.LoggerBuilder;
import org.switchyard.component.common.knowledge.config.builder.RuntimeManagerBuilder;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;

/**
 * KnowledgeRuntimeManagerFactory.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class KnowledgeRuntimeManagerFactory {

    private static final AtomicInteger IDENTIFIER_COUNT = new AtomicInteger();

    private final ClassLoader _classLoader;
    private final RuntimeManagerBuilder _runtimeManagerBuilder;
    private final List<ChannelBuilder> _channelBuilders;
    private final List<LoggerBuilder> _loggerBuilders;
    private final boolean _persistent;
    private final QName _serviceDomainName;
    private final QName _serviceName;
    private final String _identifierRoot;

    /**
     * Creates a new KnowledgeRuntimeManagerFactory.
     * @param classLoader the classLoader
     * @param serviceDomain the serviceDomain
     * @param serviceName the serviceName
     * @param implementationModel the implementationModel
     */
    public KnowledgeRuntimeManagerFactory(
            ClassLoader classLoader,
            ServiceDomain serviceDomain,
            QName serviceName,
            KnowledgeComponentImplementationModel implementationModel) {
        _classLoader = classLoader;
        _runtimeManagerBuilder = new RuntimeManagerBuilder(classLoader, serviceDomain, implementationModel);
        _persistent = implementationModel.isPersistent();
        _channelBuilders = ChannelBuilder.builders(classLoader, serviceDomain, implementationModel);
        _loggerBuilders = LoggerBuilder.builders(classLoader, implementationModel);
        _serviceDomainName = serviceDomain.getName() != null ? serviceDomain.getName() : ROOT_DOMAIN;
        _serviceName = serviceName;
        _identifierRoot = _serviceDomainName.toString() + "/" + _serviceName.toString() + "/";
    }

    /**
     * Creates a new KnowledgeRuntimeManager.
     * @param type the KnowledgeRuntimeManagerType
     * @return the new KnowledgeRuntimeManager
     */
    public KnowledgeRuntimeManager newRuntimeManager(KnowledgeRuntimeManagerType type) {
        RuntimeManager runtimeManager;
        final String identifier = _identifierRoot + IDENTIFIER_COUNT.incrementAndGet();
        final ClassLoader origTCCL = Classes.setTCCL(_classLoader);
        try {
            runtimeManager = _runtimeManagerBuilder.build(type, identifier);
        } finally {
            Classes.setTCCL(origTCCL);
        }
        return new KnowledgeRuntimeManager(
                _classLoader, type, _serviceDomainName, _serviceName, runtimeManager, _persistent, _channelBuilders, _loggerBuilders);
    }

}
