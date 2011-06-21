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

package org.switchyard.component.camel.config.model.mock;

import org.switchyard.component.camel.config.model.CamelBindingModel;

/**
 * Represents the configuration settings for a Mock endpoint in Camel. 
 * 
 * The Mock component provides a powerful declarative testing mechanism, 
 * which is similar to jMock in that it allows declarative expectations 
 * to be created on any Mock endpoint before a test begins. Then the test 
 * is run, which typically fires messages to one or more endpoints, and 
 * finally the expectations can be asserted in a test case to ensure the 
 * system worked as expected.
 * 
 * @author Mario Antollini
 * 
 */
public interface CamelMockBindingModel extends CamelBindingModel {
    
    /**
     * The name that uniquely identifies the endpoint.
     * @return The name that uniquely identifies the endpoint
     */
    String getName();

    /**
     * The name that uniquely identifies the endpoint.
     * @param name the name that uniquely identifies the endpoint
     * @return a reference to this Mock binding model
     */
    CamelMockBindingModel setName(String name);

    /**
     * An integer that specifies a group size for throughput logging. 
     * @return the group size for throughput logging 
     */
    Integer getReportGroup();
    
    /**
     * Specify a group size for throughput logging.
     * @param size the group size for throughput logging
     * @return a reference to this Mock binding model
     */
    CamelMockBindingModel setReportGroup(Integer size);

}
