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

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.CompositeModel;

/**
 * PortModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class PortModel extends BaseModel {

    public static final String PORT = "port";
    public static final String SECURE = "secure";

    public PortModel() {
        super(new QName(CompositeModel.DEFAULT_NAMESPACE, PORT));
    }

    public PortModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    public SOAPBindingModel getBinding() {
        return (SOAPBindingModel)getModelParent();
    }

    public boolean isSecure() {
        String secure = getModelAttribute(SECURE);
        return secure != null ? Boolean.parseBoolean(secure) : false;
    }

    public PortModel setSecure(boolean secure) {
        setModelAttribute(SECURE, String.valueOf(secure));
        return this;
    }

    public String getPort() {
        return getModelValue();
    }

    public PortModel setPort(String port) {
        setModelValue(port);
        return this;
    }

}
