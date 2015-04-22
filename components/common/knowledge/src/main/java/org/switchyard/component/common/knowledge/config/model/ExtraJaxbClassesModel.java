/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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

import java.util.List;

import org.switchyard.config.model.Model;

/**
 * A ExtraJaxbClasses Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public interface ExtraJaxbClassesModel extends Model {

    /** The "extraJaxbClasses" name. */
    public static final String EXTRA_JAXB_CLASSES = "extraJaxbClasses";

    /**
     * Gets the child extraJaxbClass models.
     * @return the child extraJaxbClass models
     */
    public List<ExtraJaxbClassModel> getExtraJaxbClasses();

    /**
     * Adds a child extraJaxbClass model.
     * @param extraJaxbClass the child extraJaxbClass model
     * @return this ExtraJaxbClassesModel (useful for chaining)
     */
    public ExtraJaxbClassesModel addExtraJaxbClass(ExtraJaxbClassModel extraJaxbClass);

}
