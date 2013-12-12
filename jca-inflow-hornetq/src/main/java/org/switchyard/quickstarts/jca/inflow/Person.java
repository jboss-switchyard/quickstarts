/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.jca.inflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Person.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "person", propOrder = {
    "_name",
    "_language"
})
public class Person {

    @XmlElement(name = "name", required = true)
    private String _name;
    @XmlElement(name = "language", required = true)
    private String _language;

    /**
     * get name.
     * @return name
     */
    public String getName() {
        return _name;
    }

    /**
     * set name.
     * @param name name
     */
    public void setName(String name) {
        this._name = name;
    }

    /**
     * get lang.
     * @return lang
     */
    public String getLanguage() {
        return _language;
    }

    /**
     * set lang.
     * @param lang lang
     */
    public void setLanguage(String lang) {
        this._language = lang;
    }

}
