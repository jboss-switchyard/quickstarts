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
package org.switchyard.config.model.domain;

import org.switchyard.config.model.NamedModel;
import org.switchyard.config.model.property.PropertiesModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * The "domain" configuration model.
 */
public interface DomainModel extends NamedModel {
    
    /** The "domain" name. */
    public static final String DOMAIN = "domain";
    
    /**
     * Gets the parent switchyard model.
     * @return the parent switchyard model
     */
    public SwitchYardModel getSwitchYard();
    
    /**
     * Gets the properties defined for the domain.
     * @return properties for the domain
     */
    public PropertiesModel getProperties();
    
    /**
     * Sets the child properties model.
     * @param properties the child properties model.
     * @return this DomainModel (useful for chaining)
     */
    public DomainModel setProperties(PropertiesModel properties);
    
    /**
     * Gets the list of handlers defined at the domain level.
     * @return handlers for the domain
     */
    public HandlersModel getHandlers();
    
    /**
     * Sets the child handlers model.
     * @param handlers the child handlers model.
     * @return this DomainModel (useful for chaining)
     */
    public DomainModel setHandlers(HandlersModel handlers);
    
    /**
     * Gets the child securities model.
     * @return the child securities model
     */
    public SecuritiesModel getSecurities();
    
    /**
     * Sets the child securities model.
     * @param securities the child securities model
     * @return this DomainModel (useful for chaining)
     */
    public DomainModel setSecurities(SecuritiesModel securities);
    
}
