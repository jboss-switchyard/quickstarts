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
package org.switchyard.component.common.knowledge.config.model;

import org.switchyard.component.common.knowledge.LoggerType;
import org.switchyard.config.model.Model;

/**
 * A Channel Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface LoggerModel extends Model {

    /**
     * The logger XML element.
     */
    public static final String LOGGER = "logger";

    /**
     * Gets the interval attribute.
     * @return the interval attribute
     */
    public Integer getInterval();

    /**
     * Sets the interval attribute.
     * @param interval the interval attribute
     * @return this LoggerModel (useful for chaining)
     */
    public LoggerModel setInterval(Integer interval);

    /**
     * Gets the log attribute.
     * @return the log attribute
     */
    public String getLog();

    /**
     * Sets the log attribute.
     * @param log the log attribute
     * @return this LoggerModel (useful for chaining)
     */
    public LoggerModel setLog(String log);

    /**
     * Gets the type attribute.
     * @return the type attribute
     */
    public LoggerType getType();

    /**
     * Sets the type attribute.
     * @param type the type attribute
     * @return this LoggerModel (useful for chaining)
     */
    public LoggerModel setType(LoggerType type);

}
