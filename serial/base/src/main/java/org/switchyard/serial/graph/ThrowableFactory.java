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
package org.switchyard.serial.graph;

import java.lang.reflect.Constructor;

import org.switchyard.common.type.reflect.Construction;
import org.switchyard.serial.SerialMessages;
import org.switchyard.serial.graph.node.Node;
import org.switchyard.serial.graph.node.ThrowableAccessNode;

/**
 * The default factory the AccessNode will use.
 * 
 * @param <T> the factory type
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class ThrowableFactory<T> extends BaseFactory<T> {

    private static final Class<?>[][] PARAMETER_TYPES = new Class<?>[][]{
        new Class<?>[]{String.class},
        new Class<?>[0]
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> type) {
        if (type != null) {
            return getConstructor(type) != null;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T create(Class<T> type, Node node) {
        T obj = null;
        if (type != null) {
            Constructor<?> constructor = getConstructor(type);
            Class<?>[] parameterTypes = constructor != null ? constructor.getParameterTypes() : new Class<?>[0];
            try {
                if (parameterTypes.length == 0) {
                    obj = Construction.construct(type);
                } else if (parameterTypes.length == 1) {
                    String message = (node instanceof ThrowableAccessNode) ? ((ThrowableAccessNode)node).getMessage() : null;
                    obj = Construction.construct(type, parameterTypes, new Object[]{message});
                }
            } catch (Throwable t) {
                throw SerialMessages.MESSAGES.couldNotInstantiateThrowable(type.getName());
            }
        }
        return obj;
    }

    private Constructor<?> getConstructor(Class<?> type) {
        Constructor<?> constructor = null;
        for (Class<?>[] parameterTypes : PARAMETER_TYPES) {
            try {
                constructor = type.getConstructor(parameterTypes);
                if (constructor != null) {
                    break;
                }
            } catch (Throwable t) {
                // keep checkstyle happy ("at least one statement")
                t.getMessage();
            }
        }
        return constructor;
    }

}
