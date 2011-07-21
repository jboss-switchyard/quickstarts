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

import org.switchyard.config.model.Model;

/**
 * The "handler" configuration model.
 */
public interface HandlerModel extends Model {

    /** The "handler" name. */
    public static final String HANDLER = "handler";

    /** The "name" name. */
    public static final String NAME = "name";

    /** The "class" name. */
    public static final String CLASS = "class";

    /**
     * Gets the handler name attribute.
     * @return the name attribute
     */
    public String getName();

    /**
     * Sets the handler name attribute.
     * @param name the name attribute
     * @return this HandlerModel (useful for chaining)
     */
    public HandlerModel setName(String name);

    /**
     * Gets the class attribute.
     * @return the class attribute
     */
    public String getClassName();

    /**
     * Sets the class attribute.
     * @param className the class attribute
     * @return this HandlerModel (useful for chaining)
     */
    public HandlerModel setClassName(String className);

    /**
     * Gets the parent handlers model.
     * @return the parent handlers model.
     */
    public HandlersModel getHandlers();


}
