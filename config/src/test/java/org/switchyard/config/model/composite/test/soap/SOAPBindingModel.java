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

package org.switchyard.config.model.composite.test.soap;

import static org.switchyard.config.model.composite.test.soap.PortModel.PORT;
import static org.switchyard.config.model.composite.test.soap.WSDLModel.WSDL;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1BindingModel;

/**
 * SOAPBindingModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class SOAPBindingModel extends V1BindingModel {

    public static final String SOAP = "soap";
    public static final String PORT_NUM = "portNum";

    private PortModel _port;
    private WSDLModel _wsdl;

    public SOAPBindingModel() {
        super(SOAP);
        setModelChildrenOrder(PORT, WSDL, PORT_NUM);
    }

    public SOAPBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(PORT, WSDL, PORT_NUM);
    }

    public PortModel getPort() {
        if (_port == null) {
            _port = (PortModel)getFirstChildModel(PORT);
        }
        return _port;
    }
    
    public Integer getPortNum() {
        Configuration config = getModelConfiguration().getFirstChild(PORT_NUM);
        return config != null ? Integer.valueOf(config.getValue()) : null;
    }

    public SOAPBindingModel setPort(PortModel port) {
        setChildModel(port);
        _port = port;
        return this;
    }
    
    public WSDLModel getWSDL() {
        if (_wsdl == null) {
            _wsdl = (WSDLModel)getFirstChildModelStartsWith(WSDL);
        }
        return _wsdl;
    }

    public SOAPBindingModel setWSDL(WSDLModel wsdl) {
        setChildModel(wsdl);
        _wsdl = wsdl;
        return this;
    }

}
