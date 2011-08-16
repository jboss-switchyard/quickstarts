package ${package.name};

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins=CDIMixIn.class, config=SwitchYardTestCaseConfig.SWITCHYARD_XML)
public class ${service.name}Test {
    
    @ServiceOperation("${service.name}")
    private Invoker service;
    
    @Test
    public void sendMessage() {
        service.sendInOnly("Message content goes here!");
    }
}