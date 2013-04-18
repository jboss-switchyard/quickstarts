/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.admin.mbean.internal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.admin.Application;
import org.switchyard.admin.Binding;
import org.switchyard.admin.ComponentReference;
import org.switchyard.admin.ComponentService;
import org.switchyard.admin.Reference;
import org.switchyard.admin.Service;
import org.switchyard.admin.Transformer;
import org.switchyard.admin.Validator;
import org.switchyard.admin.base.BaseApplication;
import org.switchyard.admin.base.BaseService;
import org.switchyard.admin.base.SwitchYardBuilder;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.event.EventObserver;

public class MBeansTest {

    private static final QName TEST_APP = 
            new QName("test-application");
    private static final QName TEST_COMPOSITE_SERVICE = 
            new QName("test-composite-service");
    private static final QName TEST_COMPOSITE_REFERENCE = 
            new QName("test-composite-reference");
    private static final QName TEST_COMPONENT_SERVICE = 
            new QName("test-component-service");
    private static final QName TEST_COMPONENT_REFERENCE = 
            new QName("test-component-reference");
    
    private MBeanServer _mbs;
    private ServiceDomainManager _domainManager;
    private SwitchYardBuilder _builder;
    
    @Before
    public void setUp() throws Exception {
        _mbs = ManagementFactory.getPlatformMBeanServer();
        _domainManager = new ServiceDomainManager();
        _builder = new SwitchYardBuilder();
        _builder.init(_domainManager);
    }
    
    @After
    public void tearDown() {
        _builder.destroy();
    }

    @Test
    public void localManagementRegistration() throws Exception {
        ObjectName lmName = new ObjectName(MBeans.DOMAIN + ":" + MBeans.LOCAL_MANAGEMENT);
        Assert.assertTrue(_mbs.isRegistered(lmName));
    }
    
    @Test
    public void eventNotification() throws Exception {
        EventSink sink = new EventSink();
        ObjectName lmName = new ObjectName(MBeans.DOMAIN + ":" + MBeans.LOCAL_MANAGEMENT);
        
        // Register our event listener via JMX
        _mbs.invoke(lmName, "addObserver", 
                new Object[] {sink, DummyEvent.class},
                new String[] {EventObserver.class.getName(), "java.lang.Class"});
        
        // Generate an event
        _domainManager.getEventManager().publish(new DummyEvent());
        
        // Verify we received the event
        Assert.assertNotNull(sink.lastEvent);
        Assert.assertTrue(sink.lastEvent instanceof DummyEvent);
    }
    
    @Test
    public void testApplicationMBeans() {
        Application app = createApplication();
        MBeans.registerApplication(app);
        Assert.assertTrue(_mbs.isRegistered(MBeans.getObjectName(app)));
        MBeans.unregisterApplication(app);
        Assert.assertFalse(_mbs.isRegistered(MBeans.getObjectName(app)));
    }
    
    @Test
    public void testServiceMBeans() {
        Application app = createApplication();
        MBeans.registerApplication(app);
        Assert.assertTrue(_mbs.isRegistered(MBeans.getObjectName(app.getServices().get(0))));
        MBeans.unregisterApplication(app);
        Assert.assertFalse(_mbs.isRegistered(MBeans.getObjectName(app.getServices().get(0))));
    }
    
    @Test
    public void testReferenceMBeans() {
        Application app = createApplication();
        MBeans.registerApplication(app);
        Assert.assertTrue(_mbs.isRegistered(MBeans.getObjectName(app.getReferences().get(0))));
        MBeans.unregisterApplication(app);
        Assert.assertFalse(_mbs.isRegistered(MBeans.getObjectName(app.getReferences().get(0))));
    }
    
    @Test
    public void testTransformerMBeans() {
        Application app = createApplication();
        MBeans.registerApplication(app);
        Assert.assertTrue(_mbs.isRegistered(MBeans.getObjectName(app.getTransformers().get(0))));
        MBeans.unregisterApplication(app);
        Assert.assertFalse(_mbs.isRegistered(MBeans.getObjectName(app.getTransformers().get(0))));
    }
    
    @Test
    public void testValidatorMBeans() {
        Application app = createApplication();
        MBeans.registerApplication(app);
        Assert.assertTrue(_mbs.isRegistered(MBeans.getObjectName(app.getValidators().get(0))));
        MBeans.unregisterApplication(app);
        Assert.assertFalse(_mbs.isRegistered(MBeans.getObjectName(app.getValidators().get(0))));
    }
    
    @Test
    public void testBindingMBeans() {
        Application app = createApplication();
        MBeans.registerApplication(app);
        Service service = app.getServices().get(0);
        Assert.assertTrue(_mbs.isRegistered(MBeans.getObjectName(service, service.getGateways().get(0))));
        MBeans.unregisterApplication(app);
        Assert.assertFalse(_mbs.isRegistered(MBeans.getObjectName(app.getValidators().get(0))));
    }
    
    @Test
    public void testComponentServiceMBeans() {
        Application app = createApplication();
        MBeans.registerApplication(app);
        ComponentService cs = app.getComponentServices().get(0);
        Assert.assertTrue(_mbs.isRegistered(MBeans.getObjectName(cs)));
        Assert.assertTrue(_mbs.isRegistered(MBeans.getObjectName(cs, cs.getReferences().get(0))));
        MBeans.unregisterApplication(app);
        Assert.assertFalse(_mbs.isRegistered(MBeans.getObjectName(cs)));
        Assert.assertFalse(_mbs.isRegistered(MBeans.getObjectName(cs, cs.getReferences().get(0))));
    }
    
    private Application createApplication() {
        Application app = mock(BaseApplication.class);
        when(app.getName()).thenReturn(TEST_APP);
        
        // Services
        List<Service> services = new ArrayList<Service>();
        Service service = mock(BaseService.class);
        when (service.getName()).thenReturn(TEST_COMPOSITE_SERVICE);
        services.add(service);
        when(app.getServices()).thenReturn(services);
        
        // References
        List<Reference> references = new ArrayList<Reference>();
        Reference ref = mock(Reference.class);
        when(ref.getName()).thenReturn(TEST_COMPOSITE_REFERENCE);
        references.add(ref);
        when(app.getReferences()).thenReturn(references);
        
        // Bindings
        Binding bind = mock(Binding.class);
        List<Binding> bindings = new ArrayList<Binding>();
        bindings.add(bind);
        when(bind.getName()).thenReturn("xyz");
        when(service.getGateways()).thenReturn(bindings);
        
        // Transformers
        List<Transformer> transformers = new ArrayList<Transformer>();
        Transformer t = mock(Transformer.class);
        transformers.add(t);
        when(app.getTransformers()).thenReturn(transformers);
        
        // Validators
        List<Validator> validators = new ArrayList<Validator>();
        Validator v = mock(Validator.class);
        when(v.getName()).thenReturn(new QName("foo"));
        validators.add(v);
        when(app.getValidators()).thenReturn(validators);
        
        // Components
        List<ComponentService> compSvcs = new ArrayList<ComponentService>();
        List<ComponentReference> compRefs = new ArrayList<ComponentReference>();
        ComponentService compSvc = mock(ComponentService.class);
        ComponentReference compRef = mock(ComponentReference.class);
        when(compSvc.getName()).thenReturn(TEST_COMPONENT_SERVICE);
        when(compRef.getName()).thenReturn(TEST_COMPONENT_REFERENCE);
        compSvcs.add(compSvc);
        compRefs.add(compRef);
        when(app.getComponentServices()).thenReturn(compSvcs);
        when(compSvc.getReferences()).thenReturn(compRefs);
        
        return app;
    }
}

class EventSink implements EventObserver {
    EventObject lastEvent;

    @Override
    public void notify(EventObject event) {
        lastEvent = event;
    }
    
}

class DummyEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    
    DummyEvent() {
        super("");
    }
}
