/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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

package org.switchyard.deploy;

import static org.switchyard.deploy.internal.AbstractDeployment.CLASSLOADER_PROPERTY;

import org.switchyard.BaseHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.exception.SwitchYardException;

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
        return _domain == null ? null : (ClassLoader) _domain.getProperties().get(CLASSLOADER_PROPERTY);
    }

}
