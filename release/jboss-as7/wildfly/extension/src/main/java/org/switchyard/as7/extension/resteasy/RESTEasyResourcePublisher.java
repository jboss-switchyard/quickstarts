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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.as.server.ServerEnvironment;
import org.jboss.as.web.host.ServletBuilder;
import org.jboss.as.web.host.WebDeploymentBuilder;
import org.jboss.as.web.host.WebDeploymentController;
import org.jboss.as.web.host.WebHost;
import org.jboss.logging.Logger;
import org.switchyard.ServiceDomain;
import org.switchyard.as7.extension.ExtensionMessages;
import org.switchyard.as7.extension.WebResource;
import org.switchyard.as7.extension.deployment.SwitchYardDeployment;
import org.switchyard.as7.extension.util.ServerUtil;
import org.switchyard.component.common.Endpoint;
import org.switchyard.component.resteasy.resource.ResourcePublisher;

/**
 * Creates a RESTEasy resource on WildFly.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class RESTEasyResourcePublisher implements ResourcePublisher {

    private static final Logger LOG = Logger.getLogger("org.switchyard");
    private static final String SERVER_TEMP_DIR = System.getProperty(ServerEnvironment.SERVER_TEMP_DIR);
    private static final String SERVLET_NAME = "RestEasy";

    /**
     * {@inheritDoc}
     */
    public synchronized Endpoint publish(ServiceDomain domain, String context, List<Object> instances, Map<String, String> contextParams) throws Exception {
        WebResource resource = SwitchYardDeployment.getResource(domain, context);
        if (resource == null) {
            WebHost host = ServerUtil.getDefaultHost();
            WebDeploymentBuilder deployment = new WebDeploymentBuilder();
            WebDeploymentController handle = null;
            ServletBuilder servletBuilder = new ServletBuilder();
            try {
                deployment.setContextRoot(context);
                File docBase = new File(SERVER_TEMP_DIR, context);
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

                RESTEasyServlet servlet = new RESTEasyServlet();
                servletBuilder.setServletName(SERVLET_NAME);
                servletBuilder.setServletClass(RESTEasyServlet.class);
                servletBuilder.setForceInit(true);
                servletBuilder.addInitParam("resteasy.servlet.context.deployment", "true");
                if (contextParams != null) {
                    for (Map.Entry<String, String> cp : contextParams.entrySet()) {
                        servletBuilder.addInitParam(cp.getKey(), cp.getValue());
                    }
                }
                servletBuilder.setServlet(servlet);
                deployment.addServlet(servletBuilder);

                handle = host.addWebDeployment(deployment);
            } catch (Exception e) {
                throw ExtensionMessages.MESSAGES.unableToStartContext(context, e);
            }
            resource = new WebResource();
            resource.setHandle(handle);
            resource.setDeployment(deployment);
            SwitchYardDeployment.addResource(domain, resource);
        } else if ((resource.getDeployment() != null) && !(resource.getDeployment().getServlets().get(0).getServlet() instanceof RESTEasyServlet)) {
            throw ExtensionMessages.MESSAGES.contextAlreadyExists(context);
        }
        RESTEasyResource endpoint = new RESTEasyResource();
        endpoint.setInstances(instances);
        endpoint.setWebResource(resource);
        return endpoint;
    }
}
