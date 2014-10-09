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
package org.switchyard.component.common.knowledge.runtime;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.jbpm.process.audit.AuditLogService;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.task.TaskService;
import org.kie.internal.task.api.EventService;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.services.client.api.command.RemoteRuntimeEngine;
import org.switchyard.component.common.knowledge.transaction.TransactionInvocationHandler;

/**
 * KnowledgeRuntimeEngine.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class KnowledgeRuntimeEngine implements RuntimeEngine {

    private final RuntimeEngine _wrapped;
    private final boolean _persistent;

    /**
     * Constructs a new knowledge runtime.
     * @param wrapped the wrapped RuntimeEngine
     * @param persistent if persistence is enabled
     */
    public KnowledgeRuntimeEngine(RuntimeEngine wrapped, boolean persistent) {
        _wrapped = wrapped;
        _persistent = persistent;
    }

    /**
     * Gets the RuntimeEngine.
     * @return the RuntimeEngine
     */
    public RuntimeEngine getWrapped() {
        return _wrapped;
    }

    /**
     * If the wrapped KieSession is remote.
     * @return if so
     */
    public boolean isRemote() {
        return _wrapped instanceof RemoteRuntimeEngine;
    }

    /**
     * If persistence is enabled.
     * @return true if persistence is enabled
     */
    public boolean isPersistent() {
        return _persistent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KieSession getKieSession() {
        return _wrapped.getKieSession();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskService getTaskService() {
        InvocationHandler ih = new TransactionInvocationHandler(_wrapped.getTaskService(), _persistent);
        return (TaskService)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{InternalTaskService.class, EventService.class}, ih);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuditService getAuditLogService() {
        InvocationHandler ih = new TransactionInvocationHandler(_wrapped.getAuditLogService(), _persistent);
        return (AuditService)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{AuditLogService.class}, ih);
    }

    /**
     * Gets the wrapped KieSession id.
     * @return the id
     */
    public Integer getSessionId() {
        return Integer.valueOf(getKieSession().getId());
    }

    /**
     * Gets the wrapped globals.
     * @return the globals
     */
    public Globals getSessionGlobals() {
        return isRemote() ? null : getKieSession().getGlobals();
    }

}
