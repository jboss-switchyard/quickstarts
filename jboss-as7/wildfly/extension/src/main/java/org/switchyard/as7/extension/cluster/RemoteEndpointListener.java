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
 
package org.switchyard.as7.extension.cluster;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.jboss.as.server.ServerEnvironment;
import org.jboss.as.web.host.ServletBuilder;
import org.jboss.as.web.host.WebDeploymentBuilder;
import org.jboss.as.web.host.WebDeploymentController;
import org.jboss.as.web.host.WebHost;
import org.jboss.logging.Logger;
import org.switchyard.ServiceDomain;
import org.switchyard.as7.extension.ExtensionLogger;
import org.switchyard.as7.extension.ExtensionMessages;
import org.switchyard.as7.extension.util.ServerUtil;
import org.switchyard.component.sca.RemoteEndpointPublisher;
import org.switchyard.component.sca.SwitchYardRemotingServlet;

/**
 * Publishes standalone HTTP endpoint.
 */
public class RemoteEndpointListener implements RemoteEndpointPublisher {

    private static final String SERVER_TEMP_DIR = System.getProperty(ServerEnvironment.SERVER_TEMP_DIR);
    private static final String SERVLET_NAME = "SwitchYardRemotingServlet";
    
    private static Logger _log = Logger.getLogger(RemoteEndpointListener.class);
    
    private String _contextName;
    private WebDeploymentController _handle;
    private Map<QName, ServiceDomain> _services = new ConcurrentHashMap<QName, ServiceDomain>();
    
    private boolean _started;

    /**
     * Constructor.
     */
    public RemoteEndpointListener() {
    }

    @Override
    public void init(String context) {
        _contextName = context;
    }

    @Override
    public synchronized void start() throws Exception {
        // If the remote listener is already started, just return.
        if (_started) {
            return;
        }
        
        WebHost host = ServerUtil.getDefaultHost();
        WebDeploymentBuilder deployment = new WebDeploymentBuilder();
        ServletBuilder servletBuilder = new ServletBuilder();
        try {
            deployment.setContextRoot(_contextName);
            File docBase = new File(SERVER_TEMP_DIR, _contextName);
            if (!docBase.exists()) {
                if (!docBase.mkdirs()) {
                    throw ExtensionMessages.MESSAGES.unableToCreateTempDirectory(docBase.getPath());
                }
            }
            deployment.setDocumentRoot(docBase);
            deployment.setClassLoader(Thread.currentThread().getContextClassLoader());

            List<String> urlPatterns = new ArrayList<String>();
            urlPatterns.add("/*");
            servletBuilder.addUrlMappings(urlPatterns);

            servletBuilder.setServletName(SERVLET_NAME);
            servletBuilder.setServletClass(SwitchYardRemotingServlet.class);
            servletBuilder.setForceInit(true);

            SwitchYardRemotingServlet remotingServlet = new SwitchYardRemotingServlet();
            remotingServlet.setEndpointPublisher(this);
            servletBuilder.setServlet(remotingServlet);
            deployment.addServlet(servletBuilder);

            _handle = host.addWebDeployment(deployment);
            _handle.create();
            _handle.start();
            _started = true;
            _log.info("Published Remote Service Endpoint " + _contextName);
        } catch (Exception e) {
            throw ExtensionMessages.MESSAGES.unableToStartContext(_contextName, e);
        }
    }
    
    @Override
    public synchronized void stop() throws Exception {
        if (_handle != null) {
            try {
                _handle.stop();
                _handle.destroy();
                _log.info("Destroyed HTTP context " + _contextName);
                _started = false;
            } catch (Exception e) {
                ExtensionLogger.ROOT_LOGGER.unableToDestroyWebContext(_contextName, e);
            }
        }
    }

    /**
     * Return the address.
     * 
     * @return An address string
     */
    public String getAddress() {
        String hostAddress = null;
        // TODO::
        /*Connector connector = ServerUtil.getDefaultConnector();
        if (connector.getProtocolHandler() instanceof Http11Protocol) {
            Http11Protocol protocol = (Http11Protocol) connector.getProtocolHandler();
            InetAddress address = protocol.getAddress();
            hostAddress = address.getHostAddress();
         } else {
             ExtensionLogger.ROOT_LOGGER.unableToDetermineHostAddress();
             hostAddress = ServerUtil.getDefaultHost().getHost().findAliases()[0];
         }
        return connector.getScheme() + "://" + hostAddress + ":" + connector.getPort() + "/" + _contextName;
        */
        return _contextName;
    }

    @Override
    public void addService(QName serviceName, ServiceDomain domain) {
        _services.put(serviceName, domain);
    }

    @Override
    public void removeService(QName serviceName, ServiceDomain domain) {
        _services.remove(serviceName);
    }
    
    @Override
    public ServiceDomain getDomain(QName serviceName) {
        return _services.get(serviceName);
    }

}
