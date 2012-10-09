/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.quickstarts.jca.inflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Person.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "_name",
    "_language",
})
@XmlRootElement(name = "person")
public class Person {

    @XmlElement(name="name", required = true)
    private String _name;
    @XmlElement(name="language", required = true)
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
