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
package org.switchyard.component.common.knowledge.session;

import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.switchyard.component.common.knowledge.util.Disposals;

/**
 * A Knowledge session.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class KnowledgeSession extends KnowledgeDisposer {

    private final StatelessKieSession _stateless;
    private final KieSession _stateful;
    private final boolean _remote;
    private final boolean _persistent;

    /**
     * Constructs a new stateless knowledge session.
     * @param stateless the wrapped session
     * @param remote if remote
     * @param disposals any disposals
     */
    public KnowledgeSession(StatelessKieSession stateless, boolean remote, KnowledgeDisposal... disposals) {
        _stateless = stateless;
        _stateful = null;
        _remote = remote;
        _persistent = false;
        addDisposals(disposals);
    }

    /**
     * Constructs a new stateful knowledge session.
     * @param stateful the wrapped session
     * @param remote if remote
     * @param persistent if persistent
     * @param disposals any disposals
     */
    public KnowledgeSession(KieSession stateful, boolean remote, boolean persistent, KnowledgeDisposal... disposals) {
        _stateless = null;
        _stateful = stateful;
        _remote = remote;
        _persistent = persistent;
        addDisposals(disposals);
        addDisposals(Disposals.newDisposal(_stateful));
    }

    /**
     * If the wrapped session is stateless.
     * @return if so
     */
    public boolean isStateless() {
        return _stateless != null;
    }

    /**
     * If the wrapped session is stateful.
     * @return if so
     */
    public boolean isStateful() {
        return _stateful != null;
    }

    /**
     * If the wrapped session is remote.
     * @return if so
     */
    public boolean isRemote() {
        return _remote;
    }

    /**
     * If the wrapped session is stateful and persistent.
     * @return if so
     */
    public boolean isPersistent() {
        return isStateful() && _persistent;
    }

    /**
     * Gets the wrapped stateless session.
     * @return the session
     */
    public StatelessKieSession getStateless() {
        return _stateless;
    }

    /**
     * Gets the wrapped stateful session.
     * @return the session
     */
    public KieSession getStateful() {
        return _stateful;
    }

    /**
     * Gets the wrapped stateful session id.
     * @return the id
     */
    public Integer getId() {
        if (isStateful()) {
            return Integer.valueOf(getStateful().getId());
        }
        return null;
    }

    /**
     * Gets the wrapped globals.
     * @return the globals
     */
    public Globals getGlobals() {
        if (!isRemote()) {
            if (isStateless()) {
                return getStateless().getGlobals();
            }
            if (isStateful()) {
                return getStateful().getGlobals();
            }
        }
        return null;
    }

}
