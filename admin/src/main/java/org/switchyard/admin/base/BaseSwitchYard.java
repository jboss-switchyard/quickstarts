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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.namespace.QName;

import org.switchyard.admin.Application;
import org.switchyard.admin.Component;
import org.switchyard.admin.Service;
import org.switchyard.admin.SwitchYard;

/**
 * In-memory representation of System admin contract.  Note that Service objects
 * are stored in a list and that removals are based on object identity and not 
 * object value.  This is to support multiple services registered with the 
 * same name.
 */
public class BaseSwitchYard implements SwitchYard {
    
    private String _version;
    private ConcurrentMap<QName, Application> _applications = 
        new ConcurrentHashMap<QName, Application>();
    private ConcurrentMap<String, Component> _components = 
        new ConcurrentHashMap<String, Component>();
    private List<Service> _services = 
        Collections.synchronizedList(new LinkedList<Service>());
    
    /**
     * Create a new BaseSwitchYard.
     * 
     * @param version the release version of the SwitchYard system.
     */
    public BaseSwitchYard(String version) {
        _version = version;
    }

    @Override
    public List<Application> getApplications() {
        return new ArrayList<Application>(_applications.values());
    }
    
    /**
     * Add an application.
     * @param application application to add
     * @return reference to this admin object
     */
    public BaseSwitchYard addApplication(Application application) {
        Application existing = _applications.putIfAbsent(application.getName(), application);
        if (existing == null) {
            _services.addAll(application.getServices());
        }
        return this;
    }
    
    /**
     * Remove an application.
     * @param application application to remove
     * @return reference to this admin object
     */
    public BaseSwitchYard removeApplication(Application application) {
        return removeApplication(application.getName());
    }

    /**
     * Remove an application.
     * @param name name of the application to remove.
     * @return reference to this admin object
     */
    public BaseSwitchYard removeApplication(QName name) {
        Application application = _applications.remove(name);
        if (application != null) {
            _services.removeAll(application.getServices());
        }
        return this;
    }

    @Override
    public List<Component> getComponents() {
        return new ArrayList<Component>(_components.values());
    }

    /**
     * Add a component.
     * @param component component to add
     * @return reference to this admin object
     */
    public BaseSwitchYard addComponent(Component component) {
        _components.putIfAbsent(component.getName(), component);
        return this;
    }
    
    /**
     * Remove a component.
     * @param component component to remove
     * @return reference to this admin object
     */
    public BaseSwitchYard removeComponent(Component component) {
        _components.remove(component.getName());
        return this;
    }

    @Override
    public List<Service> getServices() {
        return new ArrayList<Service>(_services);
    }
    
    /**
     * Add a service.
     * @param service service to add
     * @return reference to this admin object
     */
    public BaseSwitchYard addService(Service service) {
        _services.add(service);
        return this;
    }
    
    /**
     * Remove a service.
     * @param service service to remove
     * @return reference to this admin object
     */
    public BaseSwitchYard removeService(Service service) {
        _services.remove(service);
        return this;
    }

    @Override
    public String getVersion() {
        return _version;
    }
    
    /**
     * Set the version of the SwitchYard runtime.
     * @param version SwitchYard version
     */
    public void setVersion(String version) {
        _version = version;
    }

    @Override
    public Component getComponent(String name) {
        return _components.get(name);
    }

    @Override
    public Application getApplication(QName name) {
        return _applications.get(name);
    }

}
