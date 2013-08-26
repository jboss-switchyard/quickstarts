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

package org.switchyard.deployment;

import org.jboss.logging.Logger;
import org.switchyard.standalone.SwitchYard;
import org.switchyard.deploy.internal.AbstractDeployment;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;

/**
 * SwitchYard Web Application Deployer.
 * <p/>
 * {@link ServletContextListener} implementation that allows SwitchYard be deployed as part of
 * a JEE Web Application.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class WebApplicationDeployer implements ServletContextListener {

    private static Logger _logger = Logger.getLogger(WebApplicationDeployer.class);

    private SwitchYard _switchyard;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        InputStream switchYardConfig = servletContext.getResourceAsStream("WEB-INF/classes/" + AbstractDeployment.SWITCHYARD_XML);

        if (switchYardConfig != null) {
            try {
                _switchyard = new SwitchYard(switchYardConfig);
                _switchyard.start();
            } catch (IOException e) {
                _logger.debug("Error deploying SwitchYard within Web Application '" + servletContext.getServletContextName() + "'.  SwitchYard not deployed.", e);
            }
        } else {
            _logger.debug("No SwitchYard configuration found at '" + AbstractDeployment.SWITCHYARD_XML + "' in Web Application '" + servletContext.getServletContextName() + "'.  SwitchYard not deployed.");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (_switchyard != null) {
            _switchyard.stop();
        }
    }
}
