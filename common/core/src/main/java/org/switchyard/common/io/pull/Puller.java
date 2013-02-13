/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.common.io.pull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.switchyard.common.io.resource.Resource;
import org.switchyard.common.type.Classes;

/**
 * Utility class to safely access ("pull") resources from various sources.
 * 
 * @param <R> the type of the resource being pulled
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class Puller<R> {

    /**
     * Safely pulls a resource from the class path using {@link org.switchyard.common.type.Classes#getResourceAsStream(String, Class)}.
     * @param resource the path to the resource
     * @return the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public R pull(String resource) throws IOException {
        return pull(resource, getClass());
    }

    /**
     * Safely pulls a resource from the class path using {@link org.switchyard.common.type.Classes#getResourceAsStream(String, Class)}.
     * @param resource the path to the resource
     * @param caller class calling this method, so we can also try it's classloader
     * @return the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public R pull(String resource, Class<?> caller) throws IOException {
        InputStream is = Classes.getResourceAsStream(resource, caller);
        if (is != null) {
            try {
                return pull(is);
            } finally {
                is.close();
            }
        }
        return null;
    }

    /**
     * Safely pulls a resource from the class path using {@link org.switchyard.common.type.Classes#getResourceAsStream(String, ClassLoader)}.
     * @param resource the path to the resource
     * @param loader the classloader we can also try to find the resource
     * @return the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public R pull(String resource, ClassLoader loader) throws IOException {
        InputStream is = Classes.getResourceAsStream(resource, loader);
        if (is != null) {
            try {
                return pull(is);
            } finally {
                is.close();
            }
        }
        return null;
    }

    /**
     * Safely pulls a resource from a URI.
     * @param uri the URI to the resource
     * @return the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public R pull(URI uri) throws IOException {
        return pull(uri.toURL());
    }

    /**
     * Safely pulls a resource from a URL.
     * @param url the URL to the resource
     * @return the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public R pull(URL url) throws IOException {
        InputStream is = url.openStream();
        try {
            return pull(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * Safely pulls a resource from a Resource.
     * @param resource the Resource
     * @return the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public R pull(Resource resource) throws IOException {
        return pull(resource, getClass());
    }

    /**
     * Safely pulls a resource from a Resource, using the specified caller class.
     * @param resource the Resource
     * @param caller the class calling this method
     * @return the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public R pull(Resource resource, Class<?> caller) throws IOException {
        return pull(resource.getLocationURL(caller));
    }

    /**
     * Safely pulls a resource from a Resource, using the specified classloader.
     * @param resource the Resource
     * @param loader the classloader to check with
     * @return the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public R pull(Resource resource, ClassLoader loader) throws IOException {
        return pull(resource.getLocationURL(loader));
    }

    /**
     * Safely pulls a resource from a File.
     * @param file the resource File
     * @return the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public R pull(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        try {
            return pull(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * Safely pulls a resource from an InputStream.
     * @param stream an InputStream of the resource
     * @return the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public abstract R pull(InputStream stream) throws IOException;

    /**
     * Known path types.
     */
    public enum PathType {
        /** A class path. */
        CLASS,
        /** A file path. */
        FILE,
        /** A URI path. */
        URI,
        /** A URL path. */
        URL
    }

    /**
     * Safely pulls a resource from a path, iterating over the specified path types until it finds it.
     * @param path the path
     * @param pathTypes the types of paths to try
     * @return the resource, or null if not found
     */
    public R pullPath(String path, PathType... pathTypes) {
        return pullPath(path, getClass(), pathTypes);
    }

    /**
     * Safely pulls a resource from a path, iterating over the specified path types until it finds it.
     * @param path the path
     * @param caller class calling this method, so we can also try it's classloader
     * @param pathTypes the types of paths to try
     * @return the resource, or null if not found
     */
    public R pullPath(String path, Class<?> caller, PathType... pathTypes) {
        return pullPath(path, caller, null, true, pathTypes);
    }

    /**
     * Safely pulls a resource from a path, iterating over the specified path types until it finds it.
     * @param path the path
     * @param loader the classloader we can also try to find the resource
     * @param pathTypes the types of paths to try
     * @return the resource, or null if not found
     */
    public R pullPath(String path, ClassLoader loader, PathType... pathTypes) {
        return pullPath(path, null, loader, false, pathTypes);
    }

    private R pullPath(String path, Class<?> caller, ClassLoader loader, boolean callerOrLoader, PathType[] pathTypes) {
        if (path == null) {
            return null;
        }
        R r = null;
        for (PathType pathType : pathTypes) {
            if (pathType == null) {
                continue;
            }
            try {
                switch (pathType) {
                    case CLASS:
                        if (callerOrLoader) {
                            r = pull(path, caller);
                        } else {
                            r = pull(path, loader);
                        }
                        break;
                    case FILE:
                        File file;
                        try {
                            file = new File(new URL(path).getFile());
                        } catch (MalformedURLException mue) {
                            file = new File(path);
                        }
                        if (file.exists() && file.isFile()) {
                            r = pull(file);
                        }
                        break;
                    case URI:
                        r = pull(new URI(path));
                        break;
                    case URL:
                        r = pull(new URL(path));
                        break;
                }
            } catch (Throwable t) {
                // keep checkstyle happy
                t.getMessage();
            }
            if (r != null) {
                break;
            }
        }
        return r;
    }

}
