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
 
package org.switchyard.as7.extension;

import java.util.ArrayList;
import java.util.List;

import org.jboss.as.web.host.WebDeploymentBuilder;
import org.jboss.as.web.host.WebDeploymentController;
import org.switchyard.component.common.Endpoint;

/**
 * A Web endpoint.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2014 Red Hat Inc.
 */
public class WebResource {

    private WebDeploymentBuilder _deployment;
    private WebDeploymentController _handle;
    private List<Endpoint> _endpoints = new ArrayList<Endpoint>();
    private Boolean _started = false;

    /**
     * Sets the started boolean.
     * @param started Set endpoint started or not
     */
    public void setStarted(Boolean started) {
        _started = started;
    }

    /**
     * Sets the deployment associated with this HTTP endpoint.
     * @return The WebDeploymentBuilder
     */
    public WebDeploymentBuilder getDeployment() {
        return _deployment;
    }

    /**
     * Sets the deployment associated with this HTTP endpoint.
     * @param deployment The WebDeploymentBuilder
     */
    public void setDeployment(WebDeploymentBuilder deployment) {
        _deployment = deployment;
    }

    /**
     * Sets the handle associated with this HTTP endpoint.
     * @return The WebDeploymentController
     */
    public WebDeploymentController getHandle() {
        return _handle;
    }

    /**
     * Sets the handle associated with this HTTP endpoint.
     * @param handle The WebDeploymentController
     */
    public void setHandle(WebDeploymentController handle) {
        _handle = handle;
    }

    /**
     * Starts the web context.
     * @param endpoint endpoint
     */
    public void start(Endpoint endpoint) {
        if (!_started) {
            try {
                if (_handle != null) {
                    _handle.create();
                    _handle.start();
                }
            } catch (Exception e) {
                throw ExtensionMessages.MESSAGES.unableToStartContext(_deployment.getContextRoot(), e);
            }
            _started = true;
        }
        _endpoints.add(endpoint);
    }

    /**
     * Stops the web context.
     * @param endpoint endpoint
     */
    public void stop(Endpoint endpoint) {
        _endpoints.remove(endpoint);
        if (_endpoints.isEmpty()) {
            try {
                if (_handle != null) {
                    _handle.stop();
                    _handle.destroy();
                }
            } catch (Exception e) {
                ExtensionLogger.ROOT_LOGGER.unableToDestroyWebContext(_deployment.getContextRoot(), e);
            }
            _started = false;
        }
    }
}
