/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.deploy.osgi.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.config.Configuration;
import org.switchyard.config.Configurations;
import org.switchyard.deploy.Component;
import org.switchyard.deploy.osgi.base.SimpleExtension;

/**
 * ComponentExtension.
 */
public class ComponentExtension extends SimpleExtension {

    /**
     * Location of component descriptor.
     */
    public static final String META_INF_COMPONENT = "META-INF/services/org.switchyard.deploy.Component";
    /**
     * List of activation types.
     */
    public static final String SWITCHYARD_TYPES = "switchyard.types";

    private final Logger _logger = LoggerFactory.getLogger(SwitchYardExtender.class);

    private final SwitchYardExtender _extender;
    private List<ServiceRegistration<Component>> _registrations = new ArrayList<ServiceRegistration<Component>>();
    private final ServiceTracker<ConfigurationAdmin, ConfigurationAdmin> _configTracker;
    private ConfigurationAdmin _configAdmin;
    /**
     * Create a new instance of ComponentExtension.
     * @param extender extender
     * @param bundle component bundle
     */
    public ComponentExtension(SwitchYardExtender extender, Bundle bundle) {
        super(bundle);
        _extender = extender;
        _configTracker = new ServiceTracker<ConfigurationAdmin, ConfigurationAdmin>(
                bundle.getBundleContext(), ConfigurationAdmin.class, null);
        _configTracker.open();
    }

    @Override
    protected void doStart() throws Exception {
        _configAdmin = _configTracker.waitForService(0);
        URL url = getBundle().getEntry(META_INF_COMPONENT);
        List<String> classNames = parse(Component.class, url);
        for (String className : classNames) {
            Component component = initializeComponent(className);
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            props.put(SWITCHYARD_TYPES, component.getActivationTypes());
            ServiceRegistration<Component> reg = getBundleContext().registerService(Component.class, component, props);
            _registrations.add(reg);
        }
    }

    @Override
    protected void doDestroy() throws Exception {
        for (ServiceRegistration<Component> reg : _registrations) {
            reg.unregister();
        }
        _configTracker.close();
    }
    
    private Component initializeComponent(String className) throws Exception {
        Class<Component> clazz = (Class<Component>) getBundle().loadClass(className);
        Component component = clazz.newInstance();
        
        // load configuration for a component - SY components all use the same package
        // naming conventions so grab the config name from the package name.  For custom
        // components (non-SY), use the name of the component returned from Component.getName().
        String configName = className.contains("org.switchyard.component") && className.indexOf(".deploy") > 0
                ? className.substring(0, className.indexOf(".deploy"))
                : component.getName();
        Configuration config = loadConfiguration(configName);
        
        // invoke the component's init method with config loaded from config admin - the 
        // destroy() method is called in ComponentRegistryImpl.unregisterComponent()
        component.init(config);
        return component;
    }
    
    private Configuration loadConfiguration(String configName) throws Exception {
        Configuration syConfig = Configurations.newConfiguration();
        org.osgi.service.cm.Configuration osgiConfig = _configAdmin.getConfiguration(configName, null);
        
        Dictionary<String, Object> props = osgiConfig.getProperties();
        if (props != null) {
            Enumeration<String> keys = props.keys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                Configuration config = Configurations.newConfiguration(key);
                config.setValue((String)props.get(key));
                syConfig.addChild(config);
            }
        }
        return syConfig;
    }

    // Parse a single line from the given configuration file, adding the name
    // on the line to the names list.
    //
    // Parse the content of the given URL as a provider-configuration file.
    //
    // @param  service
    //         The service type for which providers are being sought;
    //         used to construct error detail strings
    //
    // @param  u
    //         The URL naming the configuration file to be parsed
    //
    // @return A (possibly empty) iterator that will yield the provider-class
    //         names in the given configuration file that are not yet members
    //         of the returned set
    //
    // @throws ServiceConfigurationError
    //         If an I/O error occurs while reading from the given URL, or
    //         if a configuration-file format error is detected
    //
    private List<String> parse(Class service, URL u)
    {
        InputStream in = null;
        BufferedReader r = null;
        ArrayList<String> names = new ArrayList<String>();
        try {
            in = u.openStream();
            r = new BufferedReader(new InputStreamReader(in, "utf-8"));
            int lc = 1;
            while ((lc = parseLine(service, u, r, lc, names)) >= 0);
        } catch (IOException x) {
            fail(service, "Error reading configuration file", x);
        } finally {
            try {
                if (r != null) {
                    r.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException y) {
                fail(service, "Error closing configuration file", y);
            }
        }
        return names;
    }

    private int parseLine(Class service, URL u, BufferedReader r, int lc, List<String> names) throws IOException
    {
        String ln = r.readLine();
        if (ln == null) {
            return -1;
        }
        int ci = ln.indexOf('#');
        if (ci >= 0) {
            ln = ln.substring(0, ci);
        }
        ln = ln.trim();
        int n = ln.length();
        if (n != 0) {
            if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0)) {
                fail(service, u, lc, "Illegal configuration-file syntax");
            }
            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp)) {
                fail(service, u, lc, "Illegal provider-class name: " + ln);
            }
            for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                cp = ln.codePointAt(i);
                if (!Character.isJavaIdentifierPart(cp) && (cp != '.')) {
                    fail(service, u, lc, "Illegal provider-class name: " + ln);
                }
            }
            if (!names.contains(ln)) {
                names.add(ln);
            }
        }
        return lc + 1;
    }

    private static void fail(Class service, String msg, Throwable cause) {
        throw new IllegalStateException(service.getName() + ": " + msg, cause);
    }

    private static void fail(Class service, String msg) {
        throw new IllegalStateException(service.getName() + ": " + msg);
    }

    private static void fail(Class service, URL u, int line, String msg) {
        fail(service, u + ":" + line + ": " + msg);
    }
}
