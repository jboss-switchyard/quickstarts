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


package org.switchyard.tools.forge;

import javax.inject.Inject;

import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.shell.Shell;
import org.jboss.seam.forge.shell.plugins.Plugin;

/**
 * Base Forge plugin implementation for SwitchYard services.
 */
public abstract class AbstractPlugin implements Plugin {

    @Inject
    private Shell _shell;
    
    /**
     * Convenient access to current shell.
     * @return reference to current shell.
     */
    protected Shell getShell() {
        return _shell;
    }
    
    /**
     * Convenient access to current project.
     * @return reference to current project.
     */
    protected Project getProject() {
        return _shell.getCurrentProject();
    }
}
