/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.clojure.config.model;

import org.switchyard.config.model.NamedModel;

/**
 * Configuration model for a 'script' element containing an 'inline' clojure script.
 * 
 * @author Daniel Bevenius
 *
 */
public interface ClojureScriptModel extends NamedModel {
    
    /**
     * The script element name.
     */
    String SCRIPT = "script";

    /**
     * Gets the script content from the 'script' element.
     * 
     * @return String The clojure script.
     */
    public abstract String getScript();

    /**
     * Sets the Clojure script.
     * 
     * @param script The Clojure script to set.
     * @return {@link V1ClojureScriptModel} this object ref to support method chaining.
     */
    public abstract ClojureScriptModel setScript(final String script);

}
