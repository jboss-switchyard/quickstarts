/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.switchyard.admin.base;

import java.util.Collection;

import org.switchyard.admin.Application;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.metadata.java.JavaService;

/**
 * Represents service with java contract.
 */
public class BaseJavaComponentService extends ComponentServiceWithOperations {

    /**
     * Create a new BaseComponentService from a config model.
     * 
     * @param serviceConfig the component service configuration
     * @param componentConfig the component configuration
     * @param application the switchyard application
     */
    public BaseJavaComponentService(ComponentServiceModel serviceConfig, ComponentModel componentConfig, Application application) {
        super(serviceConfig, componentConfig, application);
    }

    @Override
    protected Collection<org.switchyard.metadata.ServiceOperation> getInterfaceOperations(InterfaceModel interfaceModel) {
        return JavaService.fromClass(Classes.forName(interfaceModel.getInterface())).getOperations();
    }

}
