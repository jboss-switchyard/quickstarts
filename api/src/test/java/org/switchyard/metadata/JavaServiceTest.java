
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

package org.switchyard.metadata;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.ExchangePattern;
import org.switchyard.metadata.java.JavaService;

public class JavaServiceTest {
    
    static final QName METHOD1_INPUT = new QName(
        "java:org.switchyard.metadata.MyStuff");           
    static final QName METHOD2_INPUT = new QName(
        "java:java.lang.Object");                            
    static final QName METHOD2_OUTPUT = new QName(
        "java:org.switchyard.metadata.MyStuff");             
    
    @Test
    public void testJavaInterfaceAsService() throws Exception {
        JavaService js = JavaService.fromClass(JavaInterface.class);
        // There should be two operations
        Assert.assertEquals(2, js.getOperations().size());
        
        // method1 is InOnly
        ServiceOperation method1 = js.getOperation("method1");
        Assert.assertNotNull(method1);
        Assert.assertEquals(method1.getInputType(), METHOD1_INPUT);
        Assert.assertEquals(method1.getExchangePattern(), ExchangePattern.IN_ONLY);
        
        //method2 is InOut
        ServiceOperation method2 = js.getOperation("method2");
        Assert.assertNotNull(method2);
        Assert.assertEquals(method2.getInputType(), METHOD2_INPUT);
        Assert.assertEquals(method2.getOutputType(), METHOD2_OUTPUT);
        Assert.assertEquals(method2.getExchangePattern(), ExchangePattern.IN_OUT);
    }
    
    @Test
    public void testJavaClassAsService() throws Exception {
        JavaService js = JavaService.fromClass(JavaClassOnly.class);
        // There should be one operation
        Assert.assertEquals(1, js.getOperations().size());
        // meh() is InOnly
        ServiceOperation method = js.getOperation("meh");
        Assert.assertNotNull(method);
    }
    
    @Test
    public void testJavaClassImplementsInterfaceAsService() throws Exception {
        JavaService js = JavaService.fromClass(JavaClassImplementsInterface.class);
        // There should be one operation
        Assert.assertEquals(3, js.getOperations().size());
        // meh() is InOnly
        ServiceOperation method = js.getOperation("another");
        Assert.assertNotNull(method);
    }

    @Test
    public void testJavaClassExtendsClass() throws Exception {
        JavaService js = JavaService.fromClass(JavaClassExtendsClass.class);
        // There should be one operation
        Assert.assertEquals(1, js.getOperations().size());
        // meh() is InOnly
        ServiceOperation method = js.getOperation("blorg");
        Assert.assertNotNull(method);
    }
    
    @Test
    public void testInterfaceWithBadSignature() throws Exception {
        Exception error = null;
        
        try {
            JavaService.fromClass(JavaInterfaceBadSig.class);
        }
        catch (Exception ex) { error = ex; }
        
        Assert.assertNotNull(JavaInterfaceBadSig.class.getName() + 
                " shoud not be accepted as a valid ServiceInterface", error);
    }

}

// This interface has two methods eligible for service operations
interface JavaInterface {
    void method1(MyStuff param);
    MyStuff method2(Object param);
}

// Method with no parameter
interface JavaInterfaceBadSig {
    void method1();
}

// This class has one method eligible for service operation
class JavaClassOnly {
    public void meh(Object obj) {};
    void packageMeh() {};
    @SuppressWarnings("unused")
    private void privateMeh() {};
}


// This class has one method eligible for service operation; the inherited method
// from the base class is *not* included
class JavaClassExtendsClass extends JavaClassOnly {
 public void blorg(Object obj) {};
}

// This class has three methods eligible; one from the class and two from the
// interface
class JavaClassImplementsInterface implements JavaInterface {
    public void another(Object obj) {}
    public void method1(MyStuff param) {}
    public MyStuff method2(Object param) {return null;}
}

class MyStuff { };
