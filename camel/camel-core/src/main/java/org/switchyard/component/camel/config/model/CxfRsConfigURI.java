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

import org.switchyard.common.net.SocketAddr;
import org.switchyard.component.camel.CamelConstants;

/**
 * A cxfrs Camel configuration URI.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class CxfRsConfigURI extends DefaultConfigURI {

    /**
     * Creates a CxfRs configuration uri.
     *
     * @param uri the URI as string
     * @param socketAddr the SocketAddr
     * @param contextPath the default context path
     */
    public CxfRsConfigURI(String uri, SocketAddr socketAddr, String contextPath) {
        if (uri.startsWith(CamelConstants.CXFRS_HTTP_NO_HOST_SCHEME)) {
            if (contextPath != null) {
                setURIString(CamelConstants.CXFRS_HTTP_SCHEME + socketAddr + contextPath + uri.substring(CamelConstants.CXFRS_HTTP_SCHEME.length()));
            } else {
                setURIString(CamelConstants.CXFRS_HTTP_SCHEME + socketAddr + uri.substring(CamelConstants.CXFRS_HTTP_SCHEME.length()));
            }
        } else {
            setURIString(uri);
        }
    }
}
