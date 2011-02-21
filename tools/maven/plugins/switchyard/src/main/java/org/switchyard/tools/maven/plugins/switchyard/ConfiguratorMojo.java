/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.tools.maven.plugins.switchyard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.tools.ant.DirectoryScanner;
import org.switchyard.config.model.switchyard.DefaultSwitchYardScanner;
import org.switchyard.config.model.switchyard.MergeSwitchYardScanner;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.SwitchYardScanner;
import org.switchyard.config.util.Classes;

/**
 * ConfiguratorMojo.
 *
 * @goal configurator
 * @phase process-classes
 * @requiresDependencyResolution compile
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ConfiguratorMojo extends AbstractMojo {

    /**
     * @parameter
     */
    private String[] scannerClassNames = new String[]{};

    /**
     * @parameter expression="${project.compileClasspathElements}"
     */
    private List<String> compileClasspathElements;

    /**
     * @parameter expression="${project.build.resources}"
     */
    private List<Resource> buildResources;

    /**
     * @parameter expression="${project.basedir}"
     */
    private File basedir;

    /**
     * @parameter
     */
    private String[] includes = new String[]{};

    /**
     * @parameter
     */
    private String[] excludes = new String[]{};

    /**
     * @parameter expression="${project.build.outputDirectory}"
     */
    private File outputDirectory;

    /**
     * @parameter
     */
    private File outputFile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final List<URL> urls = new ArrayList<URL>();
        try {
            for (String path : compileClasspathElements) {
                urls.add(new File(path).toURI().toURL());
            }
            File defaultResourceDir = new File(basedir, "src/main/resources");
            if (defaultResourceDir.exists()) {
                Resource defaultResource = new Resource();
                defaultResource.setTargetPath(defaultResourceDir.getAbsolutePath());
                buildResources.add(defaultResource);
            }
            for (Resource resource : buildResources) {
                String path = resource.getTargetPath();
                if (path != null) {
                    urls.add(new File(path).toURI().toURL());
                }
            }
        } catch (MalformedURLException mue) {
            throw new MojoExecutionException(mue.getMessage(), mue);
        }
        ClassLoader loader = AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
            public URLClassLoader run() {
                return new URLClassLoader(urls.toArray(new URL[urls.size()]), ConfiguratorMojo.class.getClassLoader());
            }
        });
        ClassLoader previous = Classes.setTCCL(loader);
        Writer writer = null;
        DirectoryScanner ds = new DirectoryScanner();
        ds.setIncludes(includes);
        ds.setExcludes(excludes);
        ds.setBasedir(outputDirectory);
        try {
            ds.scan();
            Set<String> paths = new LinkedHashSet<String>();
            for (String path : ds.getIncludedFiles()) {
                paths.add(path);
            }
            List<SwitchYardScanner> scanners = new ArrayList<SwitchYardScanner>();
            for (String scannerClassName : scannerClassNames) {
                Class<?> scannerClass = Classes.forName(scannerClassName, loader);
                SwitchYardScanner scanner = (SwitchYardScanner)scannerClass.newInstance();
                scanners.add(scanner);
            }
            scanners.add(new DefaultSwitchYardScanner());
            MergeSwitchYardScanner merge_scanner = new MergeSwitchYardScanner(true, scanners);
            SwitchYardModel switchyard = merge_scanner.merge(paths);
            if (outputFile == null) {
                File od = new File(outputDirectory, "META-INF");
                if (!od.exists()) {
                    od.mkdirs();
                }
                outputFile = new File(od, "switchyard.xml");
            }
            writer = new BufferedWriter(new FileWriter(outputFile));
            switchyard.write(writer);
        } catch (Throwable t) {
            throw new MojoExecutionException(t.getMessage(), t);
        } finally {
            Classes.setTCCL(previous);
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe) {
                    throw new MojoExecutionException(ioe.getMessage(), ioe);
                }
            }
        }
    }

}
