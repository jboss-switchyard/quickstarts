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
package org.switchyard.component.jca.processor.cci;

import javax.resource.cci.IndexedRecord;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.Streamable;

import org.switchyard.component.jca.composer.RecordBindingData;
import org.switchyard.SwitchYardException;

/**
 * RecordHandler factory.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public final class RecordHandlerFactory {

    private RecordHandlerFactory() {
    }
    
    /**
     * create RecordHandler instance which supports the record type passed by parameter.
     * 
     * @param recordType record type
     * @param loader ClassLoader for application
     * @return RecordHandler instance
     */
    public static RecordHandler<? extends RecordBindingData<?>> createRecordHandler(Class<?> recordType, ClassLoader loader) {
        if (recordType.equals(MappedRecord.class)) {
            return new MappedRecordHandler();
        } else if (recordType.equals(IndexedRecord.class)) {
            return new IndexedRecordHandler();
        } else if (recordType.equals(Streamable.class)) {
            return new StreamableRecordHandler();
        } else {
            try {
                Class<?> clazz = loader.loadClass(RecordHandlerFactory.class.getPackage().getName() + "." + recordType.getSimpleName() + "RecordHandler");
                return (RecordHandler<?>)clazz.newInstance();
            } catch (Exception e) {
                throw new SwitchYardException("record type '" + recordType.getName() + "is not supported");
            }
        }
    }
}
