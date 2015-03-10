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

import java.util.Set;

import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.as.web.host.WebHost;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceRegistry;
import org.wildfly.extension.undertow.Host;
import org.wildfly.extension.undertow.ListenerService;
import org.wildfly.extension.undertow.Server;
import org.wildfly.extension.undertow.UndertowService;

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
    private static String _server = "default-server";
    private static String _listener = "default";

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
     * Get the JBoss container's WebHost.
     * @return the WebHost
     */
    public static WebHost getDefaultHost() {
        ServiceController<WebHost> service = (ServiceController<WebHost>)_registry.getService(WebHost.SERVICE_NAME.append(_host));
        return service != null ? service.getValue() : null;
    }

    /**
     * Get the JBoss container's DefaultConnector.
     * @return the DefaultConnector
     */
    public static ListenerService getDefaultListener() {
        ServiceController<Server> service = (ServiceController<Server>)_registry.getService(UndertowService.SERVER.append(_server));
        if (service != null) {
            Server server = service.getValue();
            for (ListenerService<?> listener : server.getListeners()) {
                if (listener.getName().equals(_listener)) {
                    return listener;
                }
            }
        }
        return null;
    }

    public static Set<String> getDefaultHostAliases() {
        ServiceController<Server> service = (ServiceController<Server>)_registry.getService(UndertowService.SERVER.append(_server));
        if (service != null) {
            Server server = service.getValue();
            for(Host host : server.getHosts()) {
                if (host.getName().equals(server.getDefaultHost())) {
                    return host.getAllAliases();
                }
            }
        }
        return null;
    }
}
