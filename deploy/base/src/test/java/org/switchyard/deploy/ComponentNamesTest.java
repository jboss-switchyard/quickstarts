package org.switchyard.deploy;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.ServiceReference;
import org.switchyard.internal.ServiceReferenceImpl;

public class ComponentNamesTest {
    
    private static final QName COMPONENT_NAME = new QName("urn:foo", "componentName");
    private static final QName SERVICE_NAME = new QName("urn:foo", "serviceName");

    @Test
    public void testQualifyQNames() {
        QName qualified = ComponentNames.qualify(COMPONENT_NAME, SERVICE_NAME);
        Assert.assertEquals(COMPONENT_NAME.getNamespaceURI(), qualified.getNamespaceURI());
        Assert.assertEquals(
                COMPONENT_NAME.getLocalPart() + "/" + SERVICE_NAME.getLocalPart(), 
                qualified.getLocalPart());
    }
    
    @Test
    public void testQualifyStrings() {
        QName qualified = ComponentNames.qualify(
                COMPONENT_NAME.getLocalPart(),
                SERVICE_NAME.getLocalPart(),
                COMPONENT_NAME.getNamespaceURI());
        
        Assert.assertEquals(COMPONENT_NAME.getNamespaceURI(), qualified.getNamespaceURI());
        Assert.assertEquals(
                COMPONENT_NAME.getLocalPart() + "/" + SERVICE_NAME.getLocalPart(), 
                qualified.getLocalPart());
    }
    
    @Test
    public void testUnqualifyReference() {
        ServiceReference reference = new ServiceReferenceImpl(
                ComponentNames.qualify(COMPONENT_NAME, SERVICE_NAME), null, null, null);
        
        QName unqualified = ComponentNames.unqualify(reference);
        
        Assert.assertEquals(COMPONENT_NAME.getNamespaceURI(), unqualified.getNamespaceURI());
        Assert.assertEquals(SERVICE_NAME, unqualified);
    }
    
    @Test
    public void testUnqualifyQName() {
        QName qualified = ComponentNames.qualify(COMPONENT_NAME, SERVICE_NAME);
        QName unqualified = ComponentNames.unqualify(qualified);
        
        Assert.assertEquals(COMPONENT_NAME.getNamespaceURI(), unqualified.getNamespaceURI());
        Assert.assertEquals(SERVICE_NAME, unqualified);
    }
}
