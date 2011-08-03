package ${package.name};

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config=SwitchYardTestCaseConfig.SWITCHYARD_XML)
public class ${service.name}Test {
    
    @ServiceOperation("${service.name}")
    private Invoker service;
    
    @Test
    public void sendMessage() {
        service.sendInOnly("Message content goes here!");
    }
}