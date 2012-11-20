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
package org.switchyard.component.bpm.config.model;

import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;

/**
 * BPMComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface BPMComponentImplementationModel extends KnowledgeComponentImplementationModel {

    /**
     * The "bpm" namespace.
     */
    public static final String DEFAULT_NAMESPACE = "urn:switchyard-component-bpm:config:1.0";

    /**
     * The "bpm" implementation type.
     */
    public static final String BPM = "bpm";

    /**
     * Gets the "persistent" attribute.
     * @return the "persistent" attribute
     */
    public boolean isPersistent();

    /**
     * Sets the "persistent" attribute.
     * @param persistent the "persistent" attribute
     * @return this instance (useful for chaining)
     */
    public BPMComponentImplementationModel setPersistent(boolean persistent);

    /**
     * Gets the "processId" attribute.
     * @return the "processId" attribute
     */
    public String getProcessId();

    /**
     * Sets the "processId" attribute.
     * @param processId the "processId" attribute
     * @return this instance (useful for chaining)
     */
    public BPMComponentImplementationModel setProcessId(String processId);

    /**
     * Gets the "sessionId" attribute.
     * @return the "sessionId" attribute
     */
    public Integer getSessionId();

    /**
     * Sets the "sessionId" attribute.
     * @param sessionId the "sessionId" attribute
     * @return this instance (useful for chaining)
     */
    public BPMComponentImplementationModel setSessionId(Integer sessionId);

    /**
     * Gets the child workItemHandlers model.
     * @return the child workItemHandlers model
     */
    public WorkItemHandlersModel getWorkItemHandlers();

    /**
     * Sets the child workItemHandlers model.
     * @param workItemHandlers the child workItemHandlers model
     * @return this instance (useful for chaining)
     */
    public BPMComponentImplementationModel setWorkItemHandlers(WorkItemHandlersModel workItemHandlers);

}
