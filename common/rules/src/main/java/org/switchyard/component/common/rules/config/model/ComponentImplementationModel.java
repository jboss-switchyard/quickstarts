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
package org.switchyard.component.common.rules.config.model;

import java.util.List;

import org.switchyard.component.common.rules.ClockType;
import org.switchyard.component.common.rules.EventProcessingType;
import org.switchyard.config.model.resource.ResourceModel;

/**
 * ComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface ComponentImplementationModel extends org.switchyard.config.model.composite.ComponentImplementationModel {

    /**
     * Gets the "agent" attribute.
     *
     * @return the "agent" attribute
     */
    public boolean isAgent();

    /**
     * Sets the "agent" attribute.
     *
     * @param agent the "agent" attribute
     * @return this instance (useful for chaining)
     */
    public ComponentImplementationModel setAgent(boolean agent);

    /**
     * Gets the "clock" attribute.
     *
     * @return the "clock" attribute
     */
    public ClockType getClock();

    /**
     * Sets the "clock" attribute.
     *
     * @param clock the "clock" attribute
     * @return this instance (useful for chaining)
     */
    public ComponentImplementationModel setClock(ClockType clock);

    /**
     * Gets the "eventProcessing" attribute.
     *
     * @return the "eventProcessing" attribute
     */
    public EventProcessingType getEventProcessing();

    /**
     * Sets the "eventProcessing" attribute.
     *
     * @param eventProcessing the "eventProcessing" attribute
     * @return this instance (useful for chaining)
     */
    public ComponentImplementationModel setEventProcessing(EventProcessingType eventProcessing);

    /**
     * Gets the "maxThreads" attribute.
     *
     * @return the "maxThreads" attribute
     */
    public Integer getMaxThreads();

    /**
     * Sets the "maxThreads" attribute.
     *
     * @param maxThreads the "maxThreads" attribute
     * @return this instance (useful for chaining)
     */
    public ComponentImplementationModel setMaxThreads(Integer maxThreads);

    /**
     * Gets the "multithreadEvaluation" attribute.
     *
     * @return the "multithreadEvaluation" attribute
     */
    public Boolean getMultithreadEvaluation();

    /**
     * Sets the "multithreadEvaluation" attribute.
     *
     * @param multithreadEvaluation the "multithreadEvaluation" attribute
     * @return this instance (useful for chaining)
     */
    public ComponentImplementationModel setMultithreadEvaluation(Boolean multithreadEvaluation);

    /**
     * Gets the "audit" child model.
     * @return the "audit" child model
     */
    public AuditModel getAudit();

    /**
     * Sets the "audit" child model.
     * @param audit the "audit" child model
     * @return this RulesComponentImplementationModel (useful for chaining)
     */
    public ComponentImplementationModel setAudit(AuditModel audit);

    /**
     * Gets the child resource models.
     * @return the child resource models
     */
    public List<ResourceModel> getResources();

    /**
     * Adds a child resource model.
     * @param resource the child resource model
     * @return this RulesComponentImplementationModel (useful for chaining)
     */
    public ComponentImplementationModel addResource(ResourceModel resource);

}
