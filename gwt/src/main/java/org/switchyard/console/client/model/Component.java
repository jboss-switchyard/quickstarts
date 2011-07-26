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
package org.switchyard.console.client.model;

/**
 * Component
 * 
 * Represents a SwitchYard component.
 * 
 * @author Rob Cernich
 */
public interface Component {

    /**
     * @return the name of the SwitchYard component.
     */
    public String getName();

    /**
     * @param name the name of the SwitchYard component.
     */
    public void setName(String name);

    /**
     * @return component type, e.g. GATEWAY.
     */
    public String getType();

    /**
     * @param type component type.
     */
    public void setType(String type);

    /**
     * @return the configuration schema defined by the component.
     */
    public String getConfigSchema();

    /**
     * @param configSchema the configuration schema defined by the component.
     */
    public void setConfigSchema(String configSchema);
}
