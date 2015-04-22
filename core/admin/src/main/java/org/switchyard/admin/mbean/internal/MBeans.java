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

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.jboss.logging.Logger;

import org.switchyard.SwitchYardException;
import org.switchyard.admin.Application;
import org.switchyard.admin.Binding;
import org.switchyard.admin.ComponentReference;
import org.switchyard.admin.ComponentService;
import org.switchyard.admin.Reference;
import org.switchyard.admin.Service;
import org.switchyard.admin.Throttling;
import org.switchyard.admin.Transformer;
import org.switchyard.admin.Validator;

/**
 * Utility class for JMX-related functions in admin API.
 */
public final class MBeans {
    
    /**
     * Domain name used for SwitchYard MBean ObjectNames.
     */
    public static final String DOMAIN = "org.switchyard.admin";
    
    static final String APPLICATION = "type=Application,name=";
    static final String SERVICE = "type=Service,name=";
    static final String REFERENCE = "type=Reference,name=";
    static final String BINDING = "type=Binding,name=";
    static final String TRANSFORMER = "type=Transformer,name=";
    static final String VALIDATOR = "type=Validator,name=";
    static final String COMPONENT_SERVICE = "type=ComponentService,name=";
    static final String COMPONENT_REFERENCE = "type=ComponentReference,name=";
    static final String LOCAL_MANAGEMENT = "type=Management.Local";
    static final String THROTTLING = "type=Throttling,service=";
    
    private static MBeanServer _server = ManagementFactory.getPlatformMBeanServer();
    private static Logger _log = Logger.getLogger(MBeans.class);
    
    private MBeans() {
        // Not for public use
    }
    
    /**
     * Register an instance of the local management MBean.
     * @param localManage local management MBean
     */
    public static void registerLocalManagement(LocalManagement localManage) {
        registerMBean(localManage, toName(DOMAIN + ":" + LOCAL_MANAGEMENT));
    }
    
    /**
     * Unregister local management MBean.
     */
    public static void unregisterLocalManagement() {
        unregisterMBean(toName(DOMAIN + ":" + LOCAL_MANAGEMENT));
    }
    
    /**
     * Creates MBeans for all manageable aspects of a SY application and registers them with 
     * the platform MBean server.
     * @param application the application to manage
     */
    public static void registerApplication(Application application) {
        ManagedApplication mApp = new ManagedApplication(application);
        registerMBean(mApp, getObjectName(application));
        
        // register composite services
        for (Service service : application.getServices()) {
            ManagedService mSvc = new ManagedService(service, mApp);
            registerMBean(mSvc, getObjectName(service));
            registerMBean(mSvc.getThrottling(), getObjectName(service, service.getThrottling()));
            // register service bindings
            for (Binding binding : service.getGateways()) {
                ManagedBinding mBind = new ManagedBinding(binding);
                registerMBean(mBind, getObjectName(service, binding));
                mSvc.addBinding(mBind);
            }
        }
        
        // register composite references
        for (Reference reference : application.getReferences()) {
            ManagedReference mRef = new ManagedReference(reference, mApp);
            registerMBean(mRef, getObjectName(reference));
            // register service bindings
            for (Binding binding : reference.getGateways()) {
                ManagedBinding mBind = new ManagedBinding(binding);
                registerMBean(mBind, getObjectName(reference, binding));
                mRef.addBinding(mBind);
            }
        }
        
        // register transformers
        for (Transformer trans : application.getTransformers()) {
            ManagedTransformer mTrans = new ManagedTransformer(trans);
            registerMBean(mTrans, getObjectName(trans));
        }
        
        // register validators
        for (Validator val : application.getValidators()) {
            ManagedValidator mVal = new ManagedValidator(val);
            registerMBean(mVal, getObjectName(val));
        }
        
        // register component services
        for (ComponentService cs : application.getComponentServices()) {
            ManagedComponentService mCS = new ManagedComponentService(cs, mApp);
            registerMBean(mCS, getObjectName(cs));
            for (ComponentReference cr : cs.getReferences()) {
                ManagedComponentReference mCR = new ManagedComponentReference(cr);
                registerMBean(mCR, getObjectName(cs, cr));
            }
        }
    }
    
    /**
     * Unregisters all MBeans for a SwitchYard application.
     * @param application all associated MBeans for this application will be unregistered.
     */
    public static void unregisterApplication(Application application) {
        // Unregister application
        unregisterMBean(getObjectName(application));
        
        // Unregister composite services
        for (Service service : application.getServices()) {
            unregisterMBean(getObjectName(service));
            unregisterMBean(getObjectName(service, service.getThrottling()));
            for (Binding binding : service.getGateways()) {
                unregisterMBean(getObjectName(service, binding));
            }
        }
        // Unregister composite references
        for (Reference reference : application.getReferences()) {
            unregisterMBean(getObjectName(reference));
            for (Binding binding : reference.getGateways()) {
                unregisterMBean(getObjectName(reference, binding));
            }
        }
        
        // Unregister transformers
        for (Transformer trans : application.getTransformers()) {
            unregisterMBean(getObjectName(trans));
        }

        // Unregister validators
        for (Validator val : application.getValidators()) {
            unregisterMBean(getObjectName(val));
        }

        // Unregister component services
        for (ComponentService cs : application.getComponentServices()) {
            unregisterMBean(getObjectName(cs));
            for (ComponentReference cr : cs.getReferences()) {
                unregisterMBean(getObjectName(cs, cr));
            }
        }
    }
    
    private static void registerMBean(Object obj, ObjectName name) {
        try {
            _server.registerMBean(obj, name);
        } catch (Exception ex) {
            _log.debug("Failed to register SwitchYard MBean: " + name, ex);
        }
    }
    
    private static void unregisterMBean(ObjectName name) throws SwitchYardException {
        try {
            _server.unregisterMBean(name);
        } catch (Exception ex) {
            _log.debug("Failed to unregister SwitchYard MBean: " + name, ex);
        }
    }

    static ObjectName getObjectName(Application application) {
        return toName(DOMAIN + ":" + APPLICATION + ObjectName.quote(application.getName().toString()));
    }
    
    static ObjectName getObjectName(Service service) {
        return toName(DOMAIN + ":" + SERVICE + ObjectName.quote(service.getName().toString()));
    }
    
    static ObjectName getObjectName(Reference reference) {
        return toName(DOMAIN + ":" + REFERENCE + ObjectName.quote(reference.getName().toString()));
    }
    
    static ObjectName getObjectName(ComponentService compSvc) {
        return toName(DOMAIN + ":" + COMPONENT_SERVICE + ObjectName.quote(compSvc.getName().toString()));
    }
    
    static ObjectName getObjectName(Transformer transformer) {
        return toName(DOMAIN + ":" + TRANSFORMER + ObjectName.quote(transformer.getFrom() + " => " + transformer.getTo()));
    }
    
    static ObjectName getObjectName(Validator validator) {
        return toName(DOMAIN + ":" + VALIDATOR + ObjectName.quote(validator.getName().toString()));
    }
    
    static ObjectName getObjectName(ComponentService service, ComponentReference reference) {
        return toName(DOMAIN + ":" 
                + COMPONENT_REFERENCE + ObjectName.quote(reference.getName().toString()) 
                + ",service=" + ObjectName.quote(service.getName().toString()));
    }
    
    static ObjectName getObjectName(Service service, Binding binding) {
        return toName(DOMAIN + ":" 
                + BINDING + ObjectName.quote(binding.getName()) 
                + ",service=" + ObjectName.quote(service.getName().toString()));
    }
    
    static ObjectName getObjectName(Reference reference, Binding binding) {
        return toName(DOMAIN + ":" 
                + BINDING + ObjectName.quote(binding.getName()) 
                + ",reference=" + ObjectName.quote(reference.getName().toString()));
    }
    
    static ObjectName getObjectName(Service service, Throttling throttling) {
        return toName(DOMAIN + ":" 
                + THROTTLING + ObjectName.quote(service.getName().toString()));
    }
    
    private static ObjectName toName(String nameStr) {
        try {
            return new ObjectName(nameStr);
        } catch (javax.management.MalformedObjectNameException badNameEx) {
            throw new IllegalArgumentException(badNameEx);
        }
    }
}
