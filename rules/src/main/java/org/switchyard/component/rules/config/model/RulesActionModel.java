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
package org.switchyard.component.rules.config.model;

import org.switchyard.component.rules.common.RulesActionType;
import org.switchyard.config.model.Model;

/**
 * RulesActionModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface RulesActionModel extends Model {

    /**
     * The rulesAction XML element.
     */
    public static final String RULES_ACTION = "rulesAction";

    /**
     * Gets the name of the action.
     * @return the name of the action
     */
    public String getName();

    /**
     * Sets the name of the action.
     * @param name the name of the action
     * @return this RulesActionModel (useful for chaining)
     */
    public RulesActionModel setName(String name);

    /**
     * Gets the type of the action.
     * @return the type of the action
     */
    public RulesActionType getType();
 
    /**
     * Sets the type of the action.
     * @param type the type of the action
     * @return this RulesActionModel (useful for chaining)
     */
    public RulesActionModel setType(RulesActionType type);

}
