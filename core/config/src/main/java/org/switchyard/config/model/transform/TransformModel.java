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
package org.switchyard.config.model.transform;

import javax.xml.namespace.QName;

import org.switchyard.config.model.Model;

/**
 * The "transform" configuration model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface TransformModel extends Model {

    /** The "transform" name. */
    public static final String TRANSFORM = "transform";

    /** The "from" name. */
    public static final String FROM = "from";

    /** The "to" name. */
    public static final String TO = "to";

    /**
     * Gets the parent transforms model.
     * @return the parent transforms model.
     */
    public TransformsModel getTransforms();

    /**
     * Gets the from attribute.
     * @return the from attribute
     */
    public QName getFrom();

    /**
     * Sets the from attribute.
     * @param from the from attribute
     * @return this TransformModel (useful for chaining)
     */
    public TransformModel setFrom(QName from);

    /**
     * Gets the to attribute.
     * @return the to attribute
     */
    public QName getTo();

    /**
     * Sets the to attribute.
     * @param to the to attribute
     * @return this TransformModel (useful for chaining)
     */
    public TransformModel setTo(QName to);

}
