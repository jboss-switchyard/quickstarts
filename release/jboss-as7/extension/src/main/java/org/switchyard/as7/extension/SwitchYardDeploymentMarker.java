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
package org.switchyard.as7.extension;

import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.as.server.deployment.DeploymentUnit;

/**
 * Marker for top level deployments that contain a switchyard.xml file.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public final class SwitchYardDeploymentMarker {

    private static final AttachmentKey<Boolean> MARKER = AttachmentKey.create(Boolean.class);

    private SwitchYardDeploymentMarker() {

    }
    /**
     * Mark the top level deployment as being a SwitchYard deployment. If the deployment is not a top level deployment the parent is
     * marked instead
     * @param unit the deployment unit.
     */
    public static void mark(DeploymentUnit unit) {
        unit.putAttachment(MARKER, Boolean.TRUE);
    }

    /**
     * Checks if the {@link DeploymentUnit} is a SwitchYard deployment.
     * @param unit the deployment unit
     * @return true if the {@link DeploymentUnit} has a switchyard.xml in any of it's resource roots
     */
    public static boolean isSwitchYardDeployment(DeploymentUnit unit) {
        return unit.getAttachment(MARKER) != null;
    }

}
