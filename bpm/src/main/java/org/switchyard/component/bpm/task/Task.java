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
package org.switchyard.component.bpm.task;

import java.util.Map;

/**
 * Represents a unit of work (a "task").
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface Task {

    /**
     * Gets the task id.
     * @return the task id
     */
    public Long getId();

    /**
     * Gets the task name.
     * @return the task name
     */
    public String getName();

    /**
     * Gets the task state.
     * @return the task state
     */
    public TaskState getState();

    /**
     * Gets a parameter of the task.
     * @param name the name of the parameter
     * @return the value of the parameter
     */
    public Object getParameter(String name);

    /**
     * Gets all parameters of the task.
     * @return all parameters of the task
     */
    public Map<String, Object> getParameters();

    /**
     * Gets a named result.
     * @param name the name of the result
     * @return the value of the result
     */
    public Object getResult(String name);

    /**
     * Gets all results of the task.
     * @return all results of the task
     */
    public Map<String, Object> getResults();

    /**
     * Gets the associated process instance id of the task.
     * @return the associated process instance id of the task
     */
    public Long getProcessInstanceId();

    /**
     * Gets a named process instance variable.
     * @param name the name of the process instance variable
     * @return the value of the process instance variable
     */
    public Object getProcessInstanceVariable(String name);

    /**
     * Sets a named process instance variable.
     * @param name the name of the process instance variable
     * @param value the value of the process instance variable
     * @return this Task (useful for chaining)
     */
    public Task setProcessInstanceVariable(String name, Object value);

}
