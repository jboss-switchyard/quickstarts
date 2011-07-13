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

package org.switchyard.admin.base;

import org.switchyard.admin.Component;
import org.switchyard.admin.ComponentType;

/**
 * BaseComponent
 *
 * Basic implementation for Component.
 */
public class BaseComponent implements Component {
    
    private String _configSchema;
    private String _name;
    private ComponentType _type;
    
    /**
     * Create a new BaseComponent.
     * 
     * @param name the name of the component.
     * @param type the type of the component.
     * @param configSchema the configuration schema defined by this component.
     */
    public BaseComponent(String name, ComponentType type, String configSchema) {
        _name = name;
        _type = type;
        _configSchema = configSchema;
    }

    @Override
    public String getConfigSchema() {
        return _configSchema;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public ComponentType getType() {
        return _type;
    }
}
