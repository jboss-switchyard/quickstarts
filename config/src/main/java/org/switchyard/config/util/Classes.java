/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.config.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Classes.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Classes {

    private Classes() {}

    public static Class<?> forName(String name) {
        return forName(name, (ClassLoader)null);
    }

    public static Class<?> forName(String name, Class<?> caller) {
        return forName(name, caller != null ? caller.getClassLoader() : null);
    }

    public static Class<?> forName(String name, ClassLoader loader) {
        Class<?> c = null;
        List<ClassLoader> loaders = getClassLoaders(loader);
        for (ClassLoader cl : loaders) {
            try {
                c = Class.forName(name, true, cl);
            } catch (Throwable t) {
                // ignoring, but to keep checkstyle happy ("Must have at least one statement."):
                t.getMessage();
            }
        }
        return c;
    }

    public static URL getResource(String path) throws IOException {
        return getResource(path, (ClassLoader)null);
    }

    public static URL getResource(String path, Class<?> caller) throws IOException {
        return getResource(path, caller != null ? caller.getClassLoader() : null);
    }

    public static URL getResource(String path, ClassLoader loader) throws IOException {
        List<URL> urls = getResources(path, loader);
        return urls.size() > 0 ? urls.get(0) : null;
    }

    public static List<URL> getResources(String path) throws IOException {
        return getResources(path, (ClassLoader)null);
    }

    public static List<URL> getResources(String path, Class<?> caller) throws IOException {
        return getResources(path, caller != null ? caller.getClassLoader() : null);
    }

    public static List<URL> getResources(String path, ClassLoader loader) throws IOException {
        List<URL> urls = new ArrayList<URL>();
        if (path != null) {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            for (ClassLoader cl : getClassLoaders(loader)) {
                Enumeration<URL> e = cl.getResources(path);
                while (e.hasMoreElements()) {
                    urls.add(e.nextElement());
                }
            }
        }
        return urls;
    }

    public static InputStream getResourceAsStream(String path) throws IOException {
        return getResourceAsStream(path, (ClassLoader)null);
    }

    public static InputStream getResourceAsStream(String path, Class<?> caller) throws IOException {
        return getResourceAsStream(path, caller != null ? caller.getClassLoader() : null);
    }

    public static InputStream getResourceAsStream(String path, ClassLoader loader) throws IOException {
        URL url = getResource(path, loader);
        return url != null ? url.openStream() : null;
    }

    private static List<ClassLoader> getClassLoaders(ClassLoader loader) {
        List<ClassLoader> loaders = new ArrayList<ClassLoader>(4);
        ClassLoader cl = getTCCL();
        if (cl != null) {
            loaders.add(cl);
        }
        if (loader != null) {
            loaders.add(loader);
        }
        cl = Classes.class.getClassLoader();
        if (cl != null) {
            loaders.add(cl);
        }
        cl = Class.class.getClassLoader();
        if (cl != null) {
            loaders.add(cl);
        }
        return loaders;
    }

    public static ClassLoader getTCCL() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static ClassLoader setTCCL(ClassLoader replacement) {
        Thread thread = Thread.currentThread();
        ClassLoader previous = thread.getContextClassLoader();
        thread.setContextClassLoader(replacement);
        return previous;
    }

}
