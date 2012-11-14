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

package org.switchyard.component.camel.config.model.mail;

import org.switchyard.component.camel.config.model.CamelBindingModel;

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
