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
package org.switchyard.common.camel;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.cdi.CdiBeanRegistry;
import org.apache.camel.cdi.CdiInjector;
import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.spi.EventNotifier;
import org.apache.camel.spi.PackageScanClassResolver;
import org.apache.camel.spi.Registry;
import org.switchyard.ServiceDomain;
import org.switchyard.common.camel.event.CamelEventBridge;
import org.switchyard.common.cdi.CDIUtil;

/**
 * Extension of default camel context. Supports access to mutable registry and
 * provides integration with SwitchYard eventing model.
 */
public interface SwitchYardCamelContext extends ModelCamelContext {

    /**
     * Context property name used to store camel context as service domain property.
     */
    public static final String CAMEL_CONTEXT_PROPERTY = "CamelContextProperty";
    
    /**
     * Domain property used to configure the timeout value for ShutdownStrategy.
     */
    public static final String SHUTDOWN_TIMEOUT = "org.switchyard.camel.ShutdownTimeout";
    
    /**
     * Gets SwitchYard domain associated with this context.
     * 
     * @return SwitchYard domain.
     */
    public ServiceDomain getServiceDomain();

    /**
     * Gets mutable registry associated with context. Allows to dynamically register
     * bean instances.
     * 
     * @return Registry which allows to put new objects.
     */
    public SimpleRegistry getWritebleRegistry();
}
