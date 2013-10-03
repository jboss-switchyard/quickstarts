package org.switchyard.component.bean.invoker;

public interface InvokerServiceContract {

    void testA(String msg);
    void testB(String msg);
    void testZ(String msg);
    void propertyTest(String msg);
    void noOperation(String msg);
    void getContract(String msg);
    void invokeWithContent(String msg);
    void messageTest(String msg);
}
