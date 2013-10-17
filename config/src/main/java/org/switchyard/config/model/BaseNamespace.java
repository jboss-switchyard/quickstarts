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
package org.switchyard.config.model;

/**
 * Base class for a config model namespace.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public abstract class BaseNamespace implements Namespace {

    private final String _section;
    private final String _version;
    private final String _uri;

    /**
     * Constructs a default Namespace with a specified descriptor and section.
     * @param desc descriptor
     * @param section section
     */
    protected BaseNamespace(Descriptor desc, String section) {
        _section = section;
        _uri = desc.getDefaultNamespace(_section);
        _version = _uri.substring(_uri.lastIndexOf(':')+1, _uri.length());
    }

    /**
     * Constructs a Namespace with a specified descriptor, section, and version.
     * @param desc descriptor
     * @param section section
     * @param version version
     */
    protected BaseNamespace(Descriptor desc, String section, String version) {
        _section = section;
        _version = version;
        _uri = desc.getNamespace(_section, _version);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String section() {
        return _section;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String version() {
        return _version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String uri() {
        return _uri;
    }

}
