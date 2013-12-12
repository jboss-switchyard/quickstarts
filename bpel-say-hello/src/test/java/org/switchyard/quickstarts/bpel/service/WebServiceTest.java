package org.switchyard.quickstarts.bpel.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        scanners = TransformSwitchYardScanner.class,
        mixins = {CDIMixIn.class, HTTPMixIn.class})
public class WebServiceTest {

    private HTTPMixIn httpMixIn;

    @Test
    public void sayHello() throws Exception {
        // Send a SOAP request and verify the SOAP reply is what we expected
        httpMixIn.postResourceAndTestXML(
                "http://localhost:18001/SayHelloService",
                "/xml/soap-request.xml",
                "/xml/soap-response.xml");
    }
}
