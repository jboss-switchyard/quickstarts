/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

package org.switchyard.deployment;

import org.apache.log4j.Logger;
import org.switchyard.SwitchYard;
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
