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

import java.net.URL;

/**
 * Project.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public interface Project extends Comparable<Project> {

    /**
     * Gets the groupId.
     * @return the groupId
     */
    public String getGroupId();

    /**
     * Gets the artifactId.
     * @return the artifactId
     */
    public String getArtifactId();

    /**
     * Gets the packaging.
     * @return the packaging
     */
    public String getPackaging();

    /**
     * Gets the name.
     * @return the name
     */
    public String getName();

    /**
     * Gets the description.
     * @return the description
     */
    public String getDescription();

    /**
     * Gets the URL.
     * @return the URL
     */
    public URL getURL();

    /**
     * Gets the version.
     * @return the version
     */
    public String getVersion();

}
