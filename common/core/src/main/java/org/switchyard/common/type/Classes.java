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
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class to properly load classes and find resources.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Classes {

    private Classes() {}

    /**
     * Loads a class based on name.
     * @param name fully qualified classname
     * @return the found class, or null if not found
     */
    public static Class<?> forName(String name) {
        return forName(name, (ClassLoader)null);
    }

    /**
     * Loads a class based on name.
     * @param name fully qualified classname
     * @param caller class calling this method, so we can also try it's classloader
     * @return the found class, or null if not found
     */
    public static Class<?> forName(String name, Class<?> caller) {
        return forName(name, caller != null ? caller.getClassLoader() : null);
    }

    /**
     * Loads a class based on name.
     * @param name fully qualified classname
     * @param loader a classloader we can also try to find the class
     * @return the found class, or null if not found
     */
    public static Class<?> forName(String name, ClassLoader loader) {
        Class<?> c = null;
        List<ClassLoader> loaders = getClassLoaders(loader);
        for (ClassLoader cl : loaders) {
            try {
                c = Class.forName(name, true, cl);
                break;
            } catch (Throwable t) {
                // ignoring, but to keep checkstyle happy ("Must have at least one statement."):
                t.getMessage();
            }
        }
        return c;
    }

    /**
     * Finds a classpath resource.
     * @param path the path to the resource
     * @return URL to the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public static URL getResource(String path) throws IOException {
        return getResource(path, (ClassLoader)null);
    }

    /**
     * Finds a classpath resource.
     * @param path the path to the resource
     * @param caller class calling this method, so we can also try it's classloader
     * @return URL to the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public static URL getResource(String path, Class<?> caller) throws IOException {
        ClassLoader loader = caller != null ? caller.getClassLoader() : null;
        URL url = getResource(path, loader);
        if (url == null) {
            String callerPath = callerPath(path, caller);
            if (callerPath != null) {
                url = getResource(callerPath, loader);
            }
        }
        return url;
    }

    /**
     * Finds a classpath resource.
     * @param path the path to the resource
     * @param loader classloader we can also try to find the resource
     * @return URL to the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public static URL getResource(String path, ClassLoader loader) throws IOException {
        List<URL> urls = getResources(path, loader);
        return urls.size() > 0 ? urls.get(0) : null;
    }

    /**
     * Finds all matching classpath resources.
     * @param path the path to the resource
     * @return URL to the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public static List<URL> getResources(String path) throws IOException {
        return getResources(path, (ClassLoader)null);
    }

    /**
     * Finds all matching classpath resources.
     * @param path the path to the resource
     * @param caller class calling this method, so we can also try it's classloader
     * @return URL to the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public static List<URL> getResources(String path, Class<?> caller) throws IOException {
        ClassLoader loader = caller != null ? caller.getClassLoader() : null;
        List<URL> urls = getResources(path, loader);
        String callerPath = callerPath(path, caller);
        if (callerPath != null) {
            urls.addAll(getResources(callerPath, loader)); 
        }
        return urls;
    }

    /**
     * Finds all matching classpath resources.
     * @param path the path to the resource
     * @param loader classloader we can also try to find the resource
     * @return URL to the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public static List<URL> getResources(String path, ClassLoader loader) throws IOException {
        List<URL> urls = new ArrayList<URL>();
        if (path != null) {
            while (path.startsWith("/")) {
                path = path.substring(1);
            }
            for (ClassLoader cl : getClassLoaders(loader)) {
                Enumeration<URL> e = cl.getResources(path);
                while (e.hasMoreElements()) {
                    URL url = e.nextElement();
                    if (!urls.contains(url)) {
                        urls.add(url);
                    }
                }
            }
        }
        return urls;
    }

    /**
     * Finds a classpath resource as an InputStream.
     * @param path the path to the resource
     * @return InputStream of the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public static InputStream getResourceAsStream(String path) throws IOException {
        return getResourceAsStream(path, (ClassLoader)null);
    }

    /**
     * Finds a classpath resource as an InputStream.
     * @param path the path to the resource
     * @param caller class calling this method, so we can also try it's classloader
     * @return InputStream of the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public static InputStream getResourceAsStream(String path, Class<?> caller) throws IOException {
        ClassLoader loader = caller != null ? caller.getClassLoader() : null;
        InputStream in = getResourceAsStream(path, loader);
        if (in == null) {
            String callerPath = callerPath(path, caller);
            if (callerPath != null) {
                in = getResourceAsStream(callerPath, loader);
            }
        }
        return in;
    }

    /**
     * Finds a classpath resource as an InputStream.
     * @param path the path to the resource
     * @param loader classloader we can also try to find the resource
     * @return InputStream of the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public static InputStream getResourceAsStream(String path, ClassLoader loader) throws IOException {
        URL url = getResource(path, loader);
        return url != null ? url.openStream() : null;
    }

    /**
     * Provides a CompoundClassLoader using a non-duplicate List of ClassLoaders in the most appropriate search order.
     * @return the CompoundClassLoader
     */
    public static ClassLoader getClassLoader() {
        return new CompoundClassLoader(getClassLoaders());
    }

    /**
     * Provides a CompoundClassLoader using a non-duplicate List of ClassLoaders in the most appropriate search order.
     * @param caller class calling this method, so we can also try it's classloader
     * @return the CompoundClassLoader
     */
    public static ClassLoader getClassLoader(Class<?> caller) {
        return new CompoundClassLoader(getClassLoaders(caller));
    }

    /**
     * Provides a CompoundClassLoader using a non-duplicate List of ClassLoaders in the most appropriate search order.
     * @param loader a user-specified ClassLoader
     * @return the CompoundClassLoader
     */
    public static ClassLoader getClassLoader(ClassLoader loader) {
        return new CompoundClassLoader(getClassLoaders(loader));
    }

    /**
     * Provides a non-duplicate List of ClassLoaders in the most appropriate search order.
     * @return the non-duplicate List
     */
    public static List<ClassLoader> getClassLoaders() {
        return getClassLoaders((ClassLoader)null);
    }

    /**
     * Provides a non-duplicate List of ClassLoaders in the most appropriate search order.
     * @param caller class calling this method, so we can also try it's classloader
     * @return the non-duplicate List
     */
    public static List<ClassLoader> getClassLoaders(Class<?> caller) {
        return getClassLoaders(caller != null ? caller.getClassLoader() : null);
    }

    /**
     * Provides a non-duplicate List of ClassLoaders in the most appropriate search order.
     * @param loader a user-specified ClassLoader
     * @return the non-duplicate List
     */
    public static List<ClassLoader> getClassLoaders(ClassLoader loader) {
        // We won't ever have more than 5 ClassLoaders, so an initial
        // capacity of 6 and a load factor of 1 means no re-hash ever.
        Set<ClassLoader> loaders = new LinkedHashSet<ClassLoader>(6, 1f);
        // 1. The current Thread context ClassLoader
        ClassLoader cl = getTCCL();
        if (cl != null) {
            loaders.add(cl);
        }
        // 2. The specified ClassLoader
        if (loader != null) {
            loaders.add(loader);
        }
        // 3. This util class' ClassLoader
        cl = Classes.class.getClassLoader();
        if (cl != null) {
            loaders.add(cl);
        }
        // 4. The system ClassLoader (possibly via -Djava.system.class.loader)
        cl = ClassLoader.getSystemClassLoader();
        if (cl != null) {
            loaders.add(cl);
        }
        // 5. The runtime ClassLoader (possibly different than the system ClassLoader)
        cl = Class.class.getClassLoader();
        if (cl != null) {
            loaders.add(cl);
        }
        return new ArrayList<ClassLoader>(loaders);
    }

    /**
     * Shorthand method to get the current Thread's Context ClassLoader.
     * @return the current Thread's Context ClassLoader
     */
    public static ClassLoader getTCCL() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * Shorthand method to set the current Thread's Context ClassLoader.
     * @param replacement the ClassLoader to set on the current Thread
     * @return the ClassLoader that was previously associated with the current Thread, so it can be set back in a finally block, for example.
     */
    public static ClassLoader setTCCL(ClassLoader replacement) {
        Thread thread = Thread.currentThread();
        ClassLoader previous = thread.getContextClassLoader();
        thread.setContextClassLoader(replacement);
        return previous;
    }

    private static String callerPath(String path, Class<?> caller) {
        String callerPath = null;
        if (path != null && caller != null) {
            String pkg = caller.getPackage().getName().replace('.', '/');
            if (!path.contains(pkg)) {
                callerPath = pkg + "/" + path;
            }
        }
        return callerPath;
    }

}
