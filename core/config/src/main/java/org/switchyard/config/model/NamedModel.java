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
package org.switchyard.config.model;

import java.net.URI;

import javax.xml.namespace.QName;

/**
 * A Model with a name.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface NamedModel extends Model {

    /**
     * Gets the name <b>attribute</b> of this Model (<i>not</i> the name of the wrapped Configuration).
     * @return the name
     */
    public String getName();

    /**
     * Sets the name <b>attribute</b> of this Model (<i>not</i> the name of the wrapped Configuration).
     * @param name the name
     * @return this NamedModel (useful for chaining)
     */
    public NamedModel setName(String name);

    /**
     * Gets the targetNamespace of this Model from the wrapped Configuration.
     * @return the targetNamespace
     */
    public String getTargetNamespace();

    /**
     * Gets the targetNamespace URI of this Model from the wrapped Configuration.
     * @return the targetNamespace URI
     */
    public URI getTargetNamespaceURI();

    /**
     * Gets the qualified name <b>attribute</b> of this Model (<i>not</i> the qualified name of the wrapped Configuration).
     * @return the qualified name
     */
    public QName getQName();

}
