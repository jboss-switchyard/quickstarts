/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.common.knowledge.config.model;

import org.switchyard.config.model.Model;

/**
 * A Container Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface ContainerModel extends Model {

    /** The "container" name. */
    public static final String CONTAINER = "container";

    /**
     * Gets the baseName attribute.
     * @return the baseName attribute
     */
    public String getBaseName();

    /**
     * Sets the baseName attribute.
     * @param baseName the baseName attribute
     * @return this ContainerModel (useful for chaining)
     */
    public ContainerModel setBaseName(String baseName);

    /**
     * Gets the releaseId attribute.
     * @return the releaseId attribute
     */
    public String getReleaseId();

    /**
     * Sets the releaseId attribute.
     * @param releaseId the releaseId attribute
     * @return this ContainerModel (useful for chaining)
     */
    public ContainerModel setReleaseId(String releaseId);

    /**
     * Gets the scan attribute.
     * @return the scan attribute
     */
    public boolean isScan();

    /**
     * Sets the scan attribute.
     * @param scan the scan attribute
     * @return this ContainerModel (useful for chaining)
     */
    public ContainerModel setScan(boolean scan);

    /**
     * Gets the scanInterval attribute.
     * @return the scanInterval attribute
     */
    public Long getScanInterval();

    /**
     * Sets the scanInterval attribute.
     * @param scanInterval the scanInterval attribute
     * @return this ContainerModel (useful for chaining)
     */
    public ContainerModel setScanInterval(Long scanInterval);

    /**
     * Gets the sessionName attribute.
     * @return the sessionName attribute
     */
    public String getSessionName();

    /**
     * Sets the sessionName attribute.
     * @param sessionName the baseName attribute
     * @return this ContainerModel (useful for chaining)
     */
    public ContainerModel setSessionName(String sessionName);

}
