/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

    private PortModel _port;
    private WSDLModel _wsdl;

    public SOAPBindingModel() {
        super(SOAP);
        setModelChildrenOrder(PORT, WSDL);
    }

    public SOAPBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(PORT, WSDL);
    }

    public PortModel getPort() {
        if (_port == null) {
            _port = (PortModel)getFirstChildModelStartsWith(PORT);
        }
        return _port;
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
