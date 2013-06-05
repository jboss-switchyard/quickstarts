/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.bpm.config.model.v1;

import static org.switchyard.component.bpm.config.model.BPMComponentImplementationModel.DEFAULT_NAMESPACE;
import static org.switchyard.config.model.property.PropertiesModel.PROPERTIES;

import org.switchyard.common.type.Classes;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.bpm.config.model.UserGroupCallbackModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.property.PropertiesModel;

/**
 * The 1st version UserGroupCallbackModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1UserGroupCallbackModel extends BaseModel implements UserGroupCallbackModel {

    private PropertiesModel _properties;

    /**
     * Creates a new UserGroupCallbackModel.
     */
    public V1UserGroupCallbackModel() {
        super(XMLHelper.createQName(DEFAULT_NAMESPACE, USER_GROUP_CALLBACK));
        setModelChildrenOrder(PROPERTIES);
    }

    /**
     * Creates a new UserGroupCallbackModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1UserGroupCallbackModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(PROPERTIES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getClazz(ClassLoader loader) {
        String c = getModelAttribute("class");
        return c != null ? Classes.forName(c, loader) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserGroupCallbackModel setClazz(Class<?> clazz) {
        String c = clazz != null ? clazz.getName() : null;
        setModelAttribute("class", c);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertiesModel getProperties() {
        if (_properties == null) {
            _properties = (PropertiesModel)getFirstChildModel(PROPERTIES);
        }
        return _properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserGroupCallbackModel setProperties(PropertiesModel properties) {
        setChildModel(properties);
        _properties = properties;
        return this;
    }

}
