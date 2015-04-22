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
package org.switchyard.common.type;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * A ClassLoader implementation that iterates over a collection of other ClassLoaders until it finds everything it's looking for.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class CompoundClassLoader extends ClassLoader {

    private Collection<ClassLoader> _loaders;

    /**
     * Constructs a new CompoundClassLoader.
     * @param loaders the loaders to iterate over
     */
    public CompoundClassLoader(ClassLoader... loaders) {
        _loaders = Arrays.asList(loaders);
    }

    /**
     * Constructs a new CompoundClassLoader.
     * @param loaders the loaders to iterate over
     */
    public CompoundClassLoader(Collection<ClassLoader> loaders) {
        _loaders = loaders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getResource(String name) {
        for (ClassLoader loader : _loaders) {
            if (loader != null) {
                URL resource = loader.getResource(name);
                if (resource != null) {
                    return resource;
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getResourceAsStream(String name) {
        for (ClassLoader loader : _loaders) {
            if (loader != null) {
                InputStream is = loader.getResourceAsStream(name);
                if (is != null) {
                    return is;
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        List<URL> urls = new ArrayList<URL>();
        for (ClassLoader loader : _loaders) {
            if (loader != null) {
                try {
                    Enumeration<URL> resources = loader.getResources(name);
                    while (resources.hasMoreElements()) {
                        URL resource = resources.nextElement();
                        if (resource != null && !urls.contains(resource)) {
                            urls.add(resource);
                        }
                    }
                } catch (IOException ioe) {
                    // ignoring, but to keep checkstyle happy ("Must have at least one statement."):
                    ioe.getMessage();
                }
            }
        }
        return Collections.enumeration(urls);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        for (ClassLoader loader : _loaders) {
            if (loader != null) {
                try {
                    return loader.loadClass(name);
                } catch (ClassNotFoundException cnfe) {
                    // ignoring, but to keep checkstyle happy ("Must have at least one statement."):
                    cnfe.getMessage();
                }
            }
        }
        throw new ClassNotFoundException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // loader.loadClass(name, resolve) is not visible!
        return loadClass(name);
    }

    @Override
    public String toString() {
        return String.format("CompoundClassloader %s", _loaders);
    }
}
