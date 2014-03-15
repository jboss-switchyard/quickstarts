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
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;
import javax.xml.namespace.QName;

import org.apache.catalina.Container;
import org.apache.catalina.Host;
import org.apache.catalina.Loader;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.ContextConfig;
import org.apache.coyote.http11.Http11Protocol;
import org.apache.tomcat.InstanceManager;
import org.jboss.as.server.ServerEnvironment;
import org.jboss.as.web.deployment.WebCtxLoader;
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
    private StandardContext _serverContext;
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
        
        Host host = ServerUtil.getDefaultHost().getHost();
        _serverContext = (StandardContext) host.findChild("/" + _contextName);
        if (_serverContext == null) {
            _serverContext = new StandardContext();
            _serverContext.setPath("/" + _contextName);
            File docBase = new File(SERVER_TEMP_DIR, _contextName);
            if (!docBase.exists()) {
                if (!docBase.mkdirs()) {
                    throw ExtensionMessages.MESSAGES.unableToCreateTempDirectory(docBase.getPath());
                }
            }
            _serverContext.setDocBase(docBase.getPath());
            _serverContext.addLifecycleListener(new ContextConfig());

            final Loader loader = new WebCtxLoader(Thread.currentThread().getContextClassLoader());
            loader.setContainer(host);
            _serverContext.setLoader(loader);
            _serverContext.setInstanceManager(new LocalInstanceManager());

            Wrapper wrapper = _serverContext.createWrapper();
            wrapper.setName(SERVLET_NAME);
            wrapper.setServletClass(SwitchYardRemotingServlet.class.getName());
            wrapper.setLoadOnStartup(1);
            _serverContext.addChild(wrapper);
            _serverContext.addServletMapping("/*", SERVLET_NAME);
            

            host.addChild(_serverContext);
            _serverContext.create();
            _serverContext.start();
            

            SwitchYardRemotingServlet remotingServlet = (SwitchYardRemotingServlet) wrapper.getServlet();
            remotingServlet.setEndpointPublisher(this);
            _log.info("Published Remote Service Endpoint " + _serverContext.getPath());
            
            _started = true;
        } else {
            throw ExtensionMessages.MESSAGES.contextAlreadyExists(_contextName);
        }
    }
    
    @Override
    public synchronized void stop() throws Exception {
        if (_serverContext != null) {
            // Destroy the web context unless if it is default
            if (!_serverContext.getPath().equals("/")) {
                try {
                    Container container = _serverContext.getParent();
                    container.removeChild(_serverContext);
                    _serverContext.stop();
                    _serverContext.destroy();
                    _log.info("Destroyed HTTP context " + _serverContext.getPath());
                } catch (Exception e) {
                    ExtensionLogger.ROOT_LOGGER.unableToDestroyWebContext(_contextName, e);
                }
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
        Connector connector = ServerUtil.getDefaultConnector();
        if (connector.getProtocolHandler() instanceof Http11Protocol) {
            Http11Protocol protocol = (Http11Protocol) connector.getProtocolHandler();
            InetAddress address = protocol.getAddress();
            hostAddress = address.getHostAddress();
         } else {
             ExtensionLogger.ROOT_LOGGER.unableToDetermineHostAddress();
             hostAddress = ServerUtil.getDefaultHost().getHost().findAliases()[0];
         }
        
        return connector.getScheme() + "://" + hostAddress + ":" + connector.getPort() + "/" + _contextName;
    }

    private static class LocalInstanceManager implements InstanceManager {
        LocalInstanceManager() {
        }
        @Override
        public Object newInstance(String className) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException {
            return Class.forName(className).newInstance();
        }

        @Override
        public Object newInstance(String fqcn, ClassLoader classLoader) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException {
            return Class.forName(fqcn, false, classLoader).newInstance();
        }

        @Override
        public Object newInstance(Class<?> c) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException {
            return c.newInstance();
        }

        @Override
        public void newInstance(Object o) throws IllegalAccessException, InvocationTargetException, NamingException {
            throw new IllegalStateException();
        }

        @Override
        public void destroyInstance(Object o) throws IllegalAccessException, InvocationTargetException {
        }
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
