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
 
package org.switchyard.as7.extension.util;

import org.apache.catalina.connector.Connector;
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
    
    /**
     * Get the JBoss container's DefaultConnector.
     * @return the DefaultConnector
     */
    public static Connector getDefaultConnector() {
        ServiceController<Connector> service = (ServiceController<Connector>)_registry.getService(
                WebSubsystemServices.JBOSS_WEB_CONNECTOR.append("http"));
        return service != null ? service.getValue() : null;
    }
}
