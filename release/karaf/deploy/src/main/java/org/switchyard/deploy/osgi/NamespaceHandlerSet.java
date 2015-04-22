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

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import javax.xml.validation.Schema;

import org.xml.sax.SAXException;

/**
 * Interface used to managed a set of namespace handlers.
 */
public interface NamespaceHandlerSet {

    /**
     * Retrieve the set of namespaces used by handlers.
     * @return set of namespaces
     */
    Set<URI> getNamespaces();

    boolean isComplete();

    /**
     * Retrieve the NamespaceHandler to use for the given namespace.
     * @param namespace namespace
     * @return the NamespaceHandler to use or <code>null</code> if none is available at this time
     */
    NamespaceHandler getNamespaceHandler(URI namespace);

    /**
     * Obtain a schema to validate the XML for the given list of namespaces.
     * @return the schema to use to validate the XML
     * @throws IOException failed to load schema
     * @throws SAXException failed to parse schema
     */
    Schema getSchema() throws SAXException, IOException;

    /**
     * Add a new Listener to be called when namespace handlers are registerd or unregistered.
     * @param listener the listener to register
     */
    void addListener(Listener listener);

    /**
     * Remove a previously registered Listener.
     * @param listener the listener to unregister
     */
    void removeListener(Listener listener);

    /**
     * Destroy this handler set.
     */
    void destroy();

    /**
     * Interface used to listen to registered or unregistered namespace handlers.
     * @see NamespaceHandlerSet#addListener(org.switchyard.deploy.osgi.NamespaceHandlerSet.Listener)
     * @see NamespaceHandlerSet#removeListener(org.switchyard.deploy.osgi.NamespaceHandlerSet.Listener)
     */
    public interface Listener {

        /**
         * Called when a NamespaceHandler has been registered for the specified URI.
         * @param uri the URI of the newly registered namespace handler
         */
        void namespaceHandlerRegistered(URI uri);

        /**
         * Called when a NamespaceHandler has been unregistered for the specified URI.
         * @param uri the URI of the newly unregistered namespace handler
         */
        void namespaceHandlerUnregistered(URI uri);

    }
}