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
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.switchyard.common.type.Classes;
import org.switchyard.config.OutputKey;
import org.switchyard.config.model.MergeScanner;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.ModelPullerScanner;
import org.switchyard.config.model.Scanner;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardModel;

/**
 * Maven mojo for configuring SwitchYard.
 * 
 * @param <M> the Model type being configured
 * 
 * @goal configure
 * @phase process-classes
 * @requiresDependencyResolution compile
 * 
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ConfigureMojo<M extends Model> extends AbstractMojo {

    private static final String SWITCHARD_XML_FILE_NAME = "switchyard.xml";
    private static final String SWITCHYARD_XML_DEFAULT_FOLDER = "META-INF";
    private static final String DEFAULT_SWITCHYARD_XML_FILE_PATH = SWITCHYARD_XML_DEFAULT_FOLDER + File.separator
            + SWITCHARD_XML_FILE_NAME;

    /**
     * @parameter
     */
    private String modelClassName;

    /**
     * @parameter
     */
    private String[] scannerClassNames = new String[] {};

    /**
     * @parameter
     */
    private File[] scanDirectories = new File[] {};

    /**
     * @parameter expression="${project.compileClasspathElements}"
     */
    private List<String> compileClasspathElements;

    /**
     * @parameter expression="${project.resources}"
     */
    private List<Resource> resources;

    /**
     * @parameter expression="${project.artifactId}"
     */
    private String artifactId;

    /**
     * @parameter expression="${project.build.outputDirectory}"
     */
    private File outputDirectory;

    /**
     * @parameter
     */
    private File outputFile;

    /**
     * @parameter
     */
    private boolean validate = true;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final List<URL> mojoURLs = new ArrayList<URL>();
        try {
            for (String compileClasspaths : compileClasspathElements) {
                mojoURLs.add(new File(compileClasspaths).toURI().toURL());
            }
            for (Resource resource : resources) {
                String path = resource.getTargetPath();
                if (path != null) {
                    File file = new File(path);
                    if (file.exists()) {
                        mojoURLs.add(file.toURI().toURL());
                    }
                }
            }
            if (outputDirectory.getAbsoluteFile().exists()) {
                mojoURLs.add(outputDirectory.getAbsoluteFile().toURI().toURL());
            }
            for (File scanDir : scanDirectories) {
                if (scanDir != null) {
                    scanDir = scanDir.getAbsoluteFile();
                    if (scanDir.exists()) {
                        mojoURLs.add(scanDir.toURI().toURL());
                    }
                }
            }
        } catch (MalformedURLException mue) {
            throw new MojoExecutionException(mue.getMessage(), mue);
        }
        ClassLoader loader = AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
            public URLClassLoader run() {
                return new URLClassLoader(mojoURLs.toArray(new URL[mojoURLs.size()]), ConfigureMojo.class.getClassLoader());
            }
        });
        ClassLoader previous = Classes.setTCCL(loader);
        Writer writer = null;
        try {
            List<Scanner<M>> scanners = new ArrayList<Scanner<M>>();
            for (String scannerClassName : scannerClassNames) {
                @SuppressWarnings("unchecked")
                Class<Scanner<M>> scannerClass = (Class<Scanner<M>>) Classes.forName(scannerClassName, loader);
                if (scannerClass != null) {
                    Scanner<M> scanner = scannerClass.newInstance();
                    scanners.add(scanner);
                }
            }
            addModelPullerScanners(scanners);
            if (modelClassName == null) {
                modelClassName = V1SwitchYardModel.class.getName();
            }
            @SuppressWarnings("unchecked")
            Class<M> modelClass = (Class<M>) Classes.forName(modelClassName, loader);
            MergeScanner<M> merge_scanner = new MergeScanner<M>(modelClass, true, scanners);
            List<URL> scannerURLs = new ArrayList<URL>();
            if (scanDirectories.length == 0) {
                scannerURLs.add(outputDirectory.toURI().toURL());
            } else {
                for (File scanDir : scanDirectories) {
                    if (scanDir != null) {
                        scannerURLs.add(scanDir.toURI().toURL());
                    }
                }
            }
            getLog().info("SwitchYard plugin scanning: " + scannerURLs);
            ScannerInput<M> scanner_input = new ScannerInput<M>().setName(artifactId).setURLs(scannerURLs);
            M model = merge_scanner.scan(scanner_input).getModel();
            if (outputFile == null) {
                File od = new File(outputDirectory, SWITCHYARD_XML_DEFAULT_FOLDER);
                if (!od.exists()) {
                    if (!od.mkdirs()) {
                        throw new Exception("mkdirs() on " + od + " failed.");
                    }
                }
                outputFile = new File(od, SWITCHARD_XML_FILE_NAME);
            }
            getLog().info("Outputting SwitchYard configuration model to " + outputFile.getAbsolutePath());
            writer = new BufferedWriter(new FileWriter(outputFile));
            model.write(writer, OutputKey.PRETTY_PRINT);
            writer.flush();
            // we write the output before we assert validity so that the user can compare the written XML to the validation error
            if (validate) {
                getLog().info("Validating SwitchYard configuration model...");
                model.assertModelValid();
            } else {
                getLog().warn("Skipping validation of SwitchYard configuration model. (Enable with <validate>true</validate>.)");
            }
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

    /**
     * Add a ModelPullerScanner for each instance of META-INF/switchyard.xml
     * that is found in a resource or scan folder. Any includes or excludes
     * specified for the resource folder are respected (i.e. if a switchyard.xml
     * file is in the resource folder, but excluded, a scanner will not be added
     * for that file). No filtering is applied to scan folders.
     * 
     * @param scanners the scanners list
     * @throws IOException if an error occurs scanning the resource folder(s)
     *             for files.
     */
    private void addModelPullerScanners(final List<Scanner<M>> scanners) throws IOException {
        for (Resource resource : resources) {
            addModelPullerScanner(new File(resource.getDirectory()).getCanonicalFile(),
                    convertInExcludes(resource.getIncludes()), convertInExcludes(resource.getExcludes()), scanners);
        }
        for (File file : scanDirectories) {
            addModelPullerScanner(file.getCanonicalFile(), null, null, scanners);
        }
    }

    /**
     * Adds a scanner if a "META-INF/switchyard.xml" file is found in the folder
     * specified by baseDir.
     * 
     * @param baseDir the directory to search.
     * @param includes file includes.
     * @param excludes file excludes.
     * @param scanners the scanners list
     * @throws IOException if an error occurs.
     */
    @SuppressWarnings("unchecked")
    private void addModelPullerScanner(final File baseDir, final String includes, final String excludes,
            final List<Scanner<M>> scanners) throws IOException {
        final File switchYardFile = new File(baseDir, DEFAULT_SWITCHYARD_XML_FILE_PATH).getCanonicalFile();
        for (File file : (List<File>) FileUtils.getFiles(baseDir, includes, excludes)) {
            file = file.getCanonicalFile();
            if (switchYardFile.equals(file)) {
                // found a match, add a scanner
                scanners.add(new ModelPullerScanner<M>(file));
                // one file per folder
                return;
            }
        }
    }

    private String convertInExcludes(List<String> cludes) {
        if (cludes == null) {
            return null;
        }
        final String converted = StringUtils.join(cludes.iterator(), ",");
        if (converted.length() == 0) {
            return null;
        }
        return converted;
    }

}
