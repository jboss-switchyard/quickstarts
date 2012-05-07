/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.config.model.remote;

import org.apache.camel.component.file.remote.RemoteFileConfiguration.PathSeparator;
import org.switchyard.component.camel.config.model.CamelBindingModel;

/**
 * Remote file endpoint configuration.
 * 
 * @author Lukasz Dywicki
 */
public interface CamelRemoteFileBindingModel extends CamelBindingModel {

    /**
     * Gets host name / ip.
     * 
     * @return Host name.
     */
    String getHost();

    /**
     * Sets host name.
     * 
     * @param host Host name.
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setHost(String host);

    /**
     * Gets connection port.
     * 
     * @return Port number used to connect remote server.
     */
    Integer getPort();

    /**
     * Sets port to use during connection.
     * 
     * @param port Port number.
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setPort(int port);

    /**
     * Gets directory to poll.
     * 
     * @return Directory name.
     */
    String getDirectory();

    /**
     * Sets durectory to poll.
     * 
     * @param directory Directory name.
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setDirectory(String directory);

    /**
     * Gets user name.
     * 
     * @return Username used to authenticate connection.
     */
    String getUsername();

    /**
     * Specifies the username to use to log in to the remote file system.
     * 
     * @param username Username.
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setUsername(String username);

    /**
     * Gets password.
     * 
     * @return Password used to authenticate connection.
     */
    String getPassword();

    /**
     * Specifies the password to use to log in to the remote file system.
     * 
     * @param password Password.
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setPassword(String password);

    /**
     * Should transfer be treat as binary?
     * 
     * @return Binary transfer.
     */
    Boolean isBinary();

    /**
     * Specifies the file transfer mode, BINARY or ASCII. Default is ASCII (false).
     * 
     * @param binary Should transfer be binary.
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setBinary(boolean binary);

    /**
     * Gets connect timeout.
     * 
     * @return Connect timeout (in milliseconds).
     */
    Integer getConnectTimeout();

    /**
     * Sets connect timeout (in milliseconds).
     * 
     * @param timeout Timeout.
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setConnectionTimeout(int timeout);

    /**
     * Should exception be thrown when connection is failed?
     * 
     * @return Throw exception if value is set to true. Skip otherwise.
     */
    Boolean isThrowExceptionOnConnectionFailed();

    /**
     * Sets endpoint behavior during connection problems.
     * 
     * @param throwException Throw exception when connection is failed.
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setThrowExceptionOnConnectionFailed(boolean throwException);

    /**
     * Gets stepwise flag value.
     * 
     * @return True if camel consumer is configured in stepwise mode.
     */
    Boolean isStepwise();

    /**
     * Whether or not stepwise traversing directories should be used or not.
     * Stepwise means that it will CD one directory at a time. See more details below.
     * You can disable this in case you can't use this approach.
     * 
     * @param stepwise Should camel do CD every time.
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setStepwise(boolean stepwise);

    /**
     * Gets path separator.
     * 
     * @return Path separator.
     */
    PathSeparator getSeparator();

    /**
     * Dictates what path separator char to use when uploading files.
     * Auto = Use the path provided without altering it.
     * UNIX = Use unix style path separators.
     * Windows = Use Windows style path separators.
     * 
     * @param separator Directory separator.
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setSeparator(String separator);


    /**
     * The consumer's configurations.
     * 
     * @return an instance of the camel remote file consumer binding model
     */
    public CamelRemoteFileConsumerBindingModel getConsumer();

    /**
     * Specify the consumer binding model. 
     * 
     * @param consumer The consumer binding model
     * @return a reference to this binding model
     */
    public CamelRemoteFileBindingModel setConsumer(CamelRemoteFileConsumerBindingModel consumer);

    /**
     * Specifies the maximum reconnect attempts Camel performs when it tries to connect to the remote FTP server. Use 0 to disable this behavior.
     */
//    maximumReconnectAttempts
    /**
     * Delay in millis Camel will wait before performing a reconnect attempt.
     */
//    reconnectDelay
}
