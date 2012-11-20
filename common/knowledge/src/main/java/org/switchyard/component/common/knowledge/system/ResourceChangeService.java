/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.switchyard.component.common.knowledge.system;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.drools.core.util.DelegatingSystemEventListener;
import org.drools.impl.SystemEventListenerServiceImpl.DoNothingSystemEventListener;
import org.kie.SystemEventListener;
import org.kie.SystemEventListenerFactory;
import org.kie.io.ResourceChangeScanner;
import org.kie.io.ResourceChangeScannerConfiguration;
import org.kie.io.ResourceFactory;
import org.switchyard.common.lang.Strings;
import org.switchyard.deploy.Component;

/**
 * ResourceChangeService.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class ResourceChangeService {

    private static final Logger LOGGER = Logger.getLogger(ResourceChangeService.class);

    /**
     * The "drools.resource.scanner.interval" property.
     */
    public static final String DROOLS_RESOURCE_SCANNER_INTERVAL = "drools.resource.scanner.interval";
    // TODO: Make the above and all properties listed in KnowledgeAgentConfigurationImpl configurable in SwitchYard

    private static Set<String> _names = Collections.synchronizedSet(new HashSet<String>());
    private static SystemEventListener _originalSystemEventListener = null;
    private static boolean _running = false;

    /**
     * If this is the first component calling start, then start the change services.
     * @param component the component
     */
    public static void start(Component component) {
        start(component.getName());
    }

    /**
     * If this is the first time calling start, then start the change services.
     * @param name the name to keep track of
     */
    public static synchronized void start(final String name) {
        try {
            if (_names.size() == 0 && !_running) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Starting resource change service...");
                }
                // ORDER IS IMPORTANT!
                // 1) set the system event listener to our implementation
                _originalSystemEventListener = SystemEventListenerFactory.getSystemEventListener();
                if (_originalSystemEventListener == null || _originalSystemEventListener instanceof DelegatingSystemEventListener) {
                    // We need to check for DelegatingSystemEventListener so we don't get a
                    // StackOverflowError when we set it back.  If it is a DelegatingSystemEventListener,
                    // we instead use what drools wraps by default, which is DoNothingSystemEventListener.
                    // Refer to org.drools.impl.SystemEventListenerServiceImpl for more information.
                    _originalSystemEventListener = new DoNothingSystemEventListener();
                }
                SystemEventListenerFactory.setSystemEventListener(new LoggingSystemEventListener());
                // 2) start the notifier
                ResourceFactory.getResourceChangeNotifierService().start();
                // 3) start the scanner
                ResourceChangeScanner rcs = ResourceFactory.getResourceChangeScannerService();
                String drsi = Strings.trimToNull(System.getProperty(DROOLS_RESOURCE_SCANNER_INTERVAL));
                if (drsi != null && !drsi.equals("60")) {
                    ResourceChangeScannerConfiguration rcs_conf = rcs.newResourceChangeScannerConfiguration();
                    rcs_conf.setProperty(DROOLS_RESOURCE_SCANNER_INTERVAL, drsi);
                    rcs.configure(rcs_conf);
                }
                rcs.start();
                _running = true;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Resource change service started.");
                }
            }
        } finally {
            _names.add(name);
        }
    }

    /**
     * If this is the last component calling stop, then stop the change services.
     * @param component the component
     */
    public static void stop(Component component) {
        stop(component.getName());
    }

    /**
     * If this is the last time calling stop, then stop the change services.
     * @param name the name to keep track of
     */
    public static synchronized void stop(final String name) {
        _names.remove(name);
        if (_names.size() == 0 && _running) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stopping resource change service...");
            }
            // ORDER IS IMPORTANT!
            // 1) stop the scanner
            ResourceFactory.getResourceChangeScannerService().stop();
            // 2) stop the notifier
            ResourceFactory.getResourceChangeNotifierService().stop();
            // 3) set the system event listener back to the original implementation
            SystemEventListenerFactory.setSystemEventListener(_originalSystemEventListener);
            _originalSystemEventListener = null;
            _running = false;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Resource change service stopped.");
            }
        }
    }

    private ResourceChangeService() {}

}
