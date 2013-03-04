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
 
package org.switchyard.component.resteasy.resource;

import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.resteasy.spi.ResteasyDeployment;

/**
 * Publishes standalone RESTEasy resource to Netty.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class NettyResourcePublisher implements ResourcePublisher {
    private static final Logger LOGGER = Logger.getLogger(NettyResourcePublisher.class);

    /** 
     * The global standalone NettyServer.
     *
     * Keep org.switchyard.component.resteasy.resource.NettyJaxrsServer class untill 
     * https://issues.jboss.org/browse/RESTEASY-794 moves to a released AS7 version that we can use.
     */
    private static NettyJaxrsServer _nettyServer;

    static {
        ResteasyDeployment deployment = new ResteasyDeployment();
        _nettyServer = new NettyJaxrsServer();
        _nettyServer.setPort(8080);
        _nettyServer.setRootResourcePath("");
        _nettyServer.setSecurityDomain(null);
        _nettyServer.setDeployment(deployment);
        _nettyServer.start();
    }

    /**
     * {@inheritDoc}
     */
    public Resource publish(String context, List<Object> instances) throws Exception {
        // CAUTION: Note that this publisher ignores context. Use it only for test purpose.
        for (Object instance : instances) {
            _nettyServer.getDeployment().getResources().add(instance);
        }
        _nettyServer.stop();
        _nettyServer.start();
        return new StandaloneResource();
    }
}
