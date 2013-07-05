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
package org.switchyard.common.io.resource;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.switchyard.common.type.Classes;

/**
 * BaseResource.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseResource implements Resource {

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getLocationURL() {
        return getLocationURL(getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getLocationURL(Class<?> caller) {
        return getURL(getLocation(), caller);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getLocationURL(ClassLoader loader) {
        return getURL(getLocation(), loader);
    }

    /**
     * Gets a URL with the specified location and calling class.
     * @param location the specified location
     * @param caller the calling class to use it's classloader
     * @return the URL
     */
    public static final URL getURL(String location, Class<?> caller) {
        return getURL(location, caller, null);
    }

    /**
     * Gets a URL with the specified location and calling class.
     * @param location the specified location
     * @param loader the classloader to check with
     * @return the URL
     */
    public static final URL getURL(String location, ClassLoader loader) {
        return getURL(location, null, loader);
    }

    private static final URL getURL(String location, Class<?> caller, ClassLoader loader) {
        if (location != null) {
            try {
                if (location.startsWith("http:") || location.startsWith("https:")) {
                    return new URL(location);
                }
                if (location.startsWith("file:")) {
                    return new File(location.substring(5)).toURI().toURL();
                }
                if (caller != null) {
                    return Classes.getResource(location, caller);
                }
                if (loader != null) {
                    return Classes.getResource(location, loader);
                }
                return Classes.getResource(location, BaseResource.class);
            } catch (IOException ioe) {
                return null;
            }
        }
        return null;
    }

}
