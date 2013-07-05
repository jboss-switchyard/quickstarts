/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
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
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2011 Red Hat Inc.
 */
@Mojo(name="configure", defaultPhase=LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution=ResolutionScope.COMPILE)
public class ConfigureMojo<M extends Model> extends AbstractMojo {

    private static final String SWITCHARD_XML_FILE_NAME = "switchyard.xml";
    private static final String SWITCHYARD_XML_DEFAULT_FOLDER = "META-INF";
    private static final String DEFAULT_SWITCHYARD_XML_FILE_PATH = SWITCHYARD_XML_DEFAULT_FOLDER + File.separator + SWITCHARD_XML_FILE_NAME;

    @Parameter(property="project.artifactId")
    private String _project_artifactId;

    @Parameter(property="project.build.outputDirectory", alias="outputDirectory")
    private File _project_build_outputDirectory;

    @Parameter(property="project.compileClasspathElements")
    private List<String> _project_compileClasspathElements;

    @Parameter(property="project.resources")
    private List<Resource> _project_resources;

    @Parameter(property="validate", alias="validate")
    private boolean _validate = true;

    @Parameter(alias="modelClassName")
    private String _modelClassName;

    @Parameter(alias="outputFile")
    private File _outputFile;

    @Parameter(alias="scanDirectories")
    private File[] _scanDirectories = new File[] {};

    @Parameter(alias="scannerClassNames")
    private String[] _scannerClassNames = new String[] {};

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final List<URL> mojoURLs = new ArrayList<URL>();
        try {
            for (String compileClasspaths : _project_compileClasspathElements) {
                mojoURLs.add(new File(compileClasspaths).toURI().toURL());
            }
            for (Resource resource : _project_resources) {
                String path = resource.getTargetPath();
                if (path != null) {
                    File file = new File(path);
                    if (file.exists()) {
                        mojoURLs.add(file.toURI().toURL());
                    }
                }
            }
            if (_project_build_outputDirectory.getAbsoluteFile().exists()) {
                mojoURLs.add(_project_build_outputDirectory.getAbsoluteFile().toURI().toURL());
            }
            for (File scanDir : _scanDirectories) {
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
            for (String scannerClassName : _scannerClassNames) {
                @SuppressWarnings("unchecked")
                Class<Scanner<M>> scannerClass = (Class<Scanner<M>>)Classes.forName(scannerClassName, loader);
                if (scannerClass != null) {
                    Scanner<M> scanner = scannerClass.newInstance();
                    scanners.add(scanner);
                }
            }
            addModelPullerScanners(scanners);
            if (_modelClassName == null) {
                _modelClassName = V1SwitchYardModel.class.getName();
            }
            @SuppressWarnings("unchecked")
            Class<M> modelClass = (Class<M>)Classes.forName(_modelClassName, loader);
            MergeScanner<M> merge_scanner = new MergeScanner<M>(modelClass, true, scanners);
            List<URL> scannerURLs = new ArrayList<URL>();
            if (_scanDirectories.length == 0) {
                scannerURLs.add(_project_build_outputDirectory.toURI().toURL());
            } else {
                for (File scanDir : _scanDirectories) {
                    if (scanDir != null) {
                        scannerURLs.add(scanDir.toURI().toURL());
                    }
                }
            }
            getLog().info("SwitchYard plugin scanning: " + scannerURLs);
            ScannerInput<M> scanner_input = new ScannerInput<M>().setName(_project_artifactId).setURLs(scannerURLs);
            M model = merge_scanner.scan(scanner_input).getModel();
            if (_outputFile == null) {
                File od = new File(_project_build_outputDirectory, SWITCHYARD_XML_DEFAULT_FOLDER);
                if (!od.exists()) {
                    if (!od.mkdirs()) {
                        throw new Exception("mkdirs() on " + od + " failed.");
                    }
                }
                _outputFile = new File(od, SWITCHARD_XML_FILE_NAME);
            } else {
                // make sure output directory exists
                File od = _outputFile.getParentFile();
                if (!od.exists()) {
                    if (!od.mkdirs()) {
                        throw new Exception("mkdirs() on " + od + " failed.");
                    }
                }
            }
            getLog().info("Outputting SwitchYard configuration model to " + _outputFile.getAbsolutePath());
            writer = new BufferedWriter(new FileWriter(_outputFile));
            model.write(writer, OutputKey.PRETTY_PRINT);
            writer.flush();
            // we write the output before we assert validity so that the user can compare the written XML to the validation error
            if (_validate) {
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
     * @throws IOException if an error occurs scanning the resource folder(s) for files.
     */
    private void addModelPullerScanners(final List<Scanner<M>> scanners) throws IOException {
        for (Resource resource : _project_resources) {
            addModelPullerScanner(new File(resource.getDirectory()).getCanonicalFile(),
                    convertInExcludes(resource.getIncludes()), convertInExcludes(resource.getExcludes()), scanners);
        }
        for (File file : _scanDirectories) {
            addModelPullerScanner(file.getCanonicalFile(), null, null, scanners);
        }
    }

    /**
     * Adds a scanner if a "META-INF/switchyard.xml" file is found in the folder specified by baseDir.
     * 
     * @param baseDir the directory to search.
     * @param includes file includes.
     * @param excludes file excludes.
     * @param scanners the scanners list
     * @throws IOException if an error occurs.
     */
    @SuppressWarnings("unchecked")
    private void addModelPullerScanner(final File baseDir, final String includes, final String excludes, final List<Scanner<M>> scanners) throws IOException {
        final File switchYardFile = new File(baseDir, DEFAULT_SWITCHYARD_XML_FILE_PATH).getCanonicalFile();
        for (File file : (List<File>)FileUtils.getFiles(baseDir, includes, excludes)) {
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
