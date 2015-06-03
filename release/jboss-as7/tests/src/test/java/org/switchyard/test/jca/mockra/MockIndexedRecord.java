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

import java.util.ArrayList;

import javax.resource.cci.IndexedRecord;

/**
 * MockIndexedRecord.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
@SuppressWarnings("rawtypes")
public class MockIndexedRecord extends ArrayList implements IndexedRecord {

    private static final long serialVersionUID = 1341376132007016249L;
    private String _recordName;
    private String _recordShortDescription;
    
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
        _recordShortDescription = description;
    }

    @Override
    public String getRecordShortDescription() {
        return _recordShortDescription;
    }

}
