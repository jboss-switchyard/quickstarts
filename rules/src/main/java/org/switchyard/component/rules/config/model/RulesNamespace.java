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
package org.switchyard.component.rules.config.model;

import org.switchyard.component.common.knowledge.config.model.KnowledgeNamespace;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Namespace;

/**
 * A Rules config model namespace.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public enum RulesNamespace implements KnowledgeNamespace {

    /** The 1.0 namespace. */
    V_1_0("1.0"),
    /** The 1.1 namespace. */
    V_1_1("1.1"),
    /** The 2.0 namespace. */
    V_2_0("2.0"),
    /** The default namespace. */
    DEFAULT(null);

    private final Util _util;

    /**
     * Constructs a new RulesNamespace with the specified version.
     * @param version the specified version, or null to discover the default
     */
    RulesNamespace(String version) {
        _util = new Util(version);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String section() {
        return _util.section();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String version() {
        return _util.version();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean versionMatches(Namespace namespace) {
        return _util.versionMatches(namespace);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String uri() {
        return _util.uri();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean uriMatches(Namespace namespace) {
        return _util.uriMatches(namespace);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDefault() {
        return this == DEFAULT || uriMatches(DEFAULT);
    }

    /**
     * Gets the RulesNamespace for the specified uri, or null if no matching uris are found.
     * @param uri the uri
     * @return the RulesNamespace
     */
    public static RulesNamespace fromUri(String uri) {
        return Util.fromUri(RulesNamespace.class, uri);
    }

    private static final class Util extends Namespace.Util {
        // static final since we only want to do the somewhat expensive work of instantiating this once!
        private static final Descriptor DESCRIPTOR = new Descriptor(RulesNamespace.class);
        private Util(String version) {
            super(DESCRIPTOR, "urn:switchyard-component-rules:config", version);
        }
    }

}
