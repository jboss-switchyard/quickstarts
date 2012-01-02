/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.bpm;


/**
 * Represents available process actions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public enum ProcessActionType {

    /** startProcess . */
    START_PROCESS(ProcessConstants.START_PROCESS),
    /** signalEvent . */
    SIGNAL_EVENT(ProcessConstants.SIGNAL_EVENT),
    /** abortProcessInstance . */
    ABORT_PROCESS_INSTANCE(ProcessConstants.ABORT_PROCESS_INSTANCE);

    private final String _action;

    /**
     * Constructs a new ProcessActionType with the specified action.
     * @param action the action
     */
    ProcessActionType(String action) {
        _action = action;
    }

    /**
     * Gets the action.
     * @return the action
     */
    public String action() {
        return _action;
    }

    /**
     * Gets the ProcessActionType matching the specified action.
     * @param action the action
     * @return the matching ProcessActionType
     */
    public static ProcessActionType fromAction(String action) {
        for (ProcessActionType pat : values()) {
            if (pat.action().equals(action)) {
                return pat;
            }
        }
        return null;
    }

}
