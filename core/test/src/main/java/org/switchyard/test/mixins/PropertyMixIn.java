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
package org.switchyard.test.mixins;

import java.util.Map;

import org.switchyard.common.property.TestPropertyResolver;

/**
 * Property Test Mix-In for setting test properties that will be respected in configurations.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class PropertyMixIn extends AbstractTestMixIn {

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        clear();
    }

    /**
     * Gets the test Map.
     * @return the test Map
     */
    public Map<String, Object> getMap() {
        return TestPropertyResolver.INSTANCE.getMap();
    }

    /**
     * Gets a test property.
     * @param key the property key
     * @return the property value
     */
    public Object get(String key) {
        return getMap().get(key);
    }

    /**
     * Sets a test property.
     * @param key the property key
     * @param value the property value
     */
    public void set(String key, Object value) {
        getMap().put(key, value);
    }

    /**
     * Clears all test properties.
     */
    public void clear() {
        getMap().clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uninitialize() {
        clear();
    }

}
