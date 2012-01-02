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

import org.switchyard.ServiceDomain;

/**
 * Base functionality of TaskHandlers.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseTaskHandler implements TaskHandler {

    private String _name;
    private String _messageContentInName;
    private String _messageContentOutName;
    private String _targetNamespace;
    private ServiceDomain _serviceDomain;

    /**
     * Constructs a new BaseTaskHandler using the simple name of the implementation class.
     */
    public BaseTaskHandler() {
        setName(getClass().getSimpleName());
    }

    /**
     * Constructs a new BaseTaskHandler using the specified name.
     * @param name the specified name
     */
    public BaseTaskHandler(String name) {
        setName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskHandler setName(String name) {
        _name = name;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageContentInName() {
        return _messageContentInName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskHandler setMessageContentInName(String messageContentInName) {
        _messageContentInName = messageContentInName;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageContentOutName() {
        return _messageContentOutName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskHandler setMessageContentOutName(String messageContentOutName) {
        _messageContentOutName = messageContentOutName;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTargetNamespace() {
        return _targetNamespace;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskHandler setTargetNamespace(String targetNamespace) {
        _targetNamespace = targetNamespace;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceDomain getServiceDomain() {
        return _serviceDomain;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskHandler setServiceDomain(ServiceDomain serviceDomain) {
        _serviceDomain = serviceDomain;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        // override if necessary
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        // override if necessary
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeTask(Task task, TaskManager taskManager) {
        // override if necessary
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void abortTask(Task task, TaskManager taskManager) {
        // override if necessary
    }

}
