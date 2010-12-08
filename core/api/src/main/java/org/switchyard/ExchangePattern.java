/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard;

/**
 * The list of supported exchange patterns within SwitchYard.
 */
public enum ExchangePattern {

    /**
     * A message exchange consisting of a single request message.
     */
    IN_ONLY ("http://www.w3.org/ns/wsdl/in-only"),
    /**
     * A message exchange consisting of a single request message and a single
     * response message.
     */
    IN_OUT ("http://www.w3.org/ns/wsdl/in-out");

    /**
     * String representation of the pattern URI.
     */
    private String _patternURI;

    /**
     * Creates a new ExchangePattern instance with the specified URI string.
     * @param uri a URI uniquely identifying the exchange pattern
     */
    ExchangePattern(final String uri) {
        _patternURI = uri;
    }

    /**
     * Returns the string representation of the ExchangePattern instance.
     * @return exchange pattern URI
     */
    public String getURI() {
        return _patternURI;
    }

    /**
     * Converts an exchange pattern URI string into an instance of this enum.
     * @param uri exchange pattern URI
     * @return an instane of ExchangePattern corresponding to the specified
     * pattern URI.
     */
    public static ExchangePattern fromURI(final String uri) {
        if (IN_ONLY.getURI().equals(uri)) {
            return IN_ONLY;
        } else if (IN_OUT.getURI().equals(uri)) {
            return IN_OUT;
        } else {
            throw new IllegalArgumentException("Unrecognized URI: " + uri);
        }
    }
}
