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

package org.switchyard.component.resteasy.config.model;

import org.switchyard.config.model.Model;

/**
 * A RESTEasy value model.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public interface RESTEasyNameValueModel extends Model {

    /** Known XML element names. */
    public enum RESTEasyName {
        /** Known XML element names. */
        interfaces, address, contextPath, basic, ntlm, user, password, realm, domain, host, port, proxy;
    }

    /**
     * Gets the name.
     * @return the name
     */
    public RESTEasyName getName();

    /**
     * Gets the value.
     * @return the value
     */
    public String getValue();

    /**
     * Sets the value.
     * @param value the value
     * @return this RESTEasyValueModel (useful for chaining)
     */
    public RESTEasyNameValueModel setValue(String value);

}
