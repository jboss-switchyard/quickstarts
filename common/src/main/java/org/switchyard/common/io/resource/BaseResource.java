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
        return getURL(getLocation(), getClass());
    }

    /**
     * Gets a URL with the specified location.
     * @param location the specified location
     * @return the URL
     */
    public static URL getURL(String location) {
        return getURL(location, BaseResource.class);
    }

    /**
     * Gets a URL with the specified location and calling class.
     * @param location the specified location
     * @param caller the calling class
     * @return the URL
     */
    public static URL getURL(String location, Class<?> caller) {
        if (location != null) {
            try {
                if (location.startsWith("http:") || location.startsWith("https:")) {
                    return new URL(location);
                }
                if (location.startsWith("file:")) {
                    return new File(location.substring(5)).toURI().toURL();
                }
                return Classes.getResource(location, caller);
            } catch (IOException ioe) {
                return null;
            }
        }
        return null;
    }
}
