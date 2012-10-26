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
package org.switchyard.component.bpm.task.work;

import java.util.Map;

/**
 * Represents a manager of a Task.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface TaskManager {

    /**
     * Registers a handler.
     * @param handler the handler
     */
    public void registerHandler(TaskHandler handler);

    /**
     * Marks the task as complete.
     * @param id the task id
     * @param results the task results
     */
    public void completeTask(Long id, Map<String, Object> results);

    /**
     * Marks the task as aborted.
     * @param id the task id
     */
    public void abortTask(Long id);

    /**
     * Signals an event in any process instance.
     * @param type the type of event
     * @param event the event object
     */
    public void signalEvent(String type, Object event);

    /**
     * Signals an event in a specific process instance.
     * @param type the type of event
     * @param event the event object
     * @param processInstanceId the id of the process instance to single
     */
    public void signalEvent(String type, Object event, Long processInstanceId);

}
