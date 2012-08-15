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
 
package org.switchyard.as7.extension.http;

import java.lang.reflect.InvocationTargetException;
import java.io.File;

import javax.naming.NamingException;

import org.apache.catalina.Host;
import org.apache.catalina.Loader;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.ContextConfig;
import org.apache.tomcat.InstanceManager;
import org.jboss.as.server.ServerEnvironment;
import org.jboss.as.web.deployment.WebCtxLoader;
import org.jboss.logging.Logger;
import org.switchyard.as7.extension.util.ServerUtil;
import org.switchyard.component.http.InboundHandler;
import org.switchyard.component.http.HttpGatewayServlet;
import org.switchyard.component.http.endpoint.Endpoint;
import org.switchyard.component.http.endpoint.EndpointPublisher;

/**
 * Publishes standalone HTTP endpoint.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class JBossWebEndpointPublisher implements EndpointPublisher {

    private static final Logger LOG = Logger.getLogger("org.switchyard");
    private static final String SERVER_TEMP_DIR = System.getProperty(ServerEnvironment.SERVER_TEMP_DIR);
    private static final String SERVLET_NAME = "HttpGatewayServlet";

    /**
     * {@inheritDoc}
     */
    public Endpoint publish(String context, InboundHandler handler) throws Exception {
        
        Host host = ServerUtil.getDefaultHost().getHost();
        StandardContext serverContext = (StandardContext) host.findChild("/" + context);
        if (serverContext == null) {
            serverContext = new StandardContext();
            serverContext.setPath("/" + context);
            File docBase = new File(SERVER_TEMP_DIR, context);
            if (!docBase.exists()) {
                if(!docBase.mkdirs()) {
                    throw new RuntimeException("Unable to create temp directory " + docBase.getPath());
                }
            }
            serverContext.setDocBase(docBase.getPath());
            serverContext.addLifecycleListener(new ContextConfig());

            final Loader loader = new WebCtxLoader(Thread.currentThread().getContextClassLoader());
            loader.setContainer(host);
            serverContext.setLoader(loader);
            serverContext.setInstanceManager(new LocalInstanceManager());

            Wrapper wrapper = serverContext.createWrapper();
            wrapper.setName(SERVLET_NAME);
            wrapper.setServletClass(HttpGatewayServlet.class.getName());
            //wrapper.setServlet(new HttpGatewayServlet(handler));
            wrapper.setLoadOnStartup(1);
            serverContext.addChild(wrapper);
            serverContext.addServletMapping("/*", SERVLET_NAME);

            host.addChild(serverContext);
            serverContext.create();
            serverContext.start();
            HttpGatewayServlet instance = (HttpGatewayServlet) wrapper.getServlet();
            instance.setHandler(handler);
            LOG.info("Published HTTP context " + serverContext.getPath());
        } else {
            throw new RuntimeException("Context " + context + " already exists!");
        }
        return new JBossWebEndpoint(serverContext);
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
}
