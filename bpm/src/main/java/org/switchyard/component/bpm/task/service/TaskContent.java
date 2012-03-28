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
package org.switchyard.component.bpm.task.service;

import java.io.Serializable;
import java.util.Map;

/**
 * The content of a Task.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface TaskContent extends Serializable {

    /**
     * Gets the id.
     * @return the id
     */
    public Long getId();

    /**
     * Sets the id.
     * @param id the id
     * @return this Content (useful for chaining)
     */
    public TaskContent setId(Long id);

    /**
     * Gets the type.
     * @return the type
     */
    public String getType();

    /**
     * Sets the type.
     * @param type the type
     * @return this Content (useful for chaining)
     */
    public TaskContent setType(String type);

    /**
     * Gets the object.
     * @return the object
     */
    public Object getObject();

    /**
     * Gets the object as the specified class type.
     * @param clazz the specified class
     * @param <T> the specified type
     * @return the object as the specified class
     */
    public <T> T getObject(Class<T> clazz);

    /**
     * Sets the object.
     * @param object the object
     * @return this Content (useful for chaining)
     */
    public TaskContent setObject(Object object);

    /**
     * Gets the map.
     * @return the map
     */
    public Map<String, Object> getMap();

    /**
     * Sets the map.
     * @param map the map
     * @return this Content (useful for chaining)
     */
    public TaskContent setMap(Map<String, Object> map);

    /**
     * Gets the bytes.
     * @return the bytes
     */
    public byte[] getBytes();

    /**
     * Sets the bytes.
     * @param bytes the bytes
     * @return this Content (useful for chaining)
     */
    public TaskContent setBytes(byte[] bytes);

}
