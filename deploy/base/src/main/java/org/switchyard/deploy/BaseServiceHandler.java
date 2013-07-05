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

package org.switchyard.deploy;

import static org.switchyard.deploy.internal.AbstractDeployment.CLASSLOADER_PROPERTY;

import org.switchyard.BaseHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.SwitchYardException;

/**
 * NOP implementation of ServiceHandler.
 */
public class BaseServiceHandler extends BaseHandler implements ServiceHandler {

    private State _state = State.NONE;

    final private ServiceDomain _domain;

    /**
     * Creates a service handler that will not override the context class loader
     * used when start/stop are invoked.
     */
    public BaseServiceHandler() {
        this(null);
    }

    protected BaseServiceHandler(ServiceDomain domain) {
        _domain = domain;
    }

    @Override
    public synchronized void start() {
        if (_state == State.STARTED) {
            // already started
            return;
        } else if (_state != State.NONE) {
            throw new SwitchYardException("Invalid handler state.");
        }
        final ClassLoader oldTCCL = Thread.currentThread().getContextClassLoader();
        try {
            final ClassLoader deploymentCL = getDeploymentClassLoader();
            if (deploymentCL != null) {
                Thread.currentThread().setContextClassLoader(deploymentCL);
            }
            setState(State.STARTING);
            try {
                doStart();
                setState(State.STARTED);
            } catch (RuntimeException e) {
                setState(State.NONE);
                throw e;
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldTCCL);
        }
    }

    protected void doStart() {
    }

    @Override
    public synchronized void stop() {
        if (_state == State.NONE) {
            // already stopped
            return;
        } else if (_state != State.STARTED) {
            throw new SwitchYardException("Invalid handler state.");
        }
        final ClassLoader oldTCCL = Thread.currentThread().getContextClassLoader();
        try {
            final ClassLoader deploymentCL = getDeploymentClassLoader();
            if (deploymentCL != null) {
                Thread.currentThread().setContextClassLoader(deploymentCL);
            }
            setState(State.STOPPING);
            try {
                doStop();
                setState(State.NONE);
            } catch (RuntimeException e) {
                setState(State.STARTED);
                throw e;
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldTCCL);
        }
    }

    protected void doStop() {
    }

    @Override
    public State getState() {
        return _state;
    }

    /**
     * @param newState the new state of the service handler.
     */
    protected void setState(State newState) {
        if (newState == null) {
            throw new IllegalArgumentException("state cannot be null.");
        }
        _state = newState;
    }
    
    /**
     * @return the class loader for the deployment using this handler.
     */
    protected ClassLoader getDeploymentClassLoader() {
        return _domain == null 
                ? null 
                : (ClassLoader) _domain.getProperty(CLASSLOADER_PROPERTY);
    }

}
