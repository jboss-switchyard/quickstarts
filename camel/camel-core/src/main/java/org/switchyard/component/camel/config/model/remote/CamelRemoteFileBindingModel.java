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
import org.switchyard.component.camel.config.model.generic.GenericFileBindingModel;

/**
 * Remote file endpoint configuration.
 * 
 * @author Lukasz Dywicki
 */
public interface CamelRemoteFileBindingModel extends GenericFileBindingModel {

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
    Boolean isThrowExceptionOnConnectFailed();

    /**
     * Sets endpoint behavior during connection problems.
     * 
     * @param throwException Throw exception when connection is failed.
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setThrowExceptionOnConnectFailed(boolean throwException);

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
     * Specifies the maximum reconnect attempts Camel performs when it tries to
     * connect to the remote server. Use 0 to disable this behavior.
     * 
     * @return Number of maximum reconnect attempts.
     */
    Integer getMaximumReconnectAttempts();

    /**
     * Sets number of reconnect attempts. By default it is 3.
     * 
     * @param maximumReconnectAttempts Number of attempts. 0 to do not try again.
     * @return a reference to this binding model.
     */
    CamelRemoteFileBindingModel setMaximumReconnectAttempts(Integer maximumReconnectAttempts);

    /**
     * Delay in millis Camel will wait before performing a reconnect attempt.
     * 
     * @return delay between reconnect attempts.
     */
    Integer getReconnectDelay();

    /**
     * Specifies delay in milliseconds between reconnect attempts.
     * 
     * @param reconnectDelay Delay.
     * @return a reference to this binding model.
     */
    CamelRemoteFileBindingModel setReconnectDelay(Integer reconnectDelay);

    /**
     * Whether or not to disconnect from remote FTP server right after use. 
     * Can be used for both consumer and producer. Disconnect will only disconnect
     * the current connection to the FTP server. If you have a consumer which you want to stop,
     * then you need to stop the consumer/route instead.
     * 
     * @return Should producer/consumer be disconnected every time.
     */
    Boolean getDisconnect();

    /**
     * Specifies if producer/consumer should close connection after operation.
     * 
     * @param disconnect Should connection be closed after operation?
     * @return a reference to this binding model.
     */
    CamelRemoteFileBindingModel setDisconnect(Boolean disconnect);

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
     * The producer's configurations.
     * 
     * @return an instance of the camel remote file producer binding model
     */
    public CamelRemoteFileProducerBindingModel getProducer();

    /**
     * Specify the producer binding model. 
     * 
     * @param producer The producer binding model
     * @return a reference to this binding model
     */
    public CamelRemoteFileBindingModel setProducer(CamelRemoteFileProducerBindingModel producer);

}
