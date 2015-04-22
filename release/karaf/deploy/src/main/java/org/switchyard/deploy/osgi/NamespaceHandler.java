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
package org.switchyard.deploy.osgi;

import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Marshaller;

import java.net.URL;
import java.util.Set;

/**
 * NamespaceHandler.
 */
public interface NamespaceHandler {

    /**
     * Key for namespace value used by a namespace handler.
     */
    public static final String NAMESPACES = "switchyard.namespaces";

    /**
     * Return the schema location for the given namespace.
     * @param namespace namespace
     * @return schema location
     */
    URL getSchemaLocation(String namespace);

    /**
     * Create a config marshaller for the specified namespace.
     * @param namespace namespace
     * @param descriptor config descriptor
     * @return marshaller instance
     */
    Marshaller createMarshaller(String namespace, Descriptor descriptor);

    /**
     * Returns the set of marshaller classes used by this handler.
     * @return set of marshaller classes used by this handler.
     */
    Set<Class> getManagedClasses();
}
