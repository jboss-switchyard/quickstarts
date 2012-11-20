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
import org.kie.runtime.process.WorkItemHandler;
import org.switchyard.ServiceDomain;

/**
 * A SwitchYard WorkItemHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface SwitchYardWorkItemHandler extends WorkItemHandler {

    /**
     * Gets the name.
     * @return the name
     */
    public String getName();

    /**
     * Sets the name.
     * @param name the name
     * @return this SwitchYardWorkItemHandler (useful for chaining)
     */
    public SwitchYardWorkItemHandler setName(String name);

    /**
     * Gets the targetNamespace.
     * @return the targetNamespace
     */
    public String getTargetNamespace();

    /**
     * Sets the targetNamespace.
     * @param targetNamespace the targetNamespace
     * @return this SwitchYardWorkItemHandler (useful for chaining)
     */
    public SwitchYardWorkItemHandler setTargetNamespace(String targetNamespace);

    /**
     * Gets the ServiceDomain.
     * @return the ServiceDomain
     */
    public ServiceDomain getServiceDomain();

    /**
     * Sets the ServiceDomain.
     * @param serviceDomain the ServiceDomain
     * @return this SwitchYardWorkItemHandler (useful for chaining)
     */
    public SwitchYardWorkItemHandler setServiceDomain(ServiceDomain serviceDomain);

    /**
     * Gets the ProcessRuntime.
     * @return the ProcessRuntime
     */
    public ProcessRuntime getProcessRuntime();

    /**
     * Sets the ProcessRuntime.
     * @param processRuntime the ProcessRuntime
     * @return this SwitchYardWorkItemHandler (useful for chaining)
     */
    public SwitchYardWorkItemHandler setProcessRuntime(ProcessRuntime processRuntime);

}
