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

import org.switchyard.serial.graph.Graph;

/**
 * A node representing a noop.
 * 
 * Note that there is a noop property, however it isn't used.  It only exists so serializer implementations (like Jackson or Protostuff) don't complain about "no properties".
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
@SuppressWarnings("serial")
public class NoopNode implements Node {

    /**
     * A single instance of NoopNode.
     */
    public static final NoopNode INSTANCE = new NoopNode();

    private String _noop;

    /**
     * Gets the noop.
     * @return the noop
     */
    public String getNoop() {
        return _noop;
    }

    /**
     * Sets the noop.
     * @param noop the noop
     */
    public void setNoop(String noop) {
        _noop = noop;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(Object obj, Graph graph) {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object decompose(Graph graph) {
        // noop
        return null;
    }

}
