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

package org.switchyard.extensions.java;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.ExchangePattern;
import org.switchyard.annotations.OperationTypes;
import org.switchyard.metadata.ServiceOperation;

public class JavaServiceTest {
    
    static final QName METHOD1_INPUT = new QName(
        "java:org.switchyard.extensions.java.MyStuff");           
    static final QName METHOD2_INPUT = new QName(
        "java:java.lang.Object");                            
    static final QName METHOD2_OUTPUT = new QName(
        "java:org.switchyard.extensions.java.MyStuff");             
    
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
    public void testInterfaceExtension() throws Exception {
        JavaService js = JavaService.fromClass(InheritsInterface.class);
        // There should be three operations
        Assert.assertEquals(3, js.getOperations().size());
        // Check direct and one inherited method
        Assert.assertNotNull(js.getOperation("oneMore"));
        Assert.assertNotNull(js.getOperation("method1"));
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

    @Test
    public void testOperationTypes() {
        JavaService service = JavaService.fromClass(TyepAnnotatedServiceClass.class);

        testOperationTypes("op1", service, QName.valueOf("A"), QName.valueOf("B"), null);
        testOperationTypes("op2", service, QName.valueOf("java:java.lang.String"), null, null);
        testOperationTypes("op3", service, QName.valueOf("java:java.lang.String"), QName.valueOf("java:java.lang.String"), QName.valueOf("C"));
        testOperationTypes("op4", service, QName.valueOf("java:java.lang.String"), QName.valueOf("java:java.lang.String"), null);
    }
    
    @Test
    public void testParseType() {
        final QName intType = new QName("java:java.lang.Integer");
        final Class<?> intClass = JavaService.parseType(intType);
        Assert.assertEquals(Integer.class, intClass);
    }
    
    @Test
    public void testParseTypeNull() {
        final Class<?> intClass = JavaService.parseType(null);
        Assert.assertEquals(null, intClass);
    }
    
    @Test
    public void testParseTypeMissingJavaPrefix() {
        final QName missingPrefix = new QName("java.lang.Integer");
        final Class<?> intClass = JavaService.parseType(missingPrefix);
        Assert.assertEquals(null, intClass);
    }

    private void testOperationTypes(String opName, JavaService service, QName in, QName out, QName fault) {
        ServiceOperation operation = service.getOperation(opName);
        Assert.assertEquals(in, operation.getInputType());
        Assert.assertEquals(out, operation.getOutputType());
        Assert.assertEquals(fault, operation.getFaultType());
    }

    public interface TyepAnnotatedServiceClass {

        @OperationTypes(in = "A", out = "B")
        public String op1(String input);

        @OperationTypes(out = "B") // Should have no effect since there's no return value
        public void op2(String input);

        @OperationTypes(fault = "C")
        public String op3(String input) throws RuntimeException;

        @OperationTypes(fault = "C") // Should have no effect since there's no exception
        public String op4(String input);
    }
}

// This interface has two methods eligible for service operations
interface JavaInterface {
    void method1(MyStuff param);
    MyStuff method2(Object param);
}

interface InheritsInterface extends JavaInterface {
    void oneMore(String foo);
}

// Method with no parameter
interface JavaInterfaceBadSig {
    void method1(Object foo, Object bar);
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
