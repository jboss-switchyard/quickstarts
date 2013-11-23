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
 
package org.switchyard.as7.extension.resteasy;

import java.lang.reflect.InvocationTargetException;
import java.io.File;
import java.util.ArrayList;
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
import org.switchyard.as7.extension.ExtensionMessages;
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
    public synchronized Resource publish(String context, List<Object> instances) throws Exception {
        Host host = ServerUtil.getDefaultHost().getHost();
        StandardContext serverContext = (StandardContext) host.findChild("/" + context);
        if (serverContext == null) {
            serverContext = new StandardContext();
            serverContext.setPath("/" + context);
            File docBase = new File(SERVER_TEMP_DIR, context);
            if (!docBase.exists()) {
                if (!docBase.mkdirs()) {
                    throw ExtensionMessages.MESSAGES.unableToCreateTempDirectory(docBase.getPath());
                }
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
        while (serverContext.isStarting()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                // Ignore
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Spent sometime to start context.");
                }
            }
        }
        if (serverContext.isStarted()) {
            LOG.info("Published RESTEasy context " + serverContext.getPath());
            Registry registry = (Registry)serverContext.getServletContext().getAttribute(Registry.class.getName());
            List<Class<?>> classes = new ArrayList<Class<?>>();
            // Add as singleton instance
            for (Object instance : instances) {
                registry.addSingletonResource(instance);
                classes.add(instance.getClass());
            }
            RESTEasyResource resource = new RESTEasyResource();
            resource.setClasses(classes);
            resource.setContext(serverContext);
            return resource;
        } else {
            throw ExtensionMessages.MESSAGES.unableToStartContext(context);
        }
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
