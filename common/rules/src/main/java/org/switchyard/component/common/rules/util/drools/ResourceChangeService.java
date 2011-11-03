/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.common.rules.util.drools;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.drools.SystemEventListener;
import org.drools.SystemEventListenerFactory;
import org.drools.agent.impl.PrintStreamSystemEventListener;
import org.drools.core.util.DelegatingSystemEventListener;
import org.drools.io.ResourceChangeScanner;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.switchyard.deploy.Component;

/**
 * ResourceChangeService.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class ResourceChangeService {

    // TODO: make configurable
    private static final String DROOLS_RESOURCE_SCANNER_INTERVAL = "drools.resource.scanner.interval";

    private static Set<String> _componentNames = Collections.synchronizedSet(new HashSet<String>());
    private static Lock _componentNamesLock = new ReentrantLock();

    private static SystemEventListener _originalSystemEventListener = null;

    /**
     * If this is the first component calling start, then actually start the change services.
     * @param component the component
     */
    public static void start(Component component) {
        _componentNamesLock.lock();
        try {
            if (_componentNames.size() == 0) {
                // ORDER IS IMPORTANT!
                // 1) set the system event listener to our implementation
                _originalSystemEventListener = SystemEventListenerFactory.getSystemEventListener();
                if (_originalSystemEventListener == null || _originalSystemEventListener instanceof DelegatingSystemEventListener) {
                    // We need to check for DelegatingSystemEventListener so we don't get a
                    // StackOverflowError when we set it back.  If it is a DelegatingSystemEventListener,
                    // we instead use what drools wraps by default, which is PrintStreamSystemEventListener.
                    // Refer to org.drools.impl.SystemEventListenerServiceImpl for more information.
                    _originalSystemEventListener = new PrintStreamSystemEventListener();
                }
                SystemEventListenerFactory.setSystemEventListener(new LogSystemEventListener());
                // 2) start the notifier
                ResourceFactory.getResourceChangeNotifierService().start();
                // 3) start the scanner
                ResourceChangeScanner rcs = ResourceFactory.getResourceChangeScannerService();
                ResourceChangeScannerConfiguration rcs_conf = rcs.newResourceChangeScannerConfiguration();
                // TODO: make configurable
                rcs_conf.setProperty(DROOLS_RESOURCE_SCANNER_INTERVAL, "60");
                rcs.configure(rcs_conf);
                rcs.start();
            }
        } finally {
            _componentNames.add(component.getName());
            _componentNamesLock.unlock();
        }
    }

    /**
     * If this is the last component calling stop, then actually stop the change services.
     * @param component the component
     */
    public static void stop(Component component) {
        _componentNamesLock.lock();
        try {
            _componentNames.remove(component.getName());
            if (_componentNames.size() == 0) {
                // ORDER IS IMPORTANT!
                // 1) stop the scanner
                ResourceFactory.getResourceChangeScannerService().stop();
                // 2) stop the notifier
                ResourceFactory.getResourceChangeNotifierService().stop();
                // 3) set the system event listener back to the original implementation
                SystemEventListenerFactory.setSystemEventListener(_originalSystemEventListener);
            }
        } finally {
            _componentNamesLock.unlock();
        }
    }

    private ResourceChangeService() {}

}
