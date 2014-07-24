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
package org.switchyard.component.common.knowledge.session;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.services.client.api.command.RemoteRuntimeEngine;

/**
 * A Remote-based StatelessKieSession.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class KnowledgeRemoteStatelessKieSession implements StatelessKieSession {

    private final RemoteRuntimeEngine _remoteRuntimeEngine;
    private KieSession _ksession;

    /**
     * Creates a new KnowledgeRemoteStatelessKieSession.
     * @param remoteRuntimeEngine the RemoteRuntimeEngine
     */
    KnowledgeRemoteStatelessKieSession(RemoteRuntimeEngine remoteRuntimeEngine) {
        _remoteRuntimeEngine = remoteRuntimeEngine;
        newSession();
    }

    /**
     * Gets the current KieSession.
     * @return the current KieSession
     */
    private KieSession getSession() {
        return _ksession;
    }

    /**
     * Replaces the current KieSession with a new one.
     */
    private void newSession() {
        _ksession = _remoteRuntimeEngine.getKieSession();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Object object) {
        newSession();
        //try {
        getSession().insert(object);
        getSession().fireAllRules();
        //} finally {
            // not supported by remote impl
            //getSession().dispose();
        //}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public void execute(Iterable objects) {
        newSession();
        //try {
            for (Object object : objects) {
                getSession().insert(object);
            }
            getSession().fireAllRules();
        //} finally {
            // not supported by remote impl
            //getSession().dispose();
        //}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T execute(Command<T> command) {
        T t;
        newSession();
        //try {
            t = getSession().execute(command);
        //} finally {
            // not supported by remote impl
            //getSession().dispose();
        //}
        return t;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KieRuntimeLogger getLogger() {
        return getSession().getLogger();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEventListener(RuleRuntimeEventListener listener) {
        // not supported by remote impl
        //getSession().addEventListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEventListener(RuleRuntimeEventListener listener) {
        // not supported by remote impl
        //getSession().removeEventListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<RuleRuntimeEventListener> getRuleRuntimeEventListeners() {
        return Collections.<RuleRuntimeEventListener>emptyList();
        // not supported by remote impl
        //return getSession().getRuleRuntimeEventListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEventListener(AgendaEventListener listener) {
        // not supported by remote impl
        //getSession().addEventListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEventListener(AgendaEventListener listener) {
        // not supported by remote impl
        //getSession().removeEventListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<AgendaEventListener> getAgendaEventListeners() {
        return Collections.<AgendaEventListener>emptyList();
        // not supported by remote impl
        //return getSession().getAgendaEventListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEventListener(ProcessEventListener listener) {
        // not supported by remote impl
        //getSession().addEventListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEventListener(ProcessEventListener listener) {
        // not supported by remote impl
        //getSession().removeEventListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ProcessEventListener> getProcessEventListeners() {
        return Collections.<ProcessEventListener>emptyList();
        // not supported by remote impl
        //return getSession().getProcessEventListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Globals getGlobals() {
        return null;
        // not supported by remote impl
        //return getSession().getGlobals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGlobal(String identifier, Object value) {
        getSession().setGlobal(identifier, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerChannel(String name, Channel channel) {
        getSession().registerChannel(name, channel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterChannel(String name) {
        getSession().unregisterChannel(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Channel> getChannels() {
        return getSession().getChannels();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KieBase getKieBase() {
        return null;
        // not supported by remote impl
        //return getSession().getKieBase();
    }

}
