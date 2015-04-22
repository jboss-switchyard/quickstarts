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
package org.switchyard.component.common.knowledge.runtime.remote;

import java.util.Map;
import java.util.WeakHashMap;

import org.kie.api.runtime.manager.Context;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.services.client.api.command.RemoteConfiguration;

/**
 * RemoteRuntimeManager.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class RemoteRuntimeManager implements RuntimeManager {

    private static final String NULL_CONTEXT_ID = "NullContext";

    private final RemoteConfiguration _configuration;
    private final String _identifier;
    private Map<Object, RuntimeEngine> _engines = new WeakHashMap<Object, RuntimeEngine>();

    /**
     * Creates a new RemoteRuntimeManager.
     * @param configuration the RemoteConfiguration
     * @param identifier the identifier
     */
    public RemoteRuntimeManager(RemoteConfiguration configuration, String identifier) {
        _configuration = configuration;
        _identifier = identifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized RuntimeEngine getRuntimeEngine(Context<?> context) {
        Object contextId = context != null ? context.getContextId() : null;
        if (contextId == null) {
            contextId = NULL_CONTEXT_ID;
        }
        RuntimeEngine engine = _engines.get(contextId);
        if (engine == null) {
            engine = new ExtendedRemoteRuntimeEngine(_configuration, context);
            _engines.put(contextId, engine);
        }
        return engine;
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
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // no-op
    }

}
