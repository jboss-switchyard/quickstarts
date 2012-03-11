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

package org.switchyard.component.camel;

/**
 * Constants used by Camel component.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public final class CamelConstants {

    /**
     * HTTP scheme.
     */
    public static final String HTTP_SCHEME = "http:";

    /**
     * HTTP GET Method.
     */
    public static final String HTTP_GET_METHOD = "GET";

    /**
     * HTTP POST Method.
     */
    public static final String HTTP_POST_METHOD = "POST";

    /**
     * HTTP PUT Method.
     */
    public static final String HTTP_PUT_METHOD = "PUT";

    /**
     * HTTP DELETE Method.
     */
    public static final String HTTP_DELETE_METHOD = "DELETE";

    /**
     * HTTP HEAD Method.
     */
    public static final String HTTP_HEAD_METHOD = "HEAD";

    /**
     * HTTP OPTIONS Method.
     */
    public static final String HTTP_OPTIONS_METHOD = "OPTIONS";

    /**
     * cxfrs transport scheme.
     */
    public static final String CXFRS_SCHEME = "cxfrs:";

    /**
     * Scheme seperator.
     */
    public static final String SCHEME_SUFFIX = "//";

    /**
     * cxfrs://http://<host>:<port> transport scheme.
     */
    public static final String CXFRS_HTTP_SCHEME = CXFRS_SCHEME + SCHEME_SUFFIX + HTTP_SCHEME + SCHEME_SUFFIX;

    /**
     * cxfrs://http:/// transport scheme.
     */
    public static final String CXFRS_HTTP_NO_HOST_SCHEME = CXFRS_HTTP_SCHEME + "/";

    /**
     * resourceClasses.
     */
    public static final String RESOURCE_CLASSES = "resourceClasses=";

    private CamelConstants() {
        // Utility class
    }
}
