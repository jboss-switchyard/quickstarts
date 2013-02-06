package org.switchyard.quickstarts.bpm.service;

//import org.switchyard.test.BeforeDeploy;
//import org.switchyard.test.mixins.PropertyMixIn;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        scanners = TransformSwitchYardScanner.class,
        mixins = {CDIMixIn.class, HTTPMixIn.class})
        //mixins = {CDIMixIn.class, PropertyMixIn.class, HTTPMixIn.class})
public class WebServiceTest {

    //private PropertyMixIn propMixIn;
    private HTTPMixIn httpMixIn;

    //@BeforeDeploy
    //public void setTestProperties() {
    //    propMixIn.set("soapPort", Integer.valueOf(18002));
    //}

    @Test
    public void webServiceShipped() throws Exception {
        // Send a SOAP request and verify the SOAP reply is what we expected
        httpMixIn.postResourceAndTestXML(
                "http://localhost:18001/swydws/ProcessOrder", "/xml/soap-request.xml", "/xml/soap-response.xml");
                //"http://localhost:18002/swydws/ProcessOrder", "/xml/soap-request.xml", "/xml/soap-response.xml");
    }
}
