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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.namespace.QName;

import org.switchyard.admin.Application;
import org.switchyard.admin.Component;
import org.switchyard.admin.Service;
import org.switchyard.admin.SwitchYard;
import org.switchyard.common.version.Versions;

/**
 * In-memory representation of System admin contract. Note that Service objects
 * are stored in a list and that removals are based on object identity and not
 * object value. This is to support multiple services registered with the same
 * name.
 */
public class BaseSwitchYard extends BaseMessageMetricsAware implements SwitchYard {

    private final String _version;
    private ConcurrentMap<QName, Application> _applications = new ConcurrentHashMap<QName, Application>();
    private ConcurrentMap<String, Component> _components = new ConcurrentHashMap<String, Component>();
    private List<Service> _services = Collections.synchronizedList(new LinkedList<Service>());
    private Set<String> _socketBindingNames = Collections.synchronizedSet(new HashSet<String>());
    private ConcurrentMap<String, String> _properties = new ConcurrentHashMap<String, String>();

    /**
     * Create a new BaseSwitchYard.
     */
    public BaseSwitchYard() {
        _version = Versions.getSwitchYardVersion();
    }

    @Override
    public List<Application> getApplications() {
        return new ArrayList<Application>(_applications.values());
    }

    /**
     * Add an application.
     * 
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
     * 
     * @param application application to remove
     * @return reference to this admin object
     */
    public BaseSwitchYard removeApplication(Application application) {
        return removeApplication(application.getName());
    }

    /**
     * Remove an application.
     * 
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
     * 
     * @param component component to add
     * @return reference to this admin object
     */
    public BaseSwitchYard addComponent(Component component) {
        _components.putIfAbsent(component.getName(), component);
        return this;
    }

    /**
     * Remove a component.
     * 
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
     * 
     * @param service service to add
     * @return reference to this admin object
     */
    public BaseSwitchYard addService(Service service) {
        _services.add(service);
        return this;
    }

    /**
     * Remove a service.
     * 
     * @param service service to remove
     * @return reference to this admin object
     */
    public BaseSwitchYard removeService(Service service) {
        _services.remove(service);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVersion() {
        return _version;
    }

    @Override
    public Component getComponent(String name) {
        return _components.get(name);
    }

    @Override
    public Application getApplication(QName name) {
        return _applications.get(name);
    }

    /**
     * Add a set of names to the set of socket binding names.
     * 
     * @param names the names to add.
     * 
     */
    public void addSocketBindingNames(Set<String> names) {
        _socketBindingNames.addAll(names);
    }

    /**
     * Add a name to the set of socket binding names.
     * 
     * @param name the name to add.
     */
    public void addSocketBindingName(String name) {
        _socketBindingNames.add(name);
    }

    /**
     * Remove the specified name from the set of socket bindings.
     * 
     * @param name the name to remove.
     */
    public void removeSocketBindingName(String name) {
        _socketBindingNames.remove(name);
    }

    /**
     * Removes the specified names from the set of socket bindings.
     * 
     * @param names the names to remove.
     */
    public void removeSocketBindingNames(Set<String> names) {
        _socketBindingNames.removeAll(names);
    }

    @Override
    public Set<String> getSocketBindingNames() {
        return Collections.unmodifiableSet(_socketBindingNames);
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(_properties);
    }

    /**
     * Adds a set of properties to the set of system properties.
     * 
     * @param properties the properties to add.
     */
    public void addProperties(Map<String, String> properties) {
        _properties.putAll(properties);
    }

    /**
     * Adds a property to the set of system properties.
     * 
     * @param name the name of the property.
     * @param value the value of the property.
     */
    public void addProperty(String name, String value) {
        _properties.put(name, value);
    }

    /**
     * Removes the properties from the set of system properties.
     * 
     * @param properties the properties to remove.
     */
    public void removeProperties(Map<String, String> properties) {
        _properties.keySet().removeAll(properties.keySet());
    }

    /**
     * Removes a property from the set of system properties.
     * 
     * @param name the name of the property to remove.
     */
    public void removeProperty(String name) {
        _properties.remove(name);
    }

}
