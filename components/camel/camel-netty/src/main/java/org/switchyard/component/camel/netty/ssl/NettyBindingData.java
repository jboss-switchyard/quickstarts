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
package org.switchyard.component.camel.netty.ssl;

import java.util.HashSet;
import java.util.Set;

import org.apache.camel.Message;
import org.apache.camel.component.netty.NettyConstants;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.ssl.SslHandler;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.common.composer.SecurityBindingData;
import org.switchyard.security.credential.Credential;
import org.switchyard.security.credential.extractor.SSLSessionCredentialExtractor;

/**
 * Extension of {@link CamelBindingData} which provides {@link SecurityBindingData}
 * extracted from SSLEngine used by netty.
 */
public class NettyBindingData extends CamelBindingData implements SecurityBindingData {

    /**
     * Creates netty binding data with given message.
     * 
     * @param message Camel message.
     */
    public NettyBindingData(Message message) {
        super(message);
    }

    @Override
    public Set<Credential> extractCredentials() {
        HashSet<Credential> credentials = new HashSet<Credential>();
        ChannelHandlerContext handlerContext = getMessage().getHeader(NettyConstants.NETTY_CHANNEL_HANDLER_CONTEXT, ChannelHandlerContext.class);
        if (handlerContext != null) {
            SslHandler sslHandler = handlerContext.getPipeline().get(SslHandler.class);
            if (sslHandler != null) {
                credentials.addAll(new SSLSessionCredentialExtractor().extract(sslHandler.getEngine().getSession()));
            }
        }
        return credentials;
    }

}
