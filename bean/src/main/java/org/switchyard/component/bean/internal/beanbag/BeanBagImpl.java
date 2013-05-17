package org.switchyard.component.bean.internal.beanbag;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.switchyard.Context;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.component.bean.BeanBag;
import org.switchyard.internal.CompositeContext;
import org.switchyard.internal.DefaultContext;

/**
 * BeanBag provides access to the Context object for passed in service reference,
 * as well as request Context and request Message.
 */
public class BeanBagImpl implements BeanBag {
    
    @Inject
    private Context _inContext;
    
    @Inject
    private Message _outMessage;
    
    private Map<String, Context> _refContext = new HashMap<String, Context>();
    
    /**
     * Constructor.
     * @param context IN context
     * @param message OUT message
     */
    public BeanBagImpl(Context context, Message message) {
        _inContext = context;
        _outMessage = message;
    }
    
    @Override
    public Context getInContext() {
        return _inContext;
    }
    
    @Override
    public Message getOutMessage() {
        return _outMessage;
    }

    @Override
    public Context getInContext(String reference) {
        
        if (_refContext.get(reference) == null) {
            CompositeContext composite = new CompositeContext();
            composite.setContext(Scope.EXCHANGE, new DefaultContext());
            composite.setContext(Scope.MESSAGE, new DefaultContext());
            _refContext.put(reference, composite);
        }
        return _refContext.get(reference);
    }
    
}
