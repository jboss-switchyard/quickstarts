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
 
package org.switchyard.as7.extension.resteasy;

import java.lang.reflect.InvocationTargetException;
import java.io.File;
import java.util.List;

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
import org.jboss.resteasy.spi.Registry;
import org.switchyard.as7.extension.util.ServerUtil;
import org.switchyard.component.resteasy.resource.Resource;
import org.switchyard.component.resteasy.resource.ResourcePublisher;

/**
 * Creates a RESTEasy resource on AS7.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class RESTEasyResourcePublisher implements ResourcePublisher {

    private static final Logger LOG = Logger.getLogger("org.switchyard");
    private static final String LISTENER_CLASS = "org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap";
    private static final String SERVER_TEMP_DIR = System.getProperty(ServerEnvironment.SERVER_TEMP_DIR);
    private static final String SERVLET_NAME = "RestEasy";
    private static final String SERVLET_CLASS = "org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher";

    /**
     * {@inheritDoc}
     */
    public Resource publish(String context, List<Object> instances) throws Exception {
        Host host = ServerUtil.getDefaultHost().getHost();
        StandardContext serverContext = (StandardContext) host.findChild("/" + context);
        if (serverContext == null) {
            serverContext = new StandardContext();
            serverContext.setPath("/" + context);
            File docBase = new File(SERVER_TEMP_DIR, context);
            if (!docBase.exists()) {
                docBase.mkdirs();
            }
            serverContext.setDocBase(docBase.getPath());
            serverContext.addLifecycleListener(new ContextConfig());

            final Loader loader = new WebCtxLoader(instances.get(0).getClass().getClassLoader());
            loader.setContainer(host);
            serverContext.setLoader(loader);
            serverContext.setInstanceManager(new LocalInstanceManager());

            Wrapper wrapper = serverContext.createWrapper();
            wrapper.setName(SERVLET_NAME);
            wrapper.setServletClass(SERVLET_CLASS);
            wrapper.setLoadOnStartup(1);
            serverContext.addChild(wrapper);
            serverContext.addServletMapping("/*", SERVLET_NAME);
            serverContext.addApplicationListener(LISTENER_CLASS);

            host.addChild(serverContext);
            serverContext.create();
            serverContext.start();
        }
        Registry registry = (Registry)serverContext.getServletContext().getAttribute(Registry.class.getName());
        // Add as singleton instance
        for (Object instance : instances) {
            LOG.debug("Publishing ... " + instance);
            registry.addSingletonResource(instance);
        }
        return new RESTEasyResource();
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
