package org.switchyard.karaf.test.quickstarts;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class DeploymentProbe {
    public static final String BUNDLE_NAME_KEY = "org.switchyard.karaf.test.bundleName";
    
    public DeploymentProbe(){}
    
    @Inject
    private BundleContext bundleContext;
    
    @Test
    public void testBundleActivation() {
        Assert.assertNotNull(bundleContext);
        String bundleName = System.getProperty(BUNDLE_NAME_KEY);
        Bundle bundle = null;
        for (Bundle aux : bundleContext.getBundles()) {
            if (bundleName.equals(aux.getSymbolicName())) {
                bundle = aux;
                break;
            }
        }
        Assert.assertNotNull(bundle);
        Assert.assertEquals("Bundle ACTIVE", Bundle.ACTIVE, bundle.getState());
    }
}
