package org.switchyard.test.jca;

import javax.inject.Inject;

import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.ReferenceInvoker;
import org.switchyard.component.bean.Service;
import org.switchyard.component.jca.processor.JMSProcessor;

@Service(JCAJMSReferenceService.class)
public class JCAJMSReferenceServiceImpl implements JCAJMSReferenceService {

    @Inject @Reference
    private JCAJMSReference service;

    @Inject @Reference
    private JCAJMSReferenceText serviceText;

    @Inject @Reference("JCAJMSReference")
    private ReferenceInvoker referenceInvoker;

    @Override
    public void onMessage(String name) {
        service.onMessage(name);
    }

    @Override
    public void onMessageText(String name) {
        serviceText.onMessageText(name);
    }

    @Override
    public void onMessageContextProperty(String name) throws Exception {
        referenceInvoker.newInvocation("onMessage")
                        .setProperty(JMSProcessor.CONTEXT_PROPERTY_PREFIX + JMSProcessor.KEY_DESTINATION, "ResultPropQueue")
                        .setProperty(JMSProcessor.CONTEXT_PROPERTY_PREFIX + JMSProcessor.KEY_MESSAGE_TYPE, "Bytes")
                        .invoke(name);
    }
}
