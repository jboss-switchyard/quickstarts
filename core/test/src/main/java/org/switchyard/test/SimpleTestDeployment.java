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

package org.switchyard.test;

import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.deploy.Activator;
import org.switchyard.deploy.Lifecycle;
import org.switchyard.deploy.internal.AbstractDeployment;

/**
 * Simple Test Deployment.
 * <p/>
 * Does nothing extra.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SimpleTestDeployment extends AbstractDeployment {

    /**
     * Constructor.
     *
     */
    protected SimpleTestDeployment() {
        super(null);
    }

    @Override
    /*
     * Init method.
     * 
     * @param serviceDomain The ServiceDomain that the deployment is a member of.
     */
    protected void doInit(List<Activator> activators) {
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void destroy() {
        if (getDomain() != null) {
            getDomain().destroy();
        }
    }

    @Override
    public Lifecycle getGatwayLifecycle(QName serviceName, String bindingName) {
        return null;
    }
}
