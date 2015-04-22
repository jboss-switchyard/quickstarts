package org.switchyard.component.bean.invoker;

public interface InvokerReferenceContract {

    void inOnly(String msg);
    String inOut(String msg);
    String declaredException(String msg) throws DummyException;
    String undeclaredException(String msg);
}
