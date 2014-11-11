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
package org.switchyard.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.osgi.spi.BundleInfo;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.container.ManifestContainer;

/**
 * This patches up the dependencies for OSGi bundles deployed as plain JAR
 * files.  Arquillian does not add these dependencies if the JAR is an OSGi
 * budle, even if the OSGi subsystem is not enabled.
 */
public class PatchingApplicationArchiveProcessor implements ApplicationArchiveProcessor {

    private static final List<String> defaultDependencies = new ArrayList<String>();

    static {
        defaultDependencies.add("deployment.arquillian-service");
        defaultDependencies.add("org.jboss.modules");
        defaultDependencies.add("org.jboss.msc");
    }

    /*
     * copied from org.jboss.as.arquillian.protocol.jmx.JMXProtocolPackager.
     * addModulesManifestDependencies()
     * 
     * We need to add the dependencies even if the bundle contains osgi
     * metadata.
     */
    @Override
    public void process(Archive<?> appArchive, TestClass testClass) {
        if (appArchive instanceof ManifestContainer<?> == false)
            throw new IllegalArgumentException("ManifestContainer expected " + appArchive);

        final Manifest manifest = getOrCreateManifest(appArchive);

        // We need it enriched...
        // Don't enrich with Modules Dependencies if this is a OSGi bundle
        if (!BundleInfo.isValidBundleManifest(manifest)) {
            return;
        }

        Attributes attributes = manifest.getMainAttributes();
        if (attributes.getValue(Attributes.Name.MANIFEST_VERSION.toString()) == null) {
            attributes.putValue(Attributes.Name.MANIFEST_VERSION.toString(), "1.0");
        }
        String value = attributes.getValue("Dependencies");
        StringBuffer moduleDeps = new StringBuffer(value != null && value.trim().length() > 0 ? value
                : "org.jboss.modules");
        for (String dep : defaultDependencies) {
            if (moduleDeps.indexOf(dep) < 0)
                moduleDeps.append("," + dep);
        }

        // log.debugf("Add dependencies: %s", moduleDeps);
        attributes.putValue("Dependencies", moduleDeps.toString());

        // Add the manifest to the archive
        ArchivePath manifestPath = ArchivePaths.create(JarFile.MANIFEST_NAME);
        appArchive.delete(manifestPath);
        appArchive.add(new Asset() {
            public InputStream openStream() {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    manifest.write(baos);
                    return new ByteArrayInputStream(baos.toByteArray());
                } catch (IOException ex) {
                    throw new IllegalStateException("Cannot write manifest", ex);
                }
            }
        }, manifestPath);
    }

    /*
     * Copied from org.jboss.as.arquillian.protocol.jmx.ManifestUtils
     */
    private Manifest getOrCreateManifest(Archive<?> archive) {
        Manifest manifest;
        try {
            Node node = archive.get(JarFile.MANIFEST_NAME);
            if (node == null) {
                manifest = new Manifest();
                Attributes attributes = manifest.getMainAttributes();
                attributes.putValue(Attributes.Name.MANIFEST_VERSION.toString(), "1.0");
            } else {
                manifest = new Manifest(node.getAsset().openStream());
            }
            return manifest;
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot obtain manifest", ex);
        }
    }

}
