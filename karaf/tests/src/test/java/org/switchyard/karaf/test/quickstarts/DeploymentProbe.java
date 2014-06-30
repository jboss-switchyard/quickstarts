package org.switchyard.karaf.test.quickstarts;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.switchyard.deploy.osgi.SwitchYardEvent;
import org.switchyard.deploy.osgi.SwitchYardListener;

public class DeploymentProbe {
    public static final String BUNDLE_NAME_KEY = "org.switchyard.karaf.test.bundleName";

    public DeploymentProbe() {
    }

    @Inject
    private BundleContext _bundleContext;
    private boolean _activated;

    @Test
    public void testBundleActivation() {
        Assert.assertNotNull(_bundleContext);
        String bundleName = System.getProperty(BUNDLE_NAME_KEY);
        Bundle bundle = null;
        for (Bundle aux : _bundleContext.getBundles()) {
            if (bundleName.equals(aux.getSymbolicName())) {
                bundle = aux;
                break;
            }
        }
        Assert.assertNotNull(bundle);
        Assert.assertEquals("Bundle ACTIVE", Bundle.ACTIVE, bundle.getState());

        // Wait for SwitchYard application activation
        final CountDownLatch latch = new CountDownLatch(1);
        final long bundleId = bundle.getBundleId();
        final SwitchYardListener listener = new SwitchYardListener() {
            @Override
            public void switchyardEvent(SwitchYardEvent event) {
                if (bundleId == event.getBundle().getBundleId()) {
                    switch (event.getType()) {
                    case SwitchYardEvent.CREATED:
                        _activated = true;
                        // fall through
                    case SwitchYardEvent.FAILURE:
                        latch.countDown();
                        break;
                    default:
                        break;
                    }
                }
            }
        };
        final ServiceRegistration<SwitchYardListener> registration = _bundleContext.registerService(
                SwitchYardListener.class, listener, null);
        try {
            latch.await(120000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        }
        registration.unregister();
        Assert.assertTrue("SwitchYard bundle activation failed", _activated);
    }
}
