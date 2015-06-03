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

package org.switchyard.test;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.Assert;

/**
 * Shrinkwrap utilities.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public final class ShrinkwrapUtil {

    /**
     * Current SwitchYard version test Environment variable.
     */
    public static final String SWITCHYARD_VERSION = "SWITCHYARD_VERSION";

    private static final String LOCAL_MAVEN_REPO =
        System.getProperty("maven.repo.local") != null
            ? System.getProperty("maven.repo.local")
               : (System.getProperty("user.home") + File.separatorChar + ".m2" + File.separatorChar + "repository");

    private ShrinkwrapUtil() {
    }

    /**
     * Get a SwitchYard maven Java Artifact Archive.
     * <p/>
     * Gets the SwitchYard version from the mandatory {@link #SWITCHYARD_VERSION} env property.
     *
     * @param groupId    Maven groupId
     * @param artifactId Maven artifactId.
     * @return The Maven artifact archive.
     */
    public static JavaArchive getSwitchYardJavaArchive(String groupId, String artifactId) {
        return getJavaArchive(groupId, artifactId, getSwitchYardVersion());
    }

    /**
     * Get a maven Java Artifact Archive.
     *
     * @param groupId    Maven groupId
     * @param artifactId Maven artifactId.
     * @param version    Artifact version.
     * @return The Maven artifact archive.
     */
    public static JavaArchive getJavaArchive(String groupId, String artifactId, String version) {
        return getArchive(groupId, artifactId, version, JavaArchive.class, "jar");
    }

    /**
     * Get a SwitchYard maven Web Artifact Archive.
     * <p/>
     * Gets the SwitchYard version from the mandatory {@link #SWITCHYARD_VERSION} env property.
     *
     * @param groupId    Maven groupId
     * @param artifactId Maven artifactId.
     * @return The Maven artifact archive.
     */
    public static WebArchive getSwitchYardWebArchive(String groupId, String artifactId) {
        return getWebArchive(groupId, artifactId, getSwitchYardVersion());
    }

    /**
     * Get a maven Web Artifact Archive.
     *
     * @param groupId    Maven groupId
     * @param artifactId Maven artifactId.
     * @param version    Artifact version.
     * @return The Maven artifact archive.
     */
    public static WebArchive getWebArchive(String groupId, String artifactId, String version) {
        return getArchive(groupId, artifactId, version, WebArchive.class, "war");
    }
    
    /**
     * Get a SwitchYard maven Enterprise Artifact Archive.
     * <p/>
     * Gets the SwitchYard version from the mandatory {@link #SWITCHYARD_VERSION} env property.
     *
     * @param groupId    Maven groupId
     * @param artifactId Maven artifactId.
     * @return The Maven artifact archive.
     */
    public static EnterpriseArchive getSwitchYardEarArchive(String groupId, String artifactId) {
        return getEarArchive(groupId, artifactId, getSwitchYardVersion());
    }

    /**
     * Get a maven Enterprise Artifact Archive.
     *
     * @param groupId    Maven groupId
     * @param artifactId Maven artifactId.
     * @param version    Artifact version.
     * @return The Maven artifact archive.
     */
    public static EnterpriseArchive getEarArchive(String groupId, String artifactId, String version) {
        return getArchive(groupId, artifactId, version, EnterpriseArchive.class, "ear");
    }

    /**
     * Get a maven Artifact Archive.
     *
     * @param groupId    Maven groupId
     * @param artifactId Maven artifactId.
     * @param version    Artifact version.
     * @param archiveType The artifact type.
     * @param fileExtension The artifact file extension.
     * @return The Maven artifact archive.
     *
     * @param <A> Archive type.
     */
    public static <A extends Archive> A getArchive(String groupId, String artifactId, String version, Class<A> archiveType, String fileExtension) {
        Assert.assertNotNull("'groupId' argument is null.", groupId);
        Assert.assertNotNull("'artifactId' argument is null.", artifactId);
        Assert.assertNotNull("'version' argument is null.", version);

        File artifactFile = new File(LOCAL_MAVEN_REPO,
                groupId.replace(".", "/")
                        + "/" + artifactId
                        + "/" + version
                        + "/" + artifactId + "-" + version + "." + fileExtension);

        if (!artifactFile.isFile()) {
            String artifact = groupId + ":" + artifactId + ":" + version;
            Assert.fail("Failed to resolve artifact '" + artifact + "'.  The artifact must be declared as a dependency in your POM, thereby making it available in your local repository.");
        }

        A archive = ShrinkWrap.create(ZipImporter.class, artifactFile.getName()).importFrom(convert(artifactFile)).as(archiveType);

        return archive;
    }



    /**
     * Get the SwitchYard version being tested.
     * <p/>
     * Gets the SwitchYard version from the mandatory {@link #SWITCHYARD_VERSION} env property.
     *
     * @return The SwitchYard version being tested.
     */
    public static String getSwitchYardVersion() {
        String version = System.getenv(SWITCHYARD_VERSION);

        if (version == null) {
            Assert.fail("Test Environment variable '" + SWITCHYARD_VERSION + "' is not configured.  "
                    + "\n\t\t- If running the test in your IDE, set this Environment variable (in the test Run/Debug Configuration) to the current version of SwitchYard (maven artifact version)."
                    + "\n\t\t- If running the tests through Maven, make sure that the surefire plugin sets this Environment variable to the current version of SwitchYard (maven artifact version).");
        }
        version = version.trim();
        if (version.length() == 0) {
            Assert.fail("Test Environment variable '" + SWITCHYARD_VERSION + "' not configured.  If running the test in your IDE, set this Environment variable to the current version od SwitchYard.");
        }

        return version;
    }

    // converts a file to a ZIP file
    private static ZipFile convert(File file) {
        try {
            return new ZipFile(file);
        } catch (ZipException e) {
            throw new RuntimeException("Unable to treat dependency artifact \"" + file.getAbsolutePath() + "\" as a ZIP file", e);
        } catch (IOException e) {
            throw new RuntimeException("Unable to access artifact file at \"" + file.getAbsolutePath() + "\".", e);
        }
    }
}
