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
package org.switchyard.config.model.composite.v1;

import org.switchyard.config.Configuration;
import org.switchyard.config.Descriptor;
import org.switchyard.config.model.composite.BaseTypeModel;
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.java.JavaService;

/**
 * V1InterfaceModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1InterfaceModel extends BaseTypeModel implements InterfaceModel {

    private ServiceInterface _metadata;

    public V1InterfaceModel(String type) {
        super("interface." + type);
    }

    public V1InterfaceModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getInterface() {
        return getModelAttribute("interface");
    }

    @Override
    public InterfaceModel setInterface(String interfaze) {
        setModelAttribute("interface", interfaze);
        return this;
    }

    @Override
    public synchronized ServiceInterface getMetadata() {
        if (_metadata == null) {
            String interfaze = getInterface();
            if (interfaze != null) {
                if ("java".equals(getType())) {
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(interfaze);
                    } catch (ClassNotFoundException cnfe) {
                        throw new RuntimeException(cnfe);
                    }
                    _metadata = JavaService.fromClass(clazz);
                }
            }
        }
        return _metadata;
    }

}
