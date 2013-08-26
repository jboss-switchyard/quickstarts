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

package org.switchyard.standalone;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jboss.logging.Logger;
import org.switchyard.ServiceDomain;
import org.switchyard.BaseDeployMessages;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.ActivatorLoader;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.deploy.internal.Deployment;

/**
 * SwitchYard main.
 * <p/>
 * Standalone bootstrap class.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SwitchYard {

    private static Logger _logger = Logger.getLogger(SwitchYard.class);
    private List<Activator> _activatorList;
    private ServiceDomain _domain;
    private Deployment _deployment;

    /**
     * Create a new SwitchYard runtime from the specified config.
     * @param config Switchyard configuration.
     * @throws IOException Error reading configuration.
     */
    public SwitchYard(InputStream config) throws IOException {
        _deployment = new Deployment(config);
        _domain = new ServiceDomainManager().createDomain(
                _deployment.getConfig().getQName(), _deployment.getConfig());
        _activatorList = ActivatorLoader.createActivators(_domain);
    }

    /**
     * Start the SwitchYard application.
     */
    public void start() {
        _logger.debug("Starting SwitchYard application '" + _deployment.getConfig().getQName() + "'.");
        
        _deployment.init(_domain, _activatorList);
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
     * Returns a reference to the activator list used by this runtime instance.
     * @return the activator list
     */
    public List<Activator> getActivatorList() {
        return _activatorList;
    }
    
    /**
     * Sets the list of activators used by the runtime instance.
     * @param activators the list of activators to use for this runtime instance
     */
    public void setActivatorList(List<Activator> activators) {
        _activatorList = activators;
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
                System.out.println(BaseDeployMessages.MESSAGES.usagePath(SwitchYard.class.getName()));
                System.exit(1);
            }
        } else {
            File configFile = new File(args[0]);

            if (!configFile.isFile()) {
                System.out.println(BaseDeployMessages.MESSAGES.notValidConfigFile(args[0]));
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
