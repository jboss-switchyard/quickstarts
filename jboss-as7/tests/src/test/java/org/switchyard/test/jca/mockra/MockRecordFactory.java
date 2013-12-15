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
package org.switchyard.test.jca.mockra;

import javax.resource.ResourceException;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.RecordFactory;

/**
 * MockRecordFactory.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class MockRecordFactory implements RecordFactory {

    @Override
    public MappedRecord createMappedRecord(String recordName) throws ResourceException {
        MappedRecord rec = new MockMappedRecord();
        rec.setRecordName(recordName);
        return rec;
    }

    @Override
    public IndexedRecord createIndexedRecord(String recordName) throws ResourceException {
        IndexedRecord rec = new MockIndexedRecord();
        rec.setRecordName(recordName);
        return rec;
    }

}
