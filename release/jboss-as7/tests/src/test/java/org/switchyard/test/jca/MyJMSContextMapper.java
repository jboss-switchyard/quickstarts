package org.switchyard.test.jca;

import org.switchyard.Context;
import org.switchyard.Scope;
import org.switchyard.component.jca.composer.JMSBindingData;
import org.switchyard.component.jca.composer.JMSContextMapper;

public class MyJMSContextMapper extends JMSContextMapper {

    @Override
    public void mapFrom(JMSBindingData source, Context context) throws Exception {
        super.mapFrom(source, context);
        context.setProperty("testProp", "testVal", Scope.EXCHANGE);
    }

    @Override
    public void mapTo(Context context, JMSBindingData target) throws Exception {
        super.mapTo(context, target);
        target.getMessage().setStringProperty("testProp", "testVal");
    }
    
}
