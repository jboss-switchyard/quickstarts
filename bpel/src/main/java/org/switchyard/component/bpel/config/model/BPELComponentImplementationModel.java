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
package org.switchyard.component.bpel.config.model;

import org.switchyard.config.model.composite.ComponentImplementationModel;

/**
 * A "bpel" ComponentImplementationModel.
 *
 */
public interface BPELComponentImplementationModel extends ComponentImplementationModel {

    /**
     * The "bpel" namespace.
     */
    public static final String DEFAULT_NAMESPACE = "http://docs.oasis-open.org/ns/opencsa/sca/200903";

    /**
     * The "bpel" implementation type.
     */
    public static final String BPEL = "bpel";
    
    /**
     * Gets the "process" attribute.
     *
     * @return the "process" attribute
     */
    public String getProcess();

    /**
     * Sets the "process" attribute.
     *
     * @param process the "process" attribute
     * @return this instance (useful for chaining)
     */
    public BPELComponentImplementationModel setProcess(String process);

    /**
     * Gets the "version" attribute.
     *
     * @return the "version" attribute
     */
    public String getVersion();

    /**
     * Sets the "version" attribute.
     *
     * @param version the "version" attribute
     * @return this instance (useful for chaining)
     */
    public BPELComponentImplementationModel setVersion(String version);

}
