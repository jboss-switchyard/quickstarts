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
