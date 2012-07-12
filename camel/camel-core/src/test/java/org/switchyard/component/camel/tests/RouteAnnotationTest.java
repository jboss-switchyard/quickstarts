package org.switchyard.component.camel.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.camel.config.model.RouteScanner;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test used to prove the feature described in <a href="https://issues.jboss.org/browse/SWITCHYARD-921">ticket</a>.
 *
 * @author: <a href="mailto:eduardo.devera@gmail.com">Eduardo de Vera</a>
 */
public class RouteAnnotationTest {

    private SwitchYardModel _scannedModel;

    @Before
    public void setUp() throws IOException {
        RouteScanner scanner = new RouteScanner();
        List<URL> urls = new ArrayList<URL>();
        urls.add(new File("./target/test-classes").toURI().toURL());
        ScannerInput<SwitchYardModel> input = new ScannerInput<SwitchYardModel>().setURLs(urls);
        _scannedModel = scanner.scan(input).getModel();
    }

    @Test
    public void testRouteAnnnotationServiceNaming() {
        List<ComponentModel> components = _scannedModel.getComposite().getComponents();
        boolean customServiceNameFound = false;
        boolean emptyServiceNameFound = false;

        for (ComponentModel componentModel : components) {
            List<ComponentServiceModel> services = componentModel.getServices();
            for (ComponentServiceModel serviceModel : services) {
                if (serviceModel.getName().equals("CustomRouteAnnotationServiceName")) {
                    customServiceNameFound = true;
                }
                if (serviceModel.getName().equals("")) {
                    emptyServiceNameFound = true;
                }
            }
        }

        Assert.assertTrue("The custom name service must be found.", customServiceNameFound);
        Assert.assertFalse("The empty name service should have the name of the interface.", emptyServiceNameFound);
    }
}
