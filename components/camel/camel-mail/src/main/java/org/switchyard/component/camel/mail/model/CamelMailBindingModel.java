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
package org.switchyard.component.camel.mail.model;

import org.switchyard.component.camel.common.model.CamelBindingModel;

/**
 * Represents the configuration settings for an mail endpoint in Camel. Depends
 * on produce/consume part different protocols will be used.
 */
public interface CamelMailBindingModel extends CamelBindingModel {

    /**
     * The host name or IP address to connect to.
     * 
     * @return Host name or ip.
     */
    String getHost();

    /**
     * Specify host/ip to use.
     * 
     * @param host Host or ip address.
     * @return a reference to this Camel binding model
     */
    CamelMailBindingModel setHost(String host);

    /**
     * The TCP port number to connect on.
     * 
     * @return TCP port number
     */
    Integer getPort();

    /**
     * Specify TCP port number to connect.
     * 
     * @param port TCP port to use.
     * @return a reference to this Camel binding model
     */
    CamelMailBindingModel setPort(Integer port);

    /**
     * The user name on the email server.
     * 
     * @return User name/login to connect email server.
     */
    String getUsername();

    /**
     * Specify user name to use.
     * 
     * @param username Username to use for logging in on server.
     * @return a reference to this Camel binding model
     */
    CamelMailBindingModel setUsername(String username);

    /**
     * The password on the email server.
     * 
     * @return Password to connect email server.
     */
    String getPassword();

    /**
     * Specify password to use.
     * 
     * @param password Password for authentication.
     * @return a reference to this Camel binding model
     */
    CamelMailBindingModel setPassword(String password);

    /**
     * The connection timeout in milliseconds. Default is 30 seconds (30000 millis).
     * 
     * @return Connection timeout.
     */
    Integer getConnectionTimeout();

    /**
     * Specify connection timeout.
     * 
     * @param connectionTimeout Connection timeout.
     * @return a reference to this Camel binding model
     */
    CamelMailBindingModel setConnectionTimeout(Integer connectionTimeout);

    /**
     * Returns the secure flag. In other words if connection should be created
     * using smtps or smtp protocol scheme.
     * 
     * @return True if secure connection should be used.
     */
    Boolean isSecure();

    /**
     * Specify if secure connection should be used.
     * 
     * @param secure True if secure connection should be created.
     * @return a reference to this Camel binding model
     */
    CamelMailBindingModel setSecure(Boolean secure);

    /**
     * Gets consumer binding attached to this mail binding.
     * 
     * @return Consumer binding model.
     */
    CamelMailConsumerBindingModel getConsumer();

    /**
     * Specify consumer configuration.
     * 
     * @param consumer Consumer configuration.
     * @return a reference to this Camel binding model
     */
    CamelMailBindingModel setConsumer(CamelMailConsumerBindingModel consumer);

    /**
     * Gets producer binding attached to this mail binding.
     * 
     * @return Producer binding model.
     */
    CamelMailProducerBindingModel getProducer();

    /**
     * Specify producer configuration.
     * 
     * @param producer Producer configuration.
     * @return a reference to this Camel binding model
     */
    CamelMailBindingModel setProducer(CamelMailProducerBindingModel producer);

}
