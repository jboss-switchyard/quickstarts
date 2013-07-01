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
