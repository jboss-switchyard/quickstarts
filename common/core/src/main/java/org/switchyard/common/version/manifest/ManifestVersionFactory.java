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
package org.switchyard.common.version.manifest;

import static java.util.jar.JarFile.MANIFEST_NAME;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;
import org.switchyard.common.type.Classes;
import org.switchyard.common.version.BaseVersionFactory;
import org.switchyard.common.version.Query;
import org.switchyard.common.version.Version;

/**
 * The factory for ManifestVersion.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public final class ManifestVersionFactory extends BaseVersionFactory {

    private static final Logger LOGGER = Logger.getLogger(ManifestVersionFactory.class);

    /**
     * {@inheritDoc}
     */   
    @Override
    public Set<Version> getVersions(boolean returnFirstOnly, Query... queries) {
        List<URL> urls;
        try {
            urls = Classes.getResources(MANIFEST_NAME, getClass());
        } catch (IOException ioe) {
            urls = Collections.emptyList();
        }
        Set<Version> versions = new TreeSet<Version>();
        urlLoop: for (URL url : urls) {
            InputStream is = null;
            try {
                URLConnection conn = url.openConnection();
                // avoid file-locking on Windows
                conn.setUseCaches(false);
                is = conn.getInputStream();
                Version version = new ManifestVersion(new Manifest(is));
                boolean matches = true;
                queryLoop: for (Query query : queries) {
                    if (!query.matches(version)) {
                        matches = false;
                        break queryLoop;
                    }
                }
                if (matches) {
                    versions.add(version);
                    if (returnFirstOnly) {
                        break urlLoop;
                    }
                }
            } catch (IOException ioe) {
                LOGGER.warn(String.format("problem reading %s stream: %s", url, ioe.getMessage()));
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ioe) {
                        LOGGER.warn(String.format("problem closing %s stream: %s", url, ioe.getMessage()));
                    }
                }
            }
        }
        return versions;
    }

}
