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
 
package org.switchyard.as7.extension.http;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


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
import org.switchyard.component.http.InboundHandler;
import org.switchyard.component.http.HttpGatewayServlet;
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
    public synchronized Endpoint publish(ServiceDomain domain, String context, InboundHandler handler) throws Exception {

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

                HttpGatewayServlet servlet = new HttpGatewayServlet();
                servlet.setHandler(handler);
                servletBuilder.setServletName(SERVLET_NAME);
                servletBuilder.setServletClass(HttpGatewayServlet.class);
                servletBuilder.setForceInit(true);
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
        } else if ((resource.getDeployment() != null) && !(resource.getDeployment().getServlets().get(0).getServlet() instanceof HttpGatewayServlet)) {
            throw ExtensionMessages.MESSAGES.contextAlreadyExists(context);
        }
        JBossWebEndpoint endpoint = new JBossWebEndpoint();
        endpoint.setWebResource(resource);
        return endpoint;
    }
}
