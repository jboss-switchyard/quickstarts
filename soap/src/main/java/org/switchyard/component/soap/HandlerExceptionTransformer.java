package org.switchyard.component.soap;

import org.switchyard.HandlerException;
import org.switchyard.component.soap.util.SOAPUtil;
import org.switchyard.component.soap.util.WSDLUtil;
import org.switchyard.internal.transform.BaseTransformer;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.lang.reflect.InvocationTargetException;

/**
 * Base {@link HandlerException} to SOAP fault transformer.
 *
 * @param <F> From type.
 * @param <T> To type.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class HandlerExceptionTransformer<F extends HandlerException, T extends SOAPMessage> extends BaseTransformer<F, T> {

    @Override
    public String getFrom() {
        return HandlerException.MESSAGE_TYPE;
    }

    @Override
    public String getTo() {
        return WSDLUtil.SOAP_FAULT_MESSAGE_TYPE;
    }

    @Override
    public SOAPMessage transform(HandlerException from) {
        try {
            Throwable cause = from.getCause();
            if (cause instanceof InvocationTargetException) {
                return SOAPUtil.generateFault(cause.getCause());
            } else {
                return SOAPUtil.generateFault(from);
            }
        } catch (SOAPException e1) {
            // TODO: We're in a fault on a fault type situation now... should generateFault be throwing exceptions??
            throw new IllegalStateException("Unexpected SOAPException when generating a SOAP Fault message.", from);
        }
    }
}
