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
package org.switchyard.deploy.osgi.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.deploy.Component;
import org.switchyard.deploy.osgi.base.SimpleExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 */
public class ComponentExtension extends SimpleExtension {

    public static final String META_INF_COMPONENT = "META-INF/services/org.switchyard.deploy.Component";
    public static final String SWITCHYARD_TYPES = "switchyard.types";

    private final Logger logger = LoggerFactory.getLogger(SwitchyardExtender.class);

    private final SwitchyardExtender extender;
    private List<ServiceRegistration<Component>> registrations = new ArrayList<ServiceRegistration<Component>>();

    public ComponentExtension(SwitchyardExtender extender, Bundle bundle) {
        super(bundle);
        this.extender = extender;
    }

    @Override
    protected void doStart() throws Exception {
        URL url = getBundle().getEntry(META_INF_COMPONENT);
        List<String> classNames = parse(Component.class, url);
        for (String className : classNames) {
            Class<Component> clazz = (Class<Component>) getBundle().loadClass(className);
            Component component = clazz.newInstance();
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            props.put(SWITCHYARD_TYPES, component.getActivationTypes());
            ServiceRegistration<Component> reg = getBundleContext().registerService(Component.class, component, props);
            registrations.add(reg);
        }
    }

    @Override
    protected void doDestroy() throws Exception {
        for (ServiceRegistration<Component> reg : registrations) {
            reg.unregister();
        }
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
                if (r != null) r.close();
                if (in != null) in.close();
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
        if (ci >= 0) ln = ln.substring(0, ci);
        ln = ln.trim();
        int n = ln.length();
        if (n != 0) {
            if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0))
                fail(service, u, lc, "Illegal configuration-file syntax");
            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp))
                fail(service, u, lc, "Illegal provider-class name: " + ln);
            for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                cp = ln.codePointAt(i);
                if (!Character.isJavaIdentifierPart(cp) && (cp != '.'))
                    fail(service, u, lc, "Illegal provider-class name: " + ln);
            }
            if (!names.contains(ln))
                names.add(ln);
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
