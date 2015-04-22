package org.switchyard.transform.smooks.internal;

import org.jboss.logging.Logger;
import org.milyn.Smooks;
import org.milyn.SmooksOSGIFactory;
import org.osgi.framework.Bundle;
import org.switchyard.ServiceDomain;

/**
 * Smooks producer to create Smooks instance through the SmooksOSGIFactory.
 */
public class OsgiSmooksProducer implements SmooksProducer {

    private static final String SWITCHYARD_DEPLOYMENT_BUNDLE = "switchyard.deployment.bundle";
    private static Logger _logger = Logger.getLogger(OsgiSmooksProducer.class);

    @Override
    public Smooks createSmooks(ServiceDomain domain, String config) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Creating Smooks instance - Domain: " + domain + ", Smooks Config: " + config);
        }

        if (domain != null && domain.getProperties() != null) {
            Bundle bundle = (Bundle)domain.getProperty(SWITCHYARD_DEPLOYMENT_BUNDLE);
            if (bundle != null) {
                try {
                    return new SmooksOSGIFactory(bundle).createInstance(config);
                } catch (Exception e) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(e);
                    }
                }
            }
        }
        return null;
    }

}
