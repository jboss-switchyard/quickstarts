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
package org.switchyard.component.http.config.model;

import org.switchyard.config.model.Model;

/**
 * A Basic Authentication Model.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public interface BasicAuthModel extends Model {

    /**
     * Get user name.
     * @return authentication username
     */
    public String getUser();

    /**
     * Set user name.
     * @param user the user name
     * @return this BasicAuthModel
     */
    public BasicAuthModel setUser(String user);

    /**
     * Get user password.
     * @return authentication password
     */
    public String getPassword();

    /**
     * Set user password.
     * @param password the user password
     * @return this BasicAuthModel
     */
    public BasicAuthModel setPassword(String password);

    /**
     * Get authentication realm.
     * @return authentication realm
     */
    public String getRealm();

    /**
     * Set authentication realm.
     * @param realm the authentication realm
     * @return this BasicAuthModel
     */
    public BasicAuthModel setRealm(String realm);

    /**
     * Get host.
     * @return authentication host
     */
    public String getHost();

    /**
     * Set host.
     * @param host the authentication host
     * @return this BasicAuthModel
     */
    public BasicAuthModel setHost(String host);

    /**
     * Get authentication port.
     * @return authentication port
     */
    public String getPort();

    /**
     * Set authentication port.
     * @param port the authentication port
     * @return this BasicAuthModel
     */
    public BasicAuthModel setPort(String port);

}
