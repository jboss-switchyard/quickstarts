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

package org.switchyard;

import org.apache.log4j.Logger;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.deploy.internal.Deployment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * SwitchYard main.
 * <p/>
 * Standalone bootstrap class.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SwitchYard {

    private static Logger _logger = Logger.getLogger(SwitchYard.class);

    private Deployment _deployment;

    /**
     * Public constructor.
     * @param config Switchyard configuration.
     * @throws IOException Error reading configuration.
     */
    public SwitchYard(InputStream config) throws IOException {
        _deployment = new Deployment(config);
    }

    /**
     * Start the SwitchYard application.
     */
    public void start() {
        _logger.debug("Starting SwitchYard application '" + _deployment.getConfig().getQName() + "'.");
        _deployment.init(ServiceDomainManager.createDomain(_deployment.getConfig().getQName(), _deployment.getConfig()));
        _deployment.start();
        _logger.debug("SwitchYard application '" + _deployment.getConfig().getQName() + "' started.");
    }

    /**
     * Stop the SwitchYard application.
     */
    public void stop() {
        _logger.debug("Stopping SwitchYard application '" + _deployment.getConfig().getQName() + "'.");
        _deployment.stop();
        _logger.debug("SwitchYard application '" + _deployment.getConfig().getQName() + "' stopped.");
    }

    /**
     * Main method.
     * @param args startup args.
     * @throws Exception Error starting SwitchYard application.
     */
    public static void main(String[] args) throws Exception {
        InputStream configStream;

        if (args.length == 0) {
            configStream = SwitchYard.class.getResourceAsStream(AbstractDeployment.SWITCHYARD_XML);

            if (configStream == null) {
                System.out.println("Usage: " + SwitchYard.class.getName() + " path-to-switchyard-config");
                System.exit(1);
            }
        } else {
            File configFile = new File(args[0]);

            if (!configFile.isFile()) {
                System.out.println("'" + args[0] + "' is not a valid SwitchYard configuration file.");
                System.exit(1);
            }

            configStream = new FileInputStream(configFile);
        }

        final SwitchYard switchyard;
        try {
            switchyard = new SwitchYard(configStream);
        } finally {
            configStream.close();
        }

        switchyard.start();

        Runtime.getRuntime().addShutdownHook(
            new Thread() {
                public void run() {
                    switchyard.stop();
                    switchyard.notify();
                }
            }
        );

        switchyard.wait();
        System.exit(0);
    }
}
