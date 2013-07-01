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
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.task.TaskService;
import org.kie.internal.runtime.manager.Disposable;

/**
 * A "bpm" implementation of a RuntimeManager.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public final class BPMRuntimeManager implements RuntimeManager {

    private final RuntimeEngineImpl _runtimeEngine;
    private final String _identifier;

    /**
     * Constructs a new BPMRuntimeManager with the specified KieSession, TaskService and identifier.
     * @param kieSession the KieSession
     * @param taskService the TaskService
     * @param identifier the identifier
     */
    public BPMRuntimeManager(KieSession kieSession, TaskService taskService, String identifier) {
        _runtimeEngine = new RuntimeEngineImpl(kieSession, taskService);
        _runtimeEngine.setManager(this);
        _identifier = identifier;
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
    public String getIdentifier() {
        return _identifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disposeRuntimeEngine(RuntimeEngine runtime) {
        if (runtime instanceof Disposable) {
            ((Disposable)runtime).dispose();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // no-op
    }

}
