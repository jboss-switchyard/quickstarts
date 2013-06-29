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
package org.switchyard.forge.installer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jboss.forge.shell.PluginEntry;
import org.jboss.forge.shell.InstalledPluginRegistry;

/** 
 * This Installer class is an abstraction with a main method on top of
 * Forge's InstalledPluginRegistry.installPlugin method.    It is used
 * to install forge from the SwitchYard build.xml.
 * 
 * @author Tom Cunningham
 */ 
public class Installer {
    
    public static final String FORGE_VERSION = "1.2.2.Final";

    /**
     * Add an install entry to installed.xml.
     * 
     * @param type the plugin name (i.e. org.switchyard.switchyard-forge-plugin
     * @param slot the module slot 
     */
     public void install (String plugin, String slot) {
	    try {
	    	PluginEntry pe = InstalledPluginRegistry.install(plugin, FORGE_VERSION, slot);	
	    } catch (Exception e) {
	    	System.out.println("Please make sure that you are using FORGE "
	    			+ FORGE_VERSION);
	    	e.printStackTrace();
	    }
     }

    /**
     * Main method.
     */
     public static void main (String args[]) {
		Installer i = new Installer();
		i.install(args[0], args[1]);	
     }
}
