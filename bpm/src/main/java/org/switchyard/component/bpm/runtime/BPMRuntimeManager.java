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

import org.jbpm.runtime.manager.impl.RuntimeEngineImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.Context;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.task.TaskService;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.RuntimeEnvironment;

/**
 * TODO: This implementation of RuntimeManager should be removed after SWITCHYARD-1584.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public final class BPMRuntimeManager implements InternalRuntimeManager {

    private final RuntimeEngineImpl _runtimeEngine;
    private final String _identifier;
    private final RuntimeEnvironment _environment;

    /**
     * Constructs a new BPMRuntimeManager.
     * @param kieSession the KieSession
     * @param taskService the TaskService
     * @param identifier the identifier
     * @param environment the RuntimeEnvironment
     */
    public BPMRuntimeManager(KieSession kieSession, TaskService taskService, String identifier, RuntimeEnvironment environment) {
        _identifier = identifier;
        _runtimeEngine = new RuntimeEngineImpl(kieSession, taskService) {
            @Override
            public void dispose() {
                // no-op
            }
        };
        _runtimeEngine.setManager(this);
        _environment = environment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return _identifier;
    }

    /**
     * Gets the RuntimeEngine.
     * @return the RuntimeEngine
     */
    public RuntimeEngine getRuntimeEngine() {
        return _runtimeEngine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuntimeEngine getRuntimeEngine(Context<?> context) {
        return getRuntimeEngine();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disposeRuntimeEngine(RuntimeEngine runtime) {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(KieSession ksession, Context<?> context) throws IllegalStateException {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuntimeEnvironment getEnvironment() {
        return _environment;
    }

}
