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
 
package org.switchyard.component.resteasy.resource;

import java.util.List;
import java.util.Map;

import org.jboss.resteasy.spi.ResteasyDeployment;
import org.switchyard.ServiceDomain;
import org.switchyard.component.common.Endpoint;
import org.switchyard.component.resteasy.util.RESTEasyProviderUtil;

/**
 * Publishes standalone RESTEasy resource to Netty.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class NettyResourcePublisher implements ResourcePublisher {
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
        _nettyServer.setRootResourcePath("");
        _nettyServer.setSecurityDomain(null);
        _nettyServer.setDeployment(deployment);
        _nettyServer.start();
    }

    /**
     * {@inheritDoc}
     */
    public Endpoint publish(ServiceDomain domain, String context, List<Object> instances, Map<String, String> contextParams) throws Exception {
        _nettyServer.stop();
        // CAUTION: Note that this publisher ignores context. Use it only for test purpose.
        for (Object instance : instances) {
            _nettyServer.getDeployment().getResources().add(instance);
        }
        List<String> providers = RESTEasyProviderUtil.getProviders(contextParams);
        if (providers != null) {
            _nettyServer.getDeployment().getScannedProviderClasses().addAll(providers);
        }
        _nettyServer.start();
        return new StandaloneResource();
    }
}
