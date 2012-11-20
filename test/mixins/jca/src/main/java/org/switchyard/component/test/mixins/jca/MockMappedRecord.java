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
package org.switchyard.component.test.mixins.jca;

import java.util.HashMap;

import javax.resource.cci.MappedRecord;

/**
 * MockMappedRecord.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
@SuppressWarnings("rawtypes")
public class MockMappedRecord extends HashMap implements MappedRecord {

    private static final long serialVersionUID = 1L;
    private String _recordName;
    private String _description;
    
    @Override
    public String getRecordName() {
        return _recordName;
    }

    @Override
    public void setRecordName(String name) {
        _recordName = name;
    }

    @Override
    public void setRecordShortDescription(String description) {
        _description = description;
    }

    @Override
    public String getRecordShortDescription() {
        return _description;
    }
}
