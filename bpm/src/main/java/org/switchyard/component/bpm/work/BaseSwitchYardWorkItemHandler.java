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
package org.switchyard.component.bpm.work;

import org.kie.runtime.process.ProcessRuntime;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemManager;
import org.switchyard.ServiceDomain;

/**
 * A base implementation of a SwitchYardWorkItemHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class BaseSwitchYardWorkItemHandler implements SwitchYardWorkItemHandler {

    private String _name;
    private String _targetNamespace;
    private ServiceDomain _serviceDomain;
    private ProcessRuntime _processRuntime;

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
    public SwitchYardWorkItemHandler setName(String name) {
        _name = name;
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
    public SwitchYardWorkItemHandler setTargetNamespace(String targetNamespace) {
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
    public SwitchYardWorkItemHandler setServiceDomain(ServiceDomain serviceDomain) {
        _serviceDomain = serviceDomain;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessRuntime getProcessRuntime() {
        return _processRuntime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SwitchYardWorkItemHandler setProcessRuntime(ProcessRuntime processRuntime) {
        _processRuntime = processRuntime;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        // override as necessary
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // override as necessary
    }

}
