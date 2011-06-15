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

package org.switchyard.console.client;

/**
 * NameTokens
 * 
 * SwitchYard specific path tokens.
 * 
 * @author Rob Cernich
 */
public class NameTokens extends org.jboss.as.console.client.core.NameTokens {

    /** The path for the main SwitchYard view. */
    public static final String SWITCH_YARD_PRESENTER = "switchyard";
    /** The subpath for the SwitchYard system configuration view. */
    public static final String SYSTEM_CONFIG_PRESENTER = "system";
    /** The subpath for the SwitchYard module configuration view. */
    public static final String MODULE_CONFIG_PRESENTER = "module";
    /** The subpath for the SwitchYard package configuration view. */
    public static final String PACKAGE_CONFIG_PRESENTER = "deployment";

}
