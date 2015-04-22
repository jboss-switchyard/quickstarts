/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.admin.mbean.internal;

import java.util.ArrayList;
import java.util.List;

import javax.management.ObjectName;
import javax.xml.namespace.QName;

import org.switchyard.admin.Application;
import org.switchyard.admin.ComponentService;
import org.switchyard.admin.Reference;
import org.switchyard.admin.Service;
import org.switchyard.admin.Transformer;
import org.switchyard.admin.Validator;
import org.switchyard.admin.mbean.ApplicationMXBean;

/**
 * Implementation of ApplicationMXBean.
 */
public class ManagedApplication implements ApplicationMXBean {
    
    private Application _application;
    
    /**
     * Create a new managed application.
     * @param application the Application delegate from the admin API
     */
    public ManagedApplication(Application application) {
        _application = application;
    }

    @Override
    public List<String> getServices() {
        List<String> services = new ArrayList<String>();
        for (Service s : _application.getServices()) {
            services.add(s.getName().toString());
        }
        return services;
    }
    
    @Override
    public List<String> getReferences() {
        List<String> references = new ArrayList<String>();
        for (Reference ref : _application.getReferences()) {
            references.add(ref.getName().toString());
        }
        return references;
    }

    @Override
    public ObjectName getService(String serviceName) {
        ObjectName name = null;
        if (serviceName != null) {
            Service service = _application.getService(QName.valueOf(serviceName));
            if (service != null) {
                name = MBeans.getObjectName(service);
            }
        }
        return name;
    }
    
    @Override
    public ObjectName getReference(String referenceName) {
        ObjectName name = null;
        if (referenceName != null) {
            Reference reference = _application.getReference(QName.valueOf(referenceName));
            if (reference != null) {
                name = MBeans.getObjectName(reference);
            }
        }
        return name;
    }
    
    @Override
    public List<String> getComponentServices() {
        List<String> services = new ArrayList<String>();
        for (ComponentService cs : _application.getComponentServices()) {
            services.add(cs.getName().toString());
        }
        return services;
    }

    @Override
    public ObjectName getComponentService(String componentServiceName) {
        ObjectName name = null;
        if (componentServiceName != null) {
            ComponentService service = _application.getComponentService(QName.valueOf(componentServiceName));
            if (service != null) {
                name = MBeans.getObjectName(service);
            }
        }
        return name;
    }

    @Override
    public List<ObjectName> getTransformers() {
        List<ObjectName> transformers = new ArrayList<ObjectName>();
        for (Transformer t : _application.getTransformers()) {
            transformers.add(MBeans.getObjectName(t));
        }
        
        return transformers;
    }

    @Override
    public List<ObjectName> getValidators() {
        List<ObjectName> validators = new ArrayList<ObjectName>();
        for (Validator v : _application.getValidators()) {
            validators.add(MBeans.getObjectName(v));
        }
        
        return validators;
    }

    @Override
    public String getName() {
        return _application.getName().toString();
    }

    @Override
    public String getConfig() {
        return _application.getConfig().toString();
    }

}
