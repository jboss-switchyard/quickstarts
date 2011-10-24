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
package org.switchyard.common.composer;

/**
 * Simple ContextMapperInfo implementation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class SimpleContextMapperInfo implements ContextMapperInfo {

    private Class<?> _clazz;
    private String _includes;
    private String _excludes;
    private String _includeNamespaces;
    private String _excludeNamespaces;

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getClazz() {
        return _clazz;
    }

    /**
     * Sets the class.
     * @param clazz the class
     * @return this instance (useful for chaining)
     */
    public ContextMapperInfo setClass(Class<?> clazz) {
        _clazz = clazz;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIncludes() {
        return _includes;
    }

    /**
     * Sets the includes.
     * @param includes the includes
     * @return this instance (useful for chaining)
     */
    public ContextMapperInfo setIncludes(String includes) {
        _includes = includes;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExcludes() {
        return _excludes;
    }

    /**
     * Sets the excludes.
     * @param excludes the excludes
     * @return this instance (useful for chaining)
     */
    public ContextMapperInfo setExcludes(String excludes) {
        _excludes = excludes;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIncludeNamespaces() {
        return _includeNamespaces;
    }

    /**
     * Sets the includeNamespaces.
     * @param includeNamespaces the includeNamespaces
     * @return this instance (useful for chaining)
     */
    public ContextMapperInfo setIncludesNamespaces(String includeNamespaces) {
        _includeNamespaces = includeNamespaces;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExcludeNamespaces() {
        return _excludeNamespaces;
    }

    /**
     * Sets the excludeNamespaces.
     * @param excludeNamespaces the excludeNamespaces
     * @return this instance (useful for chaining)
     */
    public ContextMapperInfo setExcludesNamespaces(String excludeNamespaces) {
        _excludeNamespaces = excludeNamespaces;
        return this;
    }

}
