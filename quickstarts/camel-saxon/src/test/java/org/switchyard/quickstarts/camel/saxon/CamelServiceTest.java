package org.switchyard.quickstarts.camel.saxon;

import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.Invoker;
import org.switchyard.test.MockHandler;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = CDIMixIn.class)
public class CamelServiceTest {

    private static final String REQUEST_HELLO =
"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
"   <soapenv:Header/>" +
"   <soapenv:Body>" +
"      <greet xmlns=\"urn:switchyard-quickstart:camel-saxon:0.1.0\">Douglas</greet>" +
"   </soapenv:Body>" +
"</soapenv:Envelope>";

    private static final String REQUEST_GOODBYE =
"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
"   <soapenv:Header/>" +
"   <soapenv:Body>" +
"      <greet xmlns=\"urn:switchyard-quickstart:camel-saxon:0.1.0\">Garfield</greet>" +
"   </soapenv:Body>" +
"</soapenv:Envelope>";

    private SwitchYardTestKit _testKit;

    @ServiceOperation("RoutingService.greet")
    private Invoker greet;

    @BeforeDeploy
    public void setProperties() {
        System.setProperty("org.switchyard.component.http.standalone.port", "18001");
    }

    @Test
    public void testXQueryRouting() throws Exception {
        _testKit.removeService("HelloService");
        _testKit.removeService("GoodbyeService");
        MockHandler helloService = _testKit.registerInOnlyService("HelloService");
        MockHandler goodbyeService = _testKit.registerInOnlyService("GoodbyeService");
        greet.sendInOnly(REQUEST_HELLO);
        greet.sendInOnly(REQUEST_GOODBYE);
        Thread.sleep(1000);

        LinkedBlockingQueue<Exchange> helloReceived = helloService.getMessages();
        Assert.assertNotNull(helloReceived);
        Exchange helloExchange = helloReceived.iterator().next();
        Assert.assertTrue(helloExchange.getMessage().getContent(String.class).matches(".*Douglas.*"));
        LinkedBlockingQueue<Exchange> goodbyeReceived = goodbyeService.getMessages();
        Assert.assertNotNull(goodbyeReceived);
        Exchange goodbyeExchange = goodbyeReceived.iterator().next();
        Assert.assertTrue(goodbyeExchange.getMessage().getContent(String.class).matches(".*Garfield.*"));
    }

}
