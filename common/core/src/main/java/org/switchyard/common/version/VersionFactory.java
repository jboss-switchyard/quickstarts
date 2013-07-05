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
package org.switchyard.common.version;

import java.util.Set;

import org.switchyard.common.version.manifest.ManifestVersionFactory;

/**
 * Gets Versions via Queries.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public abstract class VersionFactory {

    // Hardcoding until we actually have a reason to populate dynamically (via ServiceLoader).
    private static final VersionFactory INSTANCE = new ManifestVersionFactory();

    /**
     * Gets the first version matching the provided queries.
     * @param queries the queries
     * @return the first matching version
     */
    public abstract Version getVersion(Query... queries);

    /**
     * Gets all versions matching the provided queries.
     * @param queries the queries
     * @return all the matching versions
     */
    public abstract Set<Version> getVersions(Query... queries);

    /**
     * Returns the singleton instance of the VersionFactory.
     * @return the singleton instance
     */
    public static final VersionFactory instance() {
        return INSTANCE;
    }

}
