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
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.admin.Application;
import org.switchyard.admin.ComponentReference;
import org.switchyard.admin.ComponentService;

/**
 * BaseComponentService
 * 
 * Base implementation for {@link ComponentService}.
 * 
 * @author Rob Cernich
 */
public class BaseComponentService implements ComponentService {
    
    private final QName _name;
    private final String _implementation;
    private final String _interface;
    private final Application _application;
    private List<ComponentReference> _references;

    /**
     * Create a new BaseComponentService.
     * 
     * @param name the name of this service
     * @param implementation the implementation of this service
     * @param interfaceName the interface this service implements
     * @param application the application providing this service
     * @param references the references required by this service
     */
    public BaseComponentService(QName name, String implementation, String interfaceName, Application application, List<ComponentReference> references) {
        _name = name;
        _implementation = implementation;
        _interface = interfaceName;
        _application = application;
        if (references != null) {
            _references = new LinkedList<ComponentReference>(references);
        }
    }

    @Override
    public QName getName() {
        return _name;
    }

    @Override
    public String getImplementation() {
        return _implementation;
    }

    @Override
    public String getInterface() {
        return _interface;
    }

    @Override
    public List<ComponentReference> getReferences() {
        if (_references == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(_references);
    }

    @Override
    public Application getApplication() {
        return _application;
    }

}
