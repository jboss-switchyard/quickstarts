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

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.admin.Application;
import org.switchyard.admin.Service;

/**
 * Base implementation of Application.
 */
public class BaseApplication implements Application {
    
    private QName _name;
    private List<Service> _services;
    
    /**
     * Create a new BaseApplication with the specified services.
     * @param name application name
     * @param services list of services
     */
    public BaseApplication(QName name, List<Service> services) {
        this(name);
        _services = services;
    }

    /**
     * Create a new BaseApplication.
     * @param name application name
     */
    public BaseApplication(QName name) {
        _name = name;
    }

    @Override
    public QName getName() {
        return _name;
    }

    @Override
    public List<Service> getServices() {
        if (_services == null) {
            return Collections.emptyList();
        }
        return _services;
    }
    
    /**
     * Set the list of services offered by this application.
     * @param services list of services
     */
    public void setServices(List<Service> services) {
        _services = services;
    }

}
