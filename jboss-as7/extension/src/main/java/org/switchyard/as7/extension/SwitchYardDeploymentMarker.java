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
