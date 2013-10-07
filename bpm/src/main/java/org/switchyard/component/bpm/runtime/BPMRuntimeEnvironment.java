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
package org.switchyard.component.bpm.runtime;

import javax.persistence.EntityManagerFactory;

import org.jbpm.runtime.manager.impl.SimpleRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.mapper.InMemoryMapper;
import org.jbpm.runtime.manager.impl.mapper.JPAMapper;
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
// SWITCHYARD-1755: internal api usage still required (until jbpm services usage is resolved)
import org.kie.internal.runtime.manager.Mapper;
import org.kie.internal.runtime.manager.RegisterableItemsFactory;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.task.api.UserGroupCallback;

/**
 * TODO: This implementation of RuntimeEnvironment should be removed after SWITCHYARD-1584.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public final class BPMRuntimeEnvironment implements RuntimeEnvironment {

    private final KieBase _kieBase;
    private final Environment _environment;
    private final KieSessionConfiguration _configuration;
    private final boolean _usePersistence;
    private final RegisterableItemsFactory _registerableItemsFactory;
    private final Mapper _mapper;
    private final UserGroupCallback _userGroupCallback;
    private final ClassLoader _classLoader;

    /**
     * Constructs a new BPMRuntimeEnvironment.
     * @param kieSession the KieSession
     * @param processEntityManagerFactory the EntityManagerFactory
     * @param userGroupCallback the UserGroupCallback
     * @param classLoader the ClassLoader
     */
    public BPMRuntimeEnvironment(
            KieSession kieSession,
            EntityManagerFactory processEntityManagerFactory,
            UserGroupCallback userGroupCallback,
            ClassLoader classLoader) {
        _kieBase = kieSession.getKieBase();
        _environment = kieSession.getEnvironment();
        _configuration = kieSession.getSessionConfiguration();
        _usePersistence = processEntityManagerFactory != null;
        _registerableItemsFactory = new SimpleRegisterableItemsFactory();
        _mapper = _usePersistence ? new JPAMapper(processEntityManagerFactory) : new InMemoryMapper();
        _userGroupCallback = userGroupCallback;
        _classLoader = classLoader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KieBase getKieBase() {
        return _kieBase;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Environment getEnvironment() {
        return _environment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KieSessionConfiguration getConfiguration() {
        return _configuration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean usePersistence() {
        return _usePersistence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RegisterableItemsFactory getRegisterableItemsFactory() {
        return _registerableItemsFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mapper getMapper() {
        return _mapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserGroupCallback getUserGroupCallback() {
        return _userGroupCallback;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassLoader getClassLoader() {
        return _classLoader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // no-op
    }

}
