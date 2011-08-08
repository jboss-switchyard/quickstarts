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

package org.switchyard.test;

import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Assert;

import java.util.Collection;

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

    private ShrinkwrapUtil() {
    }

    /**
     * Get a SwitchYard maven Artifact Archive.
     * <p/>
     * Gets the SwitchYard version from the mandatory {@link #SWITCHYARD_VERSION} env property.
     *
     * @param groupId    Maven groupId
     * @param artifactId Maven artifactId.
     * @return The Maven artifact archive.
     */
    public static JavaArchive getSwitchYardJavaArchive(String groupId, String artifactId) {
        Collection<JavaArchive> artifactArchives = getSwitchYardJavaArchives(groupId, artifactId);

        // Multiple artifacts can be returned (transitive dependencies), but it appears as though
        // the first artifact in the Collection is the artifact being sought.
        return artifactArchives.iterator().next();
    }

    /**
     * Get a SwitchYard maven Artifact Archive and all it's transitive dependencies.
     * <p/>
     * Gets the SwitchYard version from the mandatory {@link #SWITCHYARD_VERSION} env property.
     *
     * @param groupId    Maven groupId
     * @param artifactId Maven artifactId.
     * @return The Collection of Maven artifact archives.
     */
    public static Collection<JavaArchive> getSwitchYardJavaArchives(String groupId, String artifactId) {
        return getJavaArchives(groupId, artifactId, getSwitchYardVersion());
    }

    /**
     * Get a maven Artifact Archive.
     *
     * @param groupId    Maven groupId
     * @param artifactId Maven artifactId.
     * @param version    Artifact version.
     * @return The Maven artifact archive.
     */
    public static JavaArchive getJavaArchive(String groupId, String artifactId, String version) {
        Collection<JavaArchive> artifactArchives = getJavaArchives(groupId, artifactId, version);

        // Multiple artifacts can be returned (transitive dependencies), but it appears as though
        // the first artifact in the Collection is the artifact being sought.
        return artifactArchives.iterator().next();
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

    /**
     * Get a maven Artifact Archive and all it's transitive dependencies.
     *
     * @param groupId    Maven groupId
     * @param artifactId Maven artifactId.
     * @param version    Artifact version.
     * @return The Collection of Maven artifact archives.
     */
    public static Collection<JavaArchive> getJavaArchives(String groupId, String artifactId, String version) {
        Assert.assertNotNull("'groupId' argument is null.", groupId);
        Assert.assertNotNull("'artifactId' argument is null.", artifactId);
        Assert.assertNotNull("'version' argument is null.", version);

        String artifact = groupId + ":" + artifactId + ":" + version;
        Collection<JavaArchive> resolvedArtifacts = DependencyResolvers.use(MavenDependencyResolver.class)
                .loadReposFromPom("./pom.xml").artifact(artifact).resolveAs(JavaArchive.class);

        if (resolvedArtifacts.isEmpty()) {
            Assert.fail("Failed to resolve artifact '" + artifact + "'.");
        }

        return resolvedArtifacts;
    }
}
