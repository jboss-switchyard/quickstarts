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
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * Arquillian Test utilities.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class ArquillianUtil {

    public static final String QS_GID = "org.switchyard.quickstarts";
    public static final String QS_DEMO_GID = "org.switchyard.quickstarts.demos";

    /**
     * Create a SwitchYard Quickstart Demo Deployment.
     * <p/>
     * Uses "org.switchyard.quickstarts.demos" as the groupId and gets the SwitchYard
     * version from the mandatory SWITCHYARD_VERSION env property.
     *
     * @param artifactId Maven artifactId.
     * @return The Maven artifact archive.
     */
    public static JavaArchive createJarDemoDeployment(String artifactId) {
        return ShrinkwrapUtil.getSwitchYardJavaArchive(QS_DEMO_GID, artifactId);
    }

    /**
     * Create a SwitchYard Quickstart Deployment.
     * <p/>
     * Uses "org.switchyard.quickstarts" as the groupId and gets the SwitchYard
     * version from the mandatory SWITCHYARD_VERSION env property.
     *
     * @param artifactId Maven artifactId.
     * @return The Maven artifact archive.
     */
    public static JavaArchive createJarQSDeployment(String artifactId) {
        return ShrinkwrapUtil.getSwitchYardJavaArchive(QS_GID, artifactId);
    }

    /**
     * Create a SwitchYard Quickstart Demo Deployment.
     * <p/>
     * Uses "org.switchyard.quickstarts.demos" as the groupId and gets the SwitchYard
     * version from the mandatory SWITCHYARD_VERSION env property.
     *
     * @param artifactId Maven artifactId.
     * @return The Maven artifact archive.
     */
    public static WebArchive createWarDemoDeployment(String artifactId) {
        return ShrinkwrapUtil.getSwitchYardWebArchive(QS_DEMO_GID, artifactId);
    }

    /**
     * Create a SwitchYard Quickstart Deployment.
     * <p/>
     * Uses "org.switchyard.quickstarts" as the groupId and gets the SwitchYard
     * version from the mandatory SWITCHYARD_VERSION env property.
     *
     * @param artifactId Maven artifactId.
     * @return The Maven artifact archive.
     */
    public static WebArchive createWarQSDeployment(String artifactId) {
        return ShrinkwrapUtil.getSwitchYardWebArchive(QS_GID, artifactId);
    }
}
