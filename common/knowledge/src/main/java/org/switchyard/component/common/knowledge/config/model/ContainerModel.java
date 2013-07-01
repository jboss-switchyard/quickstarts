/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
