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
import java.util.ServiceLoader;
import java.util.Set;

import org.switchyard.common.type.Classes;

/**
 * Gets Versions via their specification titles.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public abstract class VersionFactory {

    private static final VersionFactory INSTANCE;
    static {
        final ClassLoader loader = Classes.getClassLoader(VersionFactory.class);
        final Iterator<VersionFactory> i = ServiceLoader.load(VersionFactory.class, loader).iterator();
        INSTANCE = i.hasNext() ? i.next() : new VersionFactory() {
            @Override
            public Version getVersion(Query... queries) {
                return null;
            }
            @Override
            public Set<Version> getVersions(Query... queries) {
                return Collections.emptySet();
            }
        };
    }

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
