package org.switchyard.test.jca;

import javax.inject.Inject;

import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;
import org.switchyard.component.common.label.PropertyLabel;
import org.switchyard.component.jca.composer.JMSContextMapper;
import org.switchyard.component.jca.endpoint.JMSEndpoint;

@Service(JCAJMSService.class)
public class JCAJMSServiceImpl implements JCAJMSService {

    @Inject @Reference("JCAJMSStoreResult")
    private JCAJMSService _storeResult;
    @Inject @Reference("JCAJMSStoreFault")
    private JCAJMSService _storeFault;
    @Inject
    private Context _context;
    private int _declaredFaultCount = 0;
    private int _undeclaredFaultCount = 0;

    @Override
    public void onMessage(String name) {
        if (!name.equals("onMessagetest")) {
            throw new RuntimeException("expected content is 'onMessagetest' but was '" + name + "'");
        }
        final String val = _context.getProperty("testProp").getValue().toString();
        if (!val.equals("testVal")) {
            throw new RuntimeException("'testProp' property is '" + val + "' while it should be 'testVal'");
        };

        Property jmsMessageId = _context.getProperty(JMSContextMapper.HEADER_JMS_MESSAGE_ID);
        if (jmsMessageId == null) {
            throw new RuntimeException("Couldn't find javax.jms.JMSMessageID context property");
        }
        if (!jmsMessageId.hasLabel(PropertyLabel.HEADER.label())) {
            throw new RuntimeException("javax.jmsJMSMessageID context property doesn't have HEADER label");
        }
        if (jmsMessageId.getValue().toString() == null) {
            throw new RuntimeException("javax.jmsJMSMessageID context property has null value");
        };

        _storeResult.onMessage(name);
    }

    @Override
    public void onMessage_fault_rollback(String name) {
        _storeResult.onMessage(name);

        if (_undeclaredFaultCount < 4) {
            _undeclaredFaultCount++;
            _storeFault.onMessage("faultmessage");
            throw new RuntimeException("faultmessage");
        }
        
    }

    @Override
    public void onMessage_fault_commit(String name) throws JCAJMSFault {
        _storeResult.onMessage(name);

        if (_declaredFaultCount < 4) {
            _declaredFaultCount++;
            _storeFault.onMessage("faultmessage");
            _context.setProperty(Exchange.ROLLBACK_ON_FAULT, false);
            throw new JCAJMSFault("faultmessage");
        }
    }

    @Override
    public String onMessage_inout(String name) {
        return name + "_replyTo";
    }

    @Override
    public String onMessage_inout_fault(String name) throws JCAJMSFault {
        throw new JCAJMSFault(name + "_faultTo");
    }

    @Override
    public String onMessage_inout_context_property(String name) {
        _context.setProperty(JMSEndpoint.CONTEXT_PROPERTY_PREFIX + JMSEndpoint.KEY_REPLY_TO, "StoreResultQueue", Scope.EXCHANGE);
        _context.setProperty(JMSEndpoint.CONTEXT_PROPERTY_PREFIX + JMSEndpoint.KEY_MESSAGE_TYPE, "Bytes", Scope.EXCHANGE);
        return name + "_replyTo";
    }

    @Override
    public String onMessage_inout_physical_name(String name) {
        return name + "_replyTo";
    }

    @Override
    public String onMessage_inout_physical_name_fault(String name) throws JCAJMSFault {
        throw new JCAJMSFault(name + "_faultTo");
    }
}
