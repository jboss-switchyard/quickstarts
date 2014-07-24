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
package org.switchyard.component.common.knowledge.config.model.v2;

import org.switchyard.component.common.knowledge.config.model.RemoteJmsModel;
import org.switchyard.component.common.knowledge.config.model.RemoteModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * The 2nd version RemoteJmsModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class V2RemoteJmsModel extends V2RemoteModel implements RemoteJmsModel {

    /**
     * Constructs a new V2JmsRemoteModel of the specified namespace.
     * @param namespace the namespace
     */
    public V2RemoteJmsModel(String namespace) {
        super(namespace, REMOTE_JMS);
    }

    /**
     * Constructs a new V2RemoteJmsModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V2RemoteJmsModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHostName() {
        return getModelAttribute("hostName");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteJmsModel setHostName(String hostName) {
        setModelAttribute("hostName", hostName);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getRemotingPort() {
        String rp = getModelAttribute("remotingPort");
        return rp != null ? Integer.valueOf(rp) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteModel setRemotingPort(Integer remotingPort) {
        String rp = remotingPort != null ? remotingPort.toString() : null;
        setModelAttribute("remotingPort", rp);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getMessagingPort() {
        String mp = getModelAttribute("messagingPort");
        return mp != null ? Integer.valueOf(mp) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteModel setMessagingPort(Integer messagingPort) {
        String mp = messagingPort != null ? messagingPort.toString() : null;
        setModelAttribute("messagingPort", mp);
        return this;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUseSsl() {
        String useSsl = getModelAttribute("useSsl");
        return useSsl != null ? Boolean.parseBoolean(useSsl) : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteJmsModel setUseSsl(boolean useSsl) {
        setModelAttribute("useSsl", String.valueOf(useSsl));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKeystorePassword() {
        return getModelAttribute("keystorePassword");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteJmsModel setKeystorePassword(String keystorePassword) {
        setModelAttribute("keystorePassword", keystorePassword);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKeystoreLocation() {
        return getModelAttribute("keystoreLocation");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteJmsModel setKeystoreLocation(String keystoreLocation) {
        setModelAttribute("keystoreLocation", keystoreLocation);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTruststorePassword() {
        return getModelAttribute("truststorePassword");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteJmsModel setTruststorePassword(String truststorePassword) {
        setModelAttribute("truststorePassword", truststorePassword);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTruststoreLocation() {
        return getModelAttribute("truststoreLocation");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteJmsModel setTruststoreLocation(String truststoreLocation) {
        setModelAttribute("truststoreLocation", truststoreLocation);
        return this;
    }

}
