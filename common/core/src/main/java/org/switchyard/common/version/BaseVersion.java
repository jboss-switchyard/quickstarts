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
package org.switchyard.common.version;

/**
 * BaseVersion.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public class BaseVersion implements Version {

    private final Project _project;
    private final Specification _specification;
    private final Implementation _implementation;

    /**
     * Constructs a new BaseVersion.
     * @param project the Project
     * @param specification the Specification
     * @param implementation the Implementation
     */
    public BaseVersion(Project project, Specification specification, Implementation implementation) {
        _project = project;
        _specification = specification;
        _implementation = implementation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Project getProject() {
        return _project;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Specification getSpecification() {
        return _specification;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Implementation getImplementation() {
        return _implementation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s: %s, %s, %s", Version.class.getSimpleName(), getProject(), getSpecification(), getImplementation());
    }

    /**
     * {@inheritDoc}
     */   
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BaseVersion other = (BaseVersion)obj;
        if (_project == null) {
            if (other._project != null) {
                return false;
            }
        } else if (!_project.equals(other._project)) {
            return false;
        }
        if (_specification == null) {
            if (other._specification != null) {
                return false;
            }
        } else if (!_specification.equals(other._specification)) {
            return false;
        }
        if (_implementation == null) {
            if (other._implementation != null) {
                return false;
            }
        } else if (!_implementation.equals(other._implementation)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */   
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (_project == null ? 0 : _project.hashCode());
        result = prime * result + (_specification == null ? 0 : _specification.hashCode());
        result = prime * result + (_implementation == null ? 0 : _implementation.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Version that) {
        int comparison = 0;
        if (this != that) {
            if (that == null) {
                comparison = -1;
            } else {
                if (_project != null) {
                    comparison = _project.compareTo(that.getProject());
                }
                if (comparison == 0 && _specification != null) {
                    comparison = _specification.compareTo(that.getSpecification());
                }
                if (comparison == 0 && _implementation != null) {
                    comparison = _implementation.compareTo(that.getImplementation());
                }
            }
        }
        return comparison;
    }

    static int compare(String s1, String s2) {
        if (s1 != null) {
            if (s2 != null) {
                return s1.compareTo(s2);
            }
            return -1;
        }
        if (s2 != null) {
            return 1;
        }
        return 0;
    }

}
