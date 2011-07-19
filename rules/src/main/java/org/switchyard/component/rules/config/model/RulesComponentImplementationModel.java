/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

package org.switchyard.component.rules.config.model;

import java.util.List;

import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.resource.ResourceModel;

/**
 * A "rules" ComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface RulesComponentImplementationModel extends ComponentImplementationModel {

    /**
     * The "rules" namespace.
     */
    public static final String DEFAULT_NAMESPACE = "urn:switchyard-component-rules:config:1.0";

    /**
     * The "rules" implementation type.
     */
    public static final String RULES = "rules";

    /**
     * Gets the "stateful" attribute.
     *
     * @return the "stateful" attribute
     */
    public boolean isStateful();

    /**
     * Sets the "stateful" attribute.
     *
     * @param stateful the "stateful" attribute
     * @return this instance (useful for chaining)
     */
    public RulesComponentImplementationModel setStateful(boolean stateful);

    /**
     * Gets the "messageContentName" attribute.
     *
     * @return the "messageContentName" attribute
     */
    public String getMessageContentName();

    /**
     * Sets the "messageContentName" attribute.
     *
     * @param messageContentName the "messageContentName" attribute
     * @return this instance (useful for chaining)
     */
    public RulesComponentImplementationModel setMessageContentName(String messageContentName);

    /**
     * Gets the "rulesAudit" child model.
     * @return the "rulesAudit" child model
     */
    public RulesAuditModel getRulesAudit();

    /**
     * Sets the "rulesAudit" child model.
     * @param rulesAudit the "rulesAudit" child model
     * @return this RulesComponentImplementationModel (useful for chaining)
     */
    public RulesComponentImplementationModel setRulesAudit(RulesAuditModel rulesAudit);

    /**
     * Gets the child rules action models.
     * @return the child rules action models
     */
    public List<RulesActionModel> getRulesActions();

    /**
     * Adds a child rules action model.
     * @param rulesAction the child rules action model
     * @return this RulesComponentImplementationModel (useful for chaining)
     */
    public RulesComponentImplementationModel addRulesAction(RulesActionModel rulesAction);

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
    public RulesComponentImplementationModel addResource(ResourceModel resource);

}
