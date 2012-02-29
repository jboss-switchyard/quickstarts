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

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.admin.Application;
import org.switchyard.admin.Binding;
import org.switchyard.admin.ComponentReference;
import org.switchyard.admin.Reference;

/**
 * Base implementation for Reference.
 */
public class BaseReference implements Reference {
    
    private QName _name;
    private String _referenceInterface;
    private Application _application;
    private ComponentReference _promotedReference;
    private List<Binding> _gateways = new LinkedList<Binding>();
    
    /**
     * Create a new BaseReference.
     * 
     * @param name the name of the reference.
     * @param referenceInterface the interface implemented by the reference.
     * @param application the application containing the reference.
     * @param reference the component reference.
     * @param gateways the gateway types exposing the reference.
     */
    public BaseReference(QName name,
            String referenceInterface,
            Application application, 
            ComponentReference reference,
            List<Binding> gateways) {
        
        _name = name;
        _referenceInterface = referenceInterface;
        _application = application;
        _promotedReference = reference;
        _gateways = gateways;
    }

    @Override
    public Application getApplication() {
        return _application;
    }

    @Override
    public List<Binding> getGateways() {
        return _gateways;
    }

    @Override
    public ComponentReference getPromotedReference() {
       return _promotedReference;
    }

    @Override
    public String getInterface() {
        return _referenceInterface;
    }

    @Override
    public QName getName() {
        return _name;
    }
    
}
