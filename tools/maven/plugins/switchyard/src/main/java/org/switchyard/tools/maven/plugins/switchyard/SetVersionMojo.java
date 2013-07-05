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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.common.lang.Strings;
import org.switchyard.config.Configuration;
import org.switchyard.config.ConfigurationPuller;
import org.switchyard.config.OutputKey;

/**
 * Maven mojo for setting the SwitchYard version.
 * 
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@Mojo(name="setVersion", defaultPhase=LifecyclePhase.VALIDATE, requiresDirectInvocation=true)
public class SetVersionMojo extends AbstractMojo {

    private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    private static final String XML_COPYRIGHT =
          "<!--\n"
        + "- Copyright YEAR Red Hat Inc. and/or its affiliates and other contributors.\n"
        + "-\n"
        + "- Licensed under the Apache License, Version 2.0 (the \"License\")\n"
        + "- you may not use this file except in compliance with the License.\n"
        + "- You may obtain a copy of the License at\n"
        + "- http://www.apache.org/licenses/LICENSE-2.0\n"
        + "- Unless required by applicable law or agreed to in writing, software\n"
        + "- distributed under the License is distributed on an \"AS IS\" BASIS,\n"
        + "- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
        + "- See the License for the specific language governing permissions and\n"
        + "- limitations under the License.\n"
        + "-->\n";

    @Parameter(property="project.file", required=true)
    private File _project_file;

    @Parameter(property="oldVersion", required=true)
    private String _oldVersion;

    @Parameter(property="newVersion", required=true)
    private String _newVersion;

    @Parameter(property="prettyPrint", alias="prettyPrint")
    private boolean _prettyPrint = true;

    private String _oldVersion_pom;
    private String _oldVersion_download;
    private String _oldVersion_distro;

    private String _newVersion_pom;
    private String _newVersion_download;
    private String _newVersion_distro;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        parseVersion(_oldVersion, true);
        parseVersion(_newVersion, false);
        AtomicBoolean projectModified = new AtomicBoolean(false);
        try {
            Configuration project = new ConfigurationPuller(false).pull(_project_file);
            if (project != null) {
                setVersion(project.getFirstChild("version"), _newVersion_pom, projectModified);
                Configuration project_parent = project.getFirstChild("parent");
                if (project_parent != null) {
                    Configuration project_parent_groupId = project_parent.getFirstChild("groupId");
                    if (project_parent_groupId != null) {
                        String ppgid = Strings.trimToNull(project_parent_groupId.getValue());
                        if (ppgid != null && ppgid.startsWith("org.switchyard")) {
                            setVersion(project_parent.getFirstChild("version"), _newVersion_pom, projectModified);
                        }
                    }
                }
                Configuration project_properties = project.getFirstChild("properties");
                if (project_properties != null) {
                    setVersion(project_properties.getFirstChild("switchyard.version"), _newVersion_pom, projectModified);
                    setVersion(project_properties.getFirstChild("version.switchyard.runtime"), _newVersion_pom, projectModified);
                    setVersion(project_properties.getFirstChild("version.distro"), _newVersion_distro, projectModified);
                }
                if (projectModified.get()) {
                    getLog().info(_project_file.getAbsolutePath() + " modified. Writing...");
                    write(project, _project_file);
                } else {
                    getLog().info(_project_file.getAbsolutePath() + " not modified. Skipping...");
                }
                File installerFile = new File(new File(_project_file.getParentFile(), "scripts"), "installer.properties");
                if (installerFile.exists()) {
                    final String installerOld = new StringPuller().pull(installerFile);
                    String installerNew = installerOld;
                    String[][] replacements = new String[][] {
                            new String[] {_oldVersion_pom, _newVersion_pom},
                            new String[] {_oldVersion_download, _newVersion_download},
                            new String[] {_oldVersion_distro, _newVersion_distro}
                    };
                    for (String[] replacement : replacements) {
                        replacement[0] = replacement[0].replaceAll("\\.", "\\\\.");
                        installerNew = installerNew.replaceAll(replacement[0], replacement[1]);
                    }
                    if (!installerNew.equals(installerOld)) {
                        getLog().info(installerFile.getAbsolutePath() + " modified. Writing...");
                        write(installerNew, installerFile);
                    } else {
                        getLog().info(installerFile.getAbsolutePath() + " not modified. Skipping...");
                    }
                }
            }
        } catch (IOException ioe) {
            throw new MojoExecutionException(ioe.getMessage(), ioe);
        }
    }

    private void parseVersion(String version, boolean old) throws MojoExecutionException {
        // 0.6.0.Beta1, 0.6, v0.6.Beta1 (formats: pom, distro, download)
        version = Strings.trimToNull(version);
        if (version == null) {
            throw new MojoExecutionException((old ? "old" : "new") + "Version unspecified");
        }
        String[] dotSplit = version.split("\\.", 4);
        String major = dotSplit.length > 0 ? dotSplit[0] : null;
        String minor = dotSplit.length > 1 ? dotSplit[1] : null;
        String incremental = dotSplit.length > 2 ? dotSplit[2] : null;
        String qualifier = dotSplit.length > 3 ? dotSplit[3] : null;
        boolean dash = false;
        if (incremental != null && qualifier == null) {
            String[] dashSplit = incremental.split("-", 2);
            if (dashSplit.length > 1) {
                incremental = dashSplit[0];
                qualifier = dashSplit[1];
                dash = true;
            }
        }
        boolean numbers = false;
        try {
            Integer.parseInt(major);
            Integer.parseInt(minor);
            Integer.parseInt(incremental);
            numbers = true;
        } catch (NumberFormatException nfe) {
            getLog().error(NumberFormatException.class.getSimpleName() + ": " + nfe.getMessage());
        }
        if (!numbers || qualifier == null) {
            String emsg = String.format("Could not parse %s version [%s]: major=%s, minor=%s, incremental=%s, dash=%b, qualifier=%s",
                    (old ? "old" : "new"), version, major, minor, incremental, dash, qualifier);
            throw new MojoExecutionException(emsg);
        }
        String pom = major + '.' + minor + '.' + incremental + (dash ? '-' : '.') + qualifier;
        String download = 'v' + major + '.' + minor + '.' + qualifier;
        String distro = major + '.' + minor;
        if (old) {
            _oldVersion_pom = pom;
            _oldVersion_download = download;
            _oldVersion_distro = distro;
        } else {
            _newVersion_pom = pom;
            _newVersion_download = download;
            _newVersion_distro = distro;
        }
    }

    private void setVersion(Configuration config, String newVersion, AtomicBoolean modified) {
        if (config != null) {
            String oldVersion = config.getValue();
            if (newVersion.equals(oldVersion)) {
                getLog().info(String.format("old version already matches new version: %s - skipping...", newVersion));
                return;
            }
            config.setValue(newVersion);
            modified.set(true);
        }
    }

    private void write(Configuration config, File file) throws IOException {
        Writer writer = new BufferedWriter(new FileWriter(file));
        try {
            writer.write(XML_DECLARATION);
            writer.write(XML_COPYRIGHT.replaceFirst("YEAR", new SimpleDateFormat("yyyy").format(new Date())));
            if (_prettyPrint) {
                config.write(writer, OutputKey.OMIT_XML_DECLARATION, OutputKey.PRETTY_PRINT);
            } else {
                config.write(writer, OutputKey.OMIT_XML_DECLARATION);
                writer.write("\n");
            }
            writer.flush();
        } finally {
            writer.close();
        }
    }

    private void write(String content, File file) throws IOException {
        Writer writer = new BufferedWriter(new FileWriter(file));
        try {
            writer.write(content);
            writer.flush();
        } finally {
            writer.close();
        }
    }

    /**
     * Parse test.
     * @param args unused
     * @throws Exception if something blows up
     */
    public static void main(String... args) throws Exception {
        String[] oldVersions = new String[]{"0.6.0-SNAPSHOT", "0.6.0.Beta1", "0.6.0.Final"};
        String[] newVersions = new String[]{"0.6.0.Final", "0.6.0-SNAPSHOT", "0.7.0-SNAPSHOT"};
        for (int i=0; i < oldVersions.length; i++) {
            SetVersionMojo m = new SetVersionMojo();
            m._oldVersion = oldVersions[i];
            m._newVersion = newVersions[i];
            m.parseVersion(m._oldVersion, true);
            m.parseVersion(m._newVersion, false);
            System.out.println(String.format(
                    "_oldVersion_pom[%s], _oldVersion_download[%s], _oldVersion_distro[%s], _newVersion_pom[%s], _newVersion_download[%s], _newVersion_distro[%s]",
                    m._oldVersion_pom, m._oldVersion_download, m._oldVersion_distro, m._newVersion_pom, m._newVersion_download, m._newVersion_distro));
        }
    }

}
