/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.switchyard.deploy.osgi;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import javax.xml.validation.Schema;

import org.xml.sax.SAXException;

/**
 * Interface used to managed a set of namespace handlers
 */
public interface NamespaceHandlerSet {

    Set<URI> getNamespaces();

    boolean isComplete();

    /**
     * Retrieve the NamespaceHandler to use for the given namespace
     *
     * @return the NamespaceHandler to use or <code>null</code> if none is available at this time
     */
    NamespaceHandler getNamespaceHandler(URI namespace);

    /**
     * Obtain a schema to validate the XML for the given list of namespaces
     *
     * @return the schema to use to validate the XML
     */
    Schema getSchema() throws SAXException, IOException;

    /**
     * Add a new Listener to be called when namespace handlers are registerd or unregistered
     *
     * @param listener the listener to register
     */
    void addListener(Listener listener);

    /**
     * Remove a previously registered Listener
     *
     * @param listener the listener to unregister
     */
    void removeListener(Listener listener);

    /**
     * Destroy this handler set
     */
    void destroy();

    /**
     * Interface used to listen to registered or unregistered namespace handlers.
     *
     * @see NamespaceHandlerSet#addListener(org.switchyard.deploy.osgi.NamespaceHandlerSet.Listener)
     * @see NamespaceHandlerSet#removeListener(org.switchyard.deploy.osgi.NamespaceHandlerSet.Listener)
     */
    public interface Listener {

        /**
         * Called when a NamespaceHandler has been registered for the specified URI.
         *
         * @param uri the URI of the newly registered namespace handler
         */
        void namespaceHandlerRegistered(URI uri);

        /**
         * Called when a NamespaceHandler has been unregistered for the specified URI.
         *
         * @param uri the URI of the newly unregistered namespace handler
         */
        void namespaceHandlerUnregistered(URI uri);

    }
}