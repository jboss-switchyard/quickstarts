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
package org.switchyard.component.camel.config.model.v1;

import java.io.File;
import java.net.URI;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;


/**
 * A binding for Camel's file component.
 * 
 * @author Daniel Bevenius
 *
 */
public class V1CamelFileBindingModel extends V1BaseCamelBindingModel {
    
    /**
     * The name of this binding type ("binding.file").
     */
    public static final String FILE = "file";
    
    /**
     * The name of the 'inputDir' element.
     */
    public static final String INPUT_DIR = "inputDir";
    
    private File _inputDir;
    
    /**
     * Sole constructor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelFileBindingModel(final Configuration config, final Descriptor desc) {
        super(config, desc);
    }

    public File getInputDir() {
        if (_inputDir == null) {
            final Configuration childConfig = getModelConfiguration().getFirstChild(INPUT_DIR);
            _inputDir = new File(childConfig.getValue());
        }
        return _inputDir;
    }
    
    @Override
    public URI getComponentURI() {
        return _inputDir.toURI();
    }
    
}
