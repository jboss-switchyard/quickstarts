/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.bpel.osgi;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.transaction.TransactionManager;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.riftsaw.engine.BPELEngine;
import org.riftsaw.engine.ServiceLocator;
import org.riftsaw.engine.internal.BPELEngineImpl;
import org.riftsaw.engine.internal.DeploymentManager;

/**
 * Simple class for creating BPELEngine beans through blueprint.
 */
public class BPELEngineFactory {

    private ServiceLocator _serviceLocator;
    private Properties _configuration;
    private TransactionManager _transactionManager;

    /**
     * @return a new BPELEngine instance
     * @throws Exception if something goes awry
     */
    public BPELEngine createEngine() throws Exception {
        final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            final BPELEngineImpl engine = new BPELEngineImpl();
            engine.setTransactionManager(_transactionManager);
            engine.setDeploymentManager(createDeploymentManager());
            engine.init(_serviceLocator, _configuration);
            return engine;
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }

    /**
     * @param serviceLocator service locator to use for new engines
     */
    public void setServiceLocator(ServiceLocator serviceLocator) {
        _serviceLocator = serviceLocator;
    }

    /**
     * @param configuration configuration to use for new engines
     */
    public void setConfiguration(Properties configuration) {
        _configuration = configuration;
    }

    /**
     * @param transactionManager transaction manager to use for new engines
     */
    public void setTransactionManager(TransactionManager transactionManager) {
        _transactionManager = transactionManager;
    }

    private DeploymentManager createDeploymentManager() throws IOException {
        final DeploymentManager manager = new DeploymentManager();
        manager.setDeploymentFolder(getDeploymentsDirectory().getCanonicalPath());
        return manager;
    }

    private File getDeploymentsDirectory() {
        final Bundle bundle = FrameworkUtil.getBundle(getClass());
        final File dataDirectory = bundle.getDataFile("");
        final File deploymentsDirectory = new File(dataDirectory, "deployments");
        if (!deploymentsDirectory.exists()) {
            deploymentsDirectory.mkdir();
            deploymentsDirectory.deleteOnExit();
        }
        return deploymentsDirectory;
    }
}
