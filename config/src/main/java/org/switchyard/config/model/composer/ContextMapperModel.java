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

package org.switchyard.config.model.composer;

import org.switchyard.config.model.Model;

/**
 * The "contextMapper" configuration model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface ContextMapperModel extends Model {

    /** contextMapper variable. */
    public static final String CONTEXT_MAPPER = "contextMapper";

    /**
     * Gets the fully qualified class name of ContextMapper.
     * @return the fully qualified class name of ContextMapper
     */
    public String getClazz();

    /**
     * Sets the fully qualified class name of ContextMapper.
     * @param clazz the fully qualified class name of ContextMapper
     * @return this instance (useful for chaining)
     */
    public ContextMapperModel setClazz(String clazz);

    /**
     * Gets the comma-separated list of regex property includes.
     * @return the comma-separated list of regex property includes
     */
    public String getIncludes();

    /**
     * Sets the comma-separated list of regex property includes.
     * @param includes the comma-separated list of regex property includes
     * @return this instance (useful for chaining)
     */
    public ContextMapperModel setIncludes(String includes);

    /**
     * Gets the comma-separated list of regex property excludes.
     * @return the comma-separated list of regex property excludes
     */
    public String getExcludes();

    /**
     * Sets the comma-separated list of regex property excludes.
     * @param excludes the comma-separated list of regex property excludes
     * @return this instance (useful for chaining)
     */
    public ContextMapperModel setExcludes(String excludes);

    /**
     * Gets the comma-separated list of regex property include namespaces.
     * @return the comma-separated list of regex property include namespaces
     */
    public String getIncludeNamespaces();

    /**
     * Sets the comma-separated list of regex property include namespaces.
     * @param includeNamespaces the comma-separated list of regex property include namespaces
     * @return this instance (useful for chaining)
     */
    public ContextMapperModel setIncludeNamespaces(String includeNamespaces);

    /**
     * Gets the comma-separated list of regex property exclude namespace.
     * @return the comma-separated list of regex property exclude namespace
     */
    public String getExcludeNamespaces();

    /**
     * Sets the comma-separated list of regex property exclude namespace.
     * @param excludeNamespaces the comma-separated list of regex property exclude namespace
     * @return this instance (useful for chaining)
     */
    public ContextMapperModel setExcludeNamespaces(String excludeNamespaces);

}
