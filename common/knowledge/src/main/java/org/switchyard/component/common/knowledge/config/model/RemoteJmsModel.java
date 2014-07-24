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
package org.switchyard.component.common.knowledge.config.model;

/**
 * A RemoteJmsModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public interface RemoteJmsModel extends RemoteModel {

    /** The "remoteJms" name. */
    public static final String REMOTE_JMS = "remoteJms";

    /**
     * Gets the hostName attribute.
     * @return the hostName attribute
     */
    public String getHostName();

    /**
     * Sets the hostName attribute.
     * @param hostName the hostName attribute
     * @return this RemoteJmsModel (useful for chaining)
     */
    public RemoteJmsModel setHostName(String hostName);

    /**
     * Gets the remotingPort attribute.
     * @return the remotingPort attribute
     */
    public Integer getRemotingPort();

    /**
     * Sets the remotingPort attribute.
     * @param remotingPort the remotingPort attribute
     * @return this RemoteJmsModel (useful for chaining)
     */
    public RemoteModel setRemotingPort(Integer remotingPort);

    /**
     * Gets the messagingPort attribute.
     * @return the messagingPort attribute
     */
    public Integer getMessagingPort();

    /**
     * Sets the messagingPort attribute.
     * @param messagingPort the messagingPort attribute
     * @return this RemoteJmsModel (useful for chaining)
     */
    public RemoteModel setMessagingPort(Integer messagingPort);

    /**
     * Gets the useSsl attribute.
     * @return the useSsl attribute
     */
    public boolean isUseSsl();

    /**
     * Sets the useSsl attribute.
     * @param useSsl the useSsl attribute
     * @return this RemoteJmsModel (useful for chaining)
     */
    public RemoteJmsModel setUseSsl(boolean useSsl);

    /**
     * Gets the keystorePassword attribute.
     * @return the keystorePassword attribute
     */
    public String getKeystorePassword();

    /**
     * Sets the keystorePassword attribute.
     * @param keystorePassword the keystorePassword attribute
     * @return this RemoteJmsModel (useful for chaining)
     */
    public RemoteJmsModel setKeystorePassword(String keystorePassword);

    /**
     * Gets the keystoreLocation attribute.
     * @return the keystoreLocation attribute
     */
    public String getKeystoreLocation();

    /**
     * Sets the keystoreLocation attribute.
     * @param keystoreLocation the keystoreLocation attribute
     * @return this RemoteJmsModel (useful for chaining)
     */
    public RemoteJmsModel setKeystoreLocation(String keystoreLocation);

    /**
     * Gets the truststorePassword attribute.
     * @return the truststorePassword attribute
     */
    public String getTruststorePassword();

    /**
     * Sets the truststorePassword attribute.
     * @param truststorePassword the keystorePassword attribute
     * @return this RemoteJmsModel (useful for chaining)
     */
    public RemoteJmsModel setTruststorePassword(String truststorePassword);

    /**
     * Gets the truststoreLocation attribute.
     * @return the truststoreLocation attribute
     */
    public String getTruststoreLocation();

    /**
     * Sets the truststoreLocation attribute.
     * @param truststoreLocation the keystoreLocation attribute
     * @return this RemoteJmsModel (useful for chaining)
     */
    public RemoteJmsModel setTruststoreLocation(String truststoreLocation);

}
