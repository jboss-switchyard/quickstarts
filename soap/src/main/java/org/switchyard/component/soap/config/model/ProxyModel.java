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
package org.switchyard.component.soap.config.model;

import org.switchyard.config.model.Model;

/**
 * A Proxy Configuration Model.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public interface ProxyModel extends Model {

    /**
     * Get host.
     * @return authentication host
     */
    public String getHost();

    /**
     * Set host.
     * @param host the authentication host
     * @return this ProxyModel
     */
    public ProxyModel setHost(String host);

    /**
     * Get authentication port.
     * @return authentication port
     */
    public String getPort();

    /**
     * Set authentication port.
     * @param port the authentication port
     * @return this ProxyModel
     */
    public ProxyModel setPort(String port);

    /**
     * Get user name.
     * @return authentication username
     */
    public String getUser();

    /**
     * Set user name.
     * @param user the user name
     * @return this ProxyModel
     */
    public ProxyModel setUser(String user);

    /**
     * Get user password.
     * @return authentication password
     */
    public String getPassword();

    /**
     * Set user password.
     * @param password the user password
     * @return this ProxyModel
     */
    public ProxyModel setPassword(String password);

    /**
     * Get proxy type.
     * @return proxy type
     */
    public String getType();

    /**
     * Set proxy type. Default is HTTP.
     * @param type the proxy type
     * @return this ProxyModel
     */
    public ProxyModel setType(String type);

}
