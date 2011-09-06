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
package org.switchyard.component.bpel.config.model.v1;

import static org.switchyard.component.bpel.process.ProcessConstants.PROCESS;
import static org.switchyard.component.bpel.process.ProcessConstants.VERSION;

import org.switchyard.component.bpel.config.model.BPELComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1ComponentImplementationModel;

/**
 * A "bpel" implementation of a ComponentImplementationModel.
 *
 */
public class V1BPELComponentImplementationModel extends V1ComponentImplementationModel implements BPELComponentImplementationModel {

    /**
     * Default constructor for application use.
     */
    public V1BPELComponentImplementationModel() {
        super(BPEL, DEFAULT_NAMESPACE);
    }

    /**
     * Constructor for Marshaller use (ie: V1BPELMarshaller).
     *
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1BPELComponentImplementationModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    public String getProcess() {
        return getModelAttribute(PROCESS);
    }

    /**
     * {@inheritDoc}
     */
    public BPELComponentImplementationModel setProcess(String process) {
        setModelAttribute(PROCESS, process);
        return this;
    }
    
    /**
     * Gets the "version" attribute.
     *
     * @return the "version" attribute
     */
    public String getVersion() {
        return getModelAttribute(VERSION);
    }

    /**
     * Sets the "version" attribute.
     *
     * @param version the "version" attribute
     * @return this instance (useful for chaining)
     */
    public BPELComponentImplementationModel setVersion(String version) {
        setModelAttribute(VERSION, version);
        return this;
    }

}
