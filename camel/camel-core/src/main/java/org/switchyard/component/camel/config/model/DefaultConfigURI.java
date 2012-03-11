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

package org.switchyard.component.camel.config.model;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A generic Camel configuration URI.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class DefaultConfigURI implements ConfigURI {

    private String _uriString;
    private URI _uri;

    /**
     * Creates a empty configuration uri.
     *
     */
    public DefaultConfigURI() {
    }

    /**
     * Creates a default configuration uri.
     *
     * @param uriString the URI string
     */
    public DefaultConfigURI(String uriString) {
        _uriString = uriString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getURI() {
        if (_uri == null) {
            _uri = parseStringAsURI(_uriString);
        }
        return _uri;
    }

    /**
     * Set the URI wrapped by this object.
     *
     * @param uri the URI
     */
    public void setURI(URI uri) {
        _uri = uri;
    }

    /**
     * Returns the URI string wrapped by this object.
     *
     * @return the uri string
     */
    public String getURIString() {
        return _uriString;
    }

    /**
     * Set the URI string wrapped by this object.
     *
     * @param uriString the URI string
     */
    public void setURIString(String uriString) {
        _uriString = uriString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getScheme() {
        return getURI().getScheme();
    }

    /**
     * Returns a string representation of configuration uri.
     *
     * @return the URI as string
     */
    public String toString() {
        return _uriString;
    }

    protected URI parseStringAsURI(String uriString) {
        try {
            return new URI(uriString);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URI [" + uriString + "] was invalid. Please check the configuration.", e);
        }
    }
}
