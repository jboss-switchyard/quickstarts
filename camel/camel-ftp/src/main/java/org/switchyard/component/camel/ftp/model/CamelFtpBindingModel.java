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
package org.switchyard.component.camel.ftp.model;

import org.switchyard.component.camel.common.model.remote.CamelRemoteFileBindingModel;

/**
 * Ftp endpoint binding.
 * 
 * @author Lukasz Dywicki
 */
public interface CamelFtpBindingModel extends CamelRemoteFileBindingModel {

    /**
     * Should connection be used in passive mode.
     * 
     * @return Passive mode.
     */
    Boolean isPassiveMode();

    /**
     * Sets passive mode.
     * 
     * @param passive Passive mode.
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setPassiveMode(boolean passive);

    /**
     * Gets timeout parameter.
     * 
     * @return Timeout (in milliseconds).
     */
    Integer getTimeout();

    /**
     * Sets timeout.
     * 
     * @param timeout Timeout.
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setTimeout(int timeout);

    /**
     * Get socket timeout.
     * 
     * @return Socket timeout (in milliseconds).
     */
    Integer getSoTimeout();

    /**
     * Sets socket timeout.
     * 
     * @param timeout Timeout (in milliseconds).
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setSoTimeout(int timeout);

    /**
     * Gets site command.
     * 
     * @return Site command.
     */
    String getSiteCommand();

    /**
     * Sets site command.
     * 
     * @param command Site command.
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setSiteCommand(String command);

}
