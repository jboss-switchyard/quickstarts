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
package org.switchyard.component.soap.config.model;

import org.switchyard.config.model.Model;

/**
 * A SOAP Value Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public interface SOAPNameValueModel extends Model {

    /** Known XML element names. */
    public enum SOAPName {
        /** Known XML element names. */

        wsdl, wsdlPort, socketAddr, contextPath, endpointAddress, mtom, proxy, user, password, host, port, type, basic, ntlm, domain, timeout;
    }

    /**
     * Gets the name.
     * @return the name
     */
    public SOAPName getName();

    /**
     * Gets the value.
     * @return the value
     */
    public String getValue();

    /**
     * Sets the value.
     * @param value the value
     * @return this SOAPValueModel (useful for chaining)
     */
    public SOAPNameValueModel setValue(String value);

}
