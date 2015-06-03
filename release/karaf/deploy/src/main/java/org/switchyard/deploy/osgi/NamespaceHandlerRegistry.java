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

import org.osgi.framework.Bundle;

import java.net.URI;
import java.util.Set;

/**
 * Registry of NamespaceHandler.
 */
public interface NamespaceHandlerRegistry {

    /**
     * Retrieve the <code>NamespaceHandler</code> for the specified URI.
     * @param uri the namespace identifying the namespace handler
     * @param bundle the blueprint bundle to be checked for class space consistency
     * @return a set of registered <code>NamespaceHandler</code>s compatible with the class space of the given bundle
     */
    NamespaceHandlerSet getNamespaceHandlers(Set<URI> uri, Bundle bundle);

    /**
     * Destroy this registry.
     */
    void destroy();
}
