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
package org.switchyard.component.bpel.exchange;

import javax.xml.namespace.QName;

import org.riftsaw.engine.BPELEngine;
import org.switchyard.config.model.implementation.bpel.BPELComponentImplementationModel;
import org.switchyard.deploy.ServiceHandler;

/**
 * The ExchangeHandler for the BPEL component.
 *
 */
public interface BPELExchangeHandler extends ServiceHandler {

    /**
     * Initializes the BPELExchangeHandler.
     *
     * @param qname the qualified name
     * @param model the configuration
     * @param intf the WSDL interface details
     * @param engine the BPEL engine
     * @param config The configuration
     */
    public void init(QName qname, BPELComponentImplementationModel model,
                    String intf, BPELEngine engine, java.util.Properties config);

}
