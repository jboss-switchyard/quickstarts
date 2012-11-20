/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.common.knowledge.session;

import org.kie.runtime.Globals;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatelessKieSession;
import org.switchyard.component.common.knowledge.util.Disposals;

/**
 * A Knowledge session.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class KnowledgeSession extends KnowledgeDisposer {

    private final StatelessKieSession _stateless;
    private final KieSession _stateful;
    private final boolean _persistent;

    /**
     * Constructs a new stateless knowledge session.
     * @param stateless the wrapped session
     * @param disposals any disposals
     */
    public KnowledgeSession(StatelessKieSession stateless, KnowledgeDisposal... disposals) {
        _stateless = stateless;
        _stateful = null;
        _persistent = false;
        addDisposals(disposals);
    }

    /**
     * Constructs a new stateful knowledge session.
     * @param stateful the wrapped session
     * @param persistent if persistent
     * @param disposals any disposals
     */
    public KnowledgeSession(KieSession stateful, boolean persistent, KnowledgeDisposal... disposals) {
        _stateless = null;
        _stateful = stateful;
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
        if (isStateless()) {
            return getStateless().getGlobals();
        }
        if (isStateful()) {
            return getStateful().getGlobals();
        }
        return null;
    }

}
