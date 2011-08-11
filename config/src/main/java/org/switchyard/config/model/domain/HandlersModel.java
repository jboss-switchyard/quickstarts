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

import java.util.List;

import org.switchyard.config.model.Model;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * The "handlers" configuration model.
 */
public interface HandlersModel extends Model {

    /** The "handlers" name. */
    public static final String HANDLERS = "handlers";

    /**
     * Gets the parent switchyard model.
     * @return the parent switchyard model
     */
    public SwitchYardModel getSwitchYard();

    /**
     * Gets the child handler models.
     * @return the child handler models
     */
    public List<HandlerModel> getHandlers();

    /**
     * Adds a child handler model.
     * @param handler the child handler model to add
     * @return this TransformsModel (useful for chaining)
     */
    public HandlersModel addHandler(HandlerModel handler);
    
    /**
     * Removes a child handler model.
     * @param handlerName the name of the handler
     * @return the removed handler or null if the named handler was not present
     */
    public HandlerModel removeHandler(String handlerName);

}
