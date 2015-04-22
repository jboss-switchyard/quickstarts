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
package org.switchyard.component.bpel.osgi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;

import org.jboss.logging.Logger;
import org.osgi.framework.Bundle;
import org.switchyard.ServiceDomain;
import org.switchyard.component.bpel.riftsaw.RiftsawBPELExchangeHandler;
import org.switchyard.deploy.osgi.internal.SwitchYardContainerImpl;

/**
 * Specialized for use in OSGi environments.
 */
public class RiftsawBPELOSGiExchangeHandler extends RiftsawBPELExchangeHandler {

    private static final Logger LOG = Logger.getLogger(RiftsawBPELOSGiExchangeHandler.class);

    private final ServiceDomain _domain;

    protected RiftsawBPELOSGiExchangeHandler(ServiceDomain domain) {
        super(domain);
        _domain = domain;
    }

    @Override
    protected File getDeployment() throws Exception {
        final Bundle applicationBundle = getApplicationBundle();
        final Enumeration<URL> deploymentEntries = applicationBundle.findEntries("/", "deploy.xml", false);
        if (deploymentEntries.hasMoreElements()) {
            return expandDeployment(applicationBundle, deploymentEntries.nextElement());
        }
        throw new Exception("Bundle does not contain deploy.xml file: " + applicationBundle.getSymbolicName());
    }

    @Override
    protected String getDeploymentName() throws Exception {
        return getApplicationBundle().getSymbolicName();
    }

    private Bundle getApplicationBundle() throws Exception {
        return (Bundle) _domain.getProperty(SwitchYardContainerImpl.SWITCHYARD_DEPLOYMENT_BUNDLE);
    }

    private File expandDeployment(Bundle applicationBundle, URL deployXmlUrl) throws IOException {
        final File rootDirectory = getDataDirectory(applicationBundle);
        copyFile(deployXmlUrl, rootDirectory);
        final Enumeration<URL> bpelFiles = applicationBundle.findEntries("/", "*.bpel*", true);
        if (bpelFiles != null) {
            while (bpelFiles.hasMoreElements()) {
                copyFile(bpelFiles.nextElement(), rootDirectory);
            }
        }
        return rootDirectory;
    }

    private void copyFile(URL source, File rootDirectory) throws IOException {
        final File destination = new File(rootDirectory, source.getPath());
        if (!destination.getName().matches(".+\\.bpel(-\\d+)?") && !"deploy.xml".equals(destination.getName())) {
            LOG.warnf("Skipping file copy for \"%s\", not a *.bpel or deploy.xml file.", destination.getName());
            return;
        }
        destination.getParentFile().mkdirs();

        ReadableByteChannel input = null;
        FileChannel output = null;
        try {
            final ByteBuffer buffer = ByteBuffer.allocate(0x100000);
            input = Channels.newChannel(source.openStream());
            output = new FileOutputStream(destination).getChannel();
            while (input.read(buffer) > 0) {
                buffer.flip();
                output.write(buffer);
                buffer.clear();
            }
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File getDataDirectory(final Bundle bundle) {
        final File dataDirectory = bundle.getDataFile("");
        final File deploymentsDirectory = new File(dataDirectory, "bpel");
        if (!deploymentsDirectory.exists()) {
            deploymentsDirectory.mkdirs();
            deploymentsDirectory.deleteOnExit();
        }
        return deploymentsDirectory;
    }
}
