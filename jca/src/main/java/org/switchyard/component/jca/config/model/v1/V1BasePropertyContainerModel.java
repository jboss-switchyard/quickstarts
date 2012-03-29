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
package org.switchyard.component.jca.config.model.v1;

import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.switchyard.component.jca.JCAConstants;
import org.switchyard.component.jca.config.model.BasePropertyContainerModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * V1 BasePropertyContainer model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public abstract class V1BasePropertyContainerModel extends BaseModel implements BasePropertyContainerModel {

    protected V1BasePropertyContainerModel(QName name) {
        super(name);
    }
    
    protected V1BasePropertyContainerModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getProperty(String key) {
        List<Configuration> properties = getModelConfiguration().getChildren(JCAConstants.PROPERTY);
        for (Configuration prop : properties) {
            if (key.equals(prop.getAttribute(JCAConstants.NAME))) {
                return prop.getAttribute(JCAConstants.VALUE);
            }
        }
        return null;
    }

    @Override
    public BasePropertyContainerModel setProperty(String key, String value) {
        List<Configuration> properties = getModelConfiguration().getChildren(JCAConstants.PROPERTY);
        for (Configuration prop : properties) {
            if (key.equals(prop.getAttribute(JCAConstants.NAME))) {
                prop.setAttribute(JCAConstants.VALUE, value);
                return this;
            }
        }
        V1PropertyModel model = new V1PropertyModel(key, value);
        setChildModel(model);
        return this;
    }

    @Override
    public Properties getProperties() {
        Properties properties = new Properties();
        List<Configuration> modelProperties = getModelConfiguration().getChildren(JCAConstants.PROPERTY);
        for (Configuration prop : modelProperties) {
            properties.put(prop.getAttribute(JCAConstants.NAME), prop.getAttribute(JCAConstants.VALUE));
        }
        return properties;
    }

    @Override
    public BasePropertyContainerModel setProperties(Properties properties) {
        getModelConfiguration().removeChildren(JCAConstants.PROPERTY);
        Enumeration<?> e = properties.keys();
        while (e.hasMoreElements()) {
            String key = e.nextElement().toString();
            String value = properties.getProperty(key);
            V1PropertyModel model = new V1PropertyModel(key,value);
            setChildModel(model);
        }
        return this;
    }

}
