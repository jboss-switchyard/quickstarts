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
package org.switchyard.serial.graph.node;

import java.util.Map;

/**
 * Reflection-based node for arbitrary objects.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
@SuppressWarnings("serial")
public final class DefaultAccessNode extends AccessNode {

    private Integer _clazz;
    private Map<String, Integer> _ids;

    /**
     * Default constructor.
     */
    public DefaultAccessNode() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getClazz() {
        return _clazz;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClazz(Integer clazz) {
        _clazz = clazz;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Integer> getIds() {
        return _ids;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIds(Map<String, Integer> ids) {
        _ids = ids;
    }

}
