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
package org.switchyard.forge.installer;

import org.jboss.forge.shell.InstalledPluginRegistry;

/** 
 * This Installer class is an abstraction with a main method on top of
 * Forge's InstalledPluginRegistry.installPlugin method.    It is used
 * to install forge from the SwitchYard build.xml.
 * 
 * @author Tom Cunningham
 */ 
public class Installer {

    /**
     * Add an install entry to installed.xml.
     * 
     * @param type the plugin name (i.e. org.switchyard.switchyard-forge-plugin
     * @param slot the module slot 
     */
     public void install (String plugin, String slot) {
		InstalledPluginRegistry.installPlugin(plugin, slot);	
     }

    /**
     * Main method.
     */
     public static void main (String args[]) {
		Installer i = new Installer();
		i.install(args[0], args[1]);	
     }
}
