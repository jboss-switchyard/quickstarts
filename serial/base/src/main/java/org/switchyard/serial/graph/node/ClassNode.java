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
package org.switchyard.serial.graph.node;

import org.switchyard.common.type.Classes;
import org.switchyard.serial.graph.Graph;

/**
 * A node representing a Class.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@SuppressWarnings("serial")
public final class ClassNode implements Node {

    private String _name;

    /**
     * Default constructor.
     */
    public ClassNode() {}

    /**
     * Gets the name.
     * @return the name
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the name.
     * @param name the name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public void compose(Object obj, Graph graph) {
        Class clazz = (Class)obj;
        setName(clazz.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object decompose(Graph graph) {
        return Classes.forName(getName(), getClass());
    }

}
