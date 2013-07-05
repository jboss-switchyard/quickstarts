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
