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
package org.switchyard.component.soap.config.model;

import org.switchyard.config.model.Model;

/**
 * A Mtom Model.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public interface MtomModel extends Model {

    /**
     * Get if MTOM enabled.
     * @return true if enabled, false otherwise
     */
    public Boolean isEnabled();

    /**
     * Enable or disable MTOM feature.
     * @param enabled true to enable, false to diable
     * @return this MtomModel (useful for chaining)
     */
    public MtomModel setEnabled(Boolean enabled);

    /**
     * Gets the threshold value.
     * @return the configName
     */
    public Integer getThreshold();

    /**
     * Sets the threshold value.
     * @param threshold the threshold value to set
     * @return this MtomModel (useful for chaining)
     */
    public MtomModel setThreshold(Integer threshold);

    /**
     * Get if XOP expand enabled.
     * @return true if XOP expand enabled, false otherwise
     */
    public Boolean isXopExpand();

    /**
     * Enable or disable XOP expand feature.
     * @param enabled true to enable, false to diable
     * @return this MtomModel (useful for chaining)
     */
    public MtomModel setXopExpand(Boolean enabled);

}
