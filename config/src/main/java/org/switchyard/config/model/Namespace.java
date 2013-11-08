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
 * Represents config model namespace.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public interface Namespace {

    /**
     * The namespace section.
     * @return the namespace section
     */
    public String section();

    /**
     * The namespace version.
     * @return the namespace version
     */
    public String version();

    /**
     * If the version of the specified namespace matches the version of this namespace.
     * @param namespace the specified namespace
     * @return if the versions match
     */
    public boolean versionMatches(Namespace namespace);

    /**
     * The namespace uri.
     * @return the namespace uri
     */
    public String uri();

    /**
     * If the uri of the specified namespace matches the uri of this namespace.
     * @param namespace the specified namespace
     * @return if the uris match
     */
    public boolean uriMatches(Namespace namespace);

    /**
     * If this namespace is the default namespace, rule being: <code>(this == DEFAULT || versionMatches(DEFAULT))</code>.
     * @return if this namespace is the default namespace
     */
    public boolean isDefault();

    /**
     * A utility class for Namespace enums, since enums can't have extend base classes.
     */
    public static abstract class Util implements Namespace {

        private final String _section;
        private final String _version;
        private final String _uri;

        /**
         * Constructs a new Util.
         * @param descriptor descriptor
         * @param section section
         * @param version version
         */
        protected Util(Descriptor descriptor, String section, String version) {
            _section = section;
            if (version != null) {
                _version = version;
                _uri = descriptor.getNamespace(_section, _version);
            } else {
                _uri = descriptor.getDefaultNamespace(_section);
                _version = extractVersion(_uri);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String section() {
            return _section;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String version() {
            return _version;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean versionMatches(Namespace namespace) {
            String version = namespace != null ? namespace.version() : null;
            return version != null ? version.equals(_version) : _version == null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String uri() {
            return _uri;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean uriMatches(Namespace namespace) {
            String uri = namespace != null ? namespace.uri() : null;
            return uri != null ? uri.equals(_uri) : _uri == null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isDefault() {
            return false;
        }

        /**
         * Extracts the version from the uri.
         * @param uri the uri
         * @return the version
         */
        public static String extractVersion(String uri) {
            if (uri != null) {
                int pos = uri.lastIndexOf(':');
                if (pos > -1) {
                    return uri.substring(pos + 1, uri.length());
                }
            }
            return null;
        }

        /**
         * Gets a Namespace from a uri.
         * @param <N> the Namespace enum type
         * @param enumType the Namespace enum type
         * @param uri the uri
         * @return the Namespace
         */
        public static <N extends Namespace> N fromUri(Class<N> enumType, String uri) {
            if (uri != null) {
                N[] ns = enumType.getEnumConstants();
                if (ns != null) {
                    for (N n : ns) {
                        if (n.uri().equals(uri)) {
                            return n;
                        }
                    }
                }
            }
            return null;
        }

    }

}
