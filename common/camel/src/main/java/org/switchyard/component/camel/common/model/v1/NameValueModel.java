/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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
package org.switchyard.component.camel.common.model.v1;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;

/**
 * Generic configuration interface used to represent a basic name to value mapping
 * for a child configuration, e.g.
 * <br><br>
 * <pre>
 *    <name>value</name>
 * </pre>
 * <br>
 */
public class NameValueModel extends BaseModel {

    /**
     * Create a new NameValueModel with the specified name.
     * 
     * @param namespace Config namespace.
     * @param name Config name
     */
    public NameValueModel(String namespace, String name) {
        super(new QName(namespace, name));
    }

    /**
     * Create a new NameValueModel based on an existing config element.
     * @param config configuration element
     */
    public NameValueModel(Configuration config) {
        super(config);
    }

    /**
     * Get the config value.
     * @return config value
     */
    public String getValue() {
        return super.getModelValue();
    }

    /**
     * Set the config value.
     * @param value config value
     */
    public void setValue(String value) {
        super.setModelValue(value);
    }

}
