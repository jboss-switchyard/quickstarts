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
package org.switchyard.serial.graph;

import org.switchyard.common.type.reflect.Construction;

/**
 * The default factory the AccessNode will use.
 * 
 * @param <T> the factory type
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class DefaultFactory<T> extends BaseFactory<T> {

    /**
     * {@inheritDoc}
     */
    @Override
    public T create(Class<T> type) {
        return Construction.construct(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> type) {
        try {
            if (type.getDeclaredConstructor() != null) {
                return true;
            }
        } catch (NoSuchMethodException nsme1) {
            // keep checkstyle happy (at least one statement)
            nsme1.getMessage();
        }
        try {
            if (type.getConstructor() != null) {
                return true;
            }
        } catch (NoSuchMethodException nsme2) {
            // keep checkstyle happy (at least one statement)
            nsme2.getMessage();
        }
        return false;
    }

}
