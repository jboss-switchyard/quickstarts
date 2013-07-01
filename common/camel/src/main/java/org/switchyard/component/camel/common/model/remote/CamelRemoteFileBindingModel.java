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
package org.switchyard.component.camel.common.model.remote;

import org.switchyard.component.camel.common.model.file.GenericFileBindingModel;

/**
 * Remote file endpoint configuration.
 * 
 * @author Lukasz Dywicki
 */
public interface CamelRemoteFileBindingModel extends GenericFileBindingModel {

    /**
     * Path separator enumeration.
     */
    public enum PathSeparator { 
        /**
         * Unix style path separator.
         */
        UNIX, 
        /**
         * Windows style path separator.
         */
        Windows, 
        /**
         * Enables auto detection of path separator.
         */
        Auto
    };

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
