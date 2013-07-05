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
package org.switchyard.common.type.reflect;

/**
 * An abstraction of field and method access.
 *
 * @param <T> the value type of this access
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface Access<T> {

    /**
     * The name of the wrapped access mechanism.
     * @return the name
     */
    public String getName();

    /**
     * The Class type of the wrapped access mechanism.
     * @return the Class type
     */
    public Class<T> getType();

    /**
     * Whether the wrapped access mechanism is readable.
     * @return true if it's readable
     */
    public boolean isReadable();

    /**
     * Whether the wrapped access mechanism is writable.
     * @return true if it's writable
     */
    public boolean isWriteable();

    /**
     * Reads via the wrapped access mechanism targeting the specified object.
     * @param target the target object to read from
     * @return the read value
     */
    public T read(Object target);

    /**
     * Writes via the wrapped access mechanism targeting the specified object.
     * @param target the target object to write to
     * @param value to value to write
     */
    public void write(Object target, T value);

}
