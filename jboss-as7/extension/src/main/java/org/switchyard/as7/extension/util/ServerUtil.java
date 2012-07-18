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
 
package org.switchyard.as7.extension.util;

import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.as.web.VirtualHost;
import org.jboss.as.web.WebSubsystemServices;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceRegistry;

/**
 * Utility class for AS7 related functions.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public final class ServerUtil {

    /**
     * The attachment key for web.xml.
     */
    public static final AttachmentKey<JBossWebMetaData> JBOSSWEB_METADATA_KEY = AttachmentKey.create(JBossWebMetaData.class);

    private static ServiceRegistry _registry;
    private static String _host = "default-host";

    private ServerUtil() {
    }

    /**
     * Set the JBoss container's Service Registry.
     * @param registry the ServiceRegistry
     */
    public static void setRegistry(ServiceRegistry registry) {
        _registry = registry;
    }

    /**
     * Get the JBoss container's Service Registry.
     * @return the ServiceRegistry
     */
    public static ServiceRegistry getRegistry() {
        return _registry;
    }

    /**
     * Set the JBoss container's Host Name.
     * @param host the name of the host
     */
    public static void setHostName(String host) {
        _host = host;
    }

    /**
     * Get the JBoss container's Host Name.
     * @return the name of the host
     */
    public static String getHostName() {
        return _host;
    }

    /**
     * Get the JBoss container's VirtualHost.
     * @return the VirtualHost
     */
    public static VirtualHost getDefaultHost() {
        ServiceController<VirtualHost> service = (ServiceController<VirtualHost>)_registry.getService(WebSubsystemServices.JBOSS_WEB_HOST.append(_host));
        return service != null ? service.getValue() : null;
    }
}
