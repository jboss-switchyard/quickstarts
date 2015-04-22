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

import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
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
    
    /**
     * Create a SwitchYard Quickstart Deployment.
     * <p/>
     * Uses "org.switchyard.quickstarts" as the groupId and gets the SwitchYard
     * version from the mandatory SWITCHYARD_VERSION env property.
     *
     * @param artifactId Maven artifactId.
     * @return The Maven artifact archive.
     */
    public static EnterpriseArchive createEarQSDeployment(String artifactId) {
        return ShrinkwrapUtil.getSwitchYardEarArchive(QS_GID, artifactId);
    }
}
