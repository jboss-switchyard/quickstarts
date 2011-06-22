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
package org.switchyard.component.camel;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.camel.impl.DefaultPackageScanClassResolver;

/**
 * JBoss specific package scan class resolver to be used when Camel is running
 * inside JBoss Application Server 7.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class JBossPackageScanClassResolver extends DefaultPackageScanClassResolver {

    @Override
    protected URL customResourceLocator(URL url) throws IOException {
        // Fix URL Encoded paths encountered in Windows platforms
        String urlPath = url.getFile();
        urlPath = URLDecoder.decode(urlPath, "UTF-8");
        return new URL(url.getProtocol(), url.getHost(), url.getPort(), urlPath);
    }
}
