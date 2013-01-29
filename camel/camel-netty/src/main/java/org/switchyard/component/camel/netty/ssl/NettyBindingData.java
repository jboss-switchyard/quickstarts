/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
import org.switchyard.security.credential.extractor.SslSessionCredentialExtractor;

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
                credentials.addAll(new SslSessionCredentialExtractor().extract(sslHandler.getEngine().getSession()));
            }
        }
        return credentials;
    }

}
