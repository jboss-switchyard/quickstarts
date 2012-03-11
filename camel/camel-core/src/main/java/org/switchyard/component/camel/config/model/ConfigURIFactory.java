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
 * Creates Camel ConfigURI objects based on uri string.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public final class ConfigURIFactory {

    /** 
     * No need to directly instantiate.
     */
    private ConfigURIFactory() {
    }

    /**
     * Construct a ConfigURI based on URI.
     *
     * @param uri the URI string
     * @param socketAddr the SocketAddr
     * @return a ConfigURI object
     */
    public static ConfigURI newConfigURI(String uri, SocketAddr socketAddr) {
        if ((uri != null) && uri.startsWith(CamelConstants.CXFRS_SCHEME)) {
            return new CxfRsConfigURI(uri, socketAddr);
        }
        return new DefaultConfigURI(uri);
    }
}
