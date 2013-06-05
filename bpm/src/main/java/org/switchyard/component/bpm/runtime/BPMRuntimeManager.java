/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
