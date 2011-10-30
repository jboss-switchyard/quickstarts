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
