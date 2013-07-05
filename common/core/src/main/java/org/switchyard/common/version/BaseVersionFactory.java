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

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * BaseVersionFactory.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public abstract class BaseVersionFactory extends VersionFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public final Version getVersion(Query... queries) {
        Iterator<Version> i = getVersions(true, queries).iterator();
        return i.hasNext() ? i.next() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Set<Version> getVersions(Query... queries) {
        return Collections.unmodifiableSet(getVersions(false, queries));
    }

    /**
     * Gets all (or the first) versions matching the provided queries.
     * @param returnFirstOnly true if only return the first matching version, false if returning all matching versions
     * @param queries the queries
     * @return all the matching (or first) version
     */
    public abstract Set<Version> getVersions(boolean returnFirstOnly, Query... queries);

}
