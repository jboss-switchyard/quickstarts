package org.switchyard.component.bean.invoker;

import javax.inject.Inject;

import org.junit.Assert;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.ReferenceInvocation;
import org.switchyard.component.bean.ReferenceInvoker;
import org.switchyard.component.bean.Service;
import org.switchyard.extensions.java.JavaService;

@Service(value = InvokerServiceContract.class, name = "InvokerService")
public class ReferenceInvokerBean implements InvokerServiceContract {
    
    @Inject @Reference(ReferenceInvokerTest.REFERENCE_A)
    private ReferenceInvoker invokerA;
    
    @Inject @Reference(ReferenceInvokerTest.REFERENCE_B)
    private ReferenceInvoker invokerB;

    @Inject @Reference("ThisReferenceDoesNotExist")
    private ReferenceInvoker invokerZ;

    @Override
    public void testA(String msg) {
        try {
            invokerA.newInvocation("inOnly").invoke();
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }
    
    @Override
    public void testB(String msg) {
        try {
            invokerB.newInvocation("inOnly").invoke();
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }
    
    @Override
    public void testZ(String msg) {
        try {
            invokerZ.newInvocation("inOnly").invoke();
            Assert.fail("Should not be able to invoke a service without a component reference");
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof IllegalStateException);
            System.out.println("Expected exception caught with missing reference:\n\t" + ex.getMessage());
        }
    }
    
    @Override
    public void propertyTest(String msg) {
        try {
            ReferenceInvocation invoke = invokerA.newInvocation("inOut")
                .setProperty(ReferenceInvokerTest.TEST_IN_PROPERTY, ReferenceInvokerTest.TEST_IN_PROPERTY)
                .invoke();
            
            // check that the out property can be read from the context
            Object outProp = invoke.getProperty(ReferenceInvokerTest.TEST_OUT_PROPERTY);
            Assert.assertNotNull(outProp);
            Assert.assertEquals(ReferenceInvokerTest.TEST_OUT_PROPERTY, outProp);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }
    
    @Override
    public void messageTest(String msg) {
        try {
            ReferenceInvocation invoker = invokerA.newInvocation("inOut");
            invoker.getMessage().setContent("message-test-in");
            invoker.invoke();
            
            // check that the correct message reference is returned from invoke
            Assert.assertNotNull(invoker.getMessage().getContent());
            Assert.assertEquals("message-test-out", invoker.getMessage().getContent());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }
    
    @Override
    public void invokeWithContent(String msg) {
        try {
            invokerA.newInvocation("inOnly").invoke("content-test-in");
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }

    @Override
    public void noOperation(String msg) {
        try {
            invokerA.newInvocation().invoke();
            Assert.fail("Should not be able to invoke a service with multiple operations without specifying op name");
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof IllegalStateException);
            System.out.println("Expected exception caught when operation name not specified:\n\t" + ex.getMessage());
        }
    }

    @Override
    public void getContract(String msg) {
        Assert.assertEquals(invokerA.getContract().getType(), JavaService.TYPE);
        Assert.assertEquals(((JavaService)invokerA.getContract()).getJavaInterface(), InvokerReferenceContract.class);
    }

    @Override
    public void declaredException(String msg) {
        try {
            invokerA.newInvocation("declaredException").invoke();
            Assert.fail("Fault returned on reference invocation not thrown as exception");
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof DummyException);
        }
    }

    @Override
    public void undeclaredException(String msg) {
        try {
            invokerA.newInvocation("undeclaredException").invoke();
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof DummyException);
        }
    }


}
