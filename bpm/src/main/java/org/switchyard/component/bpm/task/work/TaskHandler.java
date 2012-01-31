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

import org.switchyard.ServiceDomain;

/**
 * Represents a task handler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface TaskHandler {

    /**
     * Gets the name of the handler.
     * @return the name of the handler
     */
    public String getName();

    /**
     * Sets the name of the handler.
     * @param name the name of the handler
     * @return this handler (useful for chaining)
     */
    public TaskHandler setName(String name);

    /**
     * Gets the message content in name.
     * @return the name of the message content in
     */
    public String getMessageContentInName();

    /**
     * Sets the message content in name.
     * @param messageContentInName the message content in name
     * @return this handler (useful for chaining)
     */
    public TaskHandler setMessageContentInName(String messageContentInName);

    /**
     * Gets the message content out name.
     * @return the name of the message content out
     */
    public String getMessageContentOutName();

    /**
     * Sets the message content out name.
     * @param messageContentOutName the message content out name
     * @return this handler (useful for chaining)
     */
    public TaskHandler setMessageContentOutName(String messageContentOutName);

    /**
     * Gets the target namespace.
     * @return the target namespace
     */
    public String getTargetNamespace();

    /**
     * Sets the target namespace.
     * @param targetNamespace the target namespace
     * @return this handler (useful for chaining)
     */
    public TaskHandler setTargetNamespace(String targetNamespace);

    /**
     * Gets the ServiceDomain.
     * @return the ServiceDomain
     */
    public ServiceDomain getServiceDomain();

    /**
     * Sets the ServiceDomain.
     * @param serviceDomain the ServiceDomain
     * @return this handler (useful for chaining)
     */
    public TaskHandler setServiceDomain(ServiceDomain serviceDomain);

    /**
     * Initializes this handler.
     */
    public void init();

    /**
     * Destroys this handler.
     */
    public void destroy();

    /**
     * Executes the specified task.
     * @param task the specified task
     * @param manager the manager
     */
    public void executeTask(Task task, TaskManager manager);

    /**
     * Aborts the specified task.
     * @param task the specified task
     * @param manager the manager
     */
    public void abortTask(Task task, TaskManager manager);

}
