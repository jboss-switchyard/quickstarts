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
package org.switchyard.component.jca.composer;

import javax.resource.cci.MappedRecord;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.component.common.composer.BaseRegexContextMapper;
import org.switchyard.component.common.label.ComponentLabel;
import org.switchyard.component.common.label.EndpointLabel;
import org.switchyard.component.jca.JCAConstants;

/**
 * ContextMapper for CCI MappedRecord.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class MappedRecordContextMapper extends BaseRegexContextMapper<MappedRecordBindingData> {

    private static final String[] MAPPED_RECORD_LABELS = new String[]{ComponentLabel.JCA.label(), EndpointLabel.JCA.label()};

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapFrom(MappedRecordBindingData source, Context context) throws Exception {
        MappedRecord record = source.getRecord();
        String recordName = record.getRecordName();
        if (recordName != null) {
            context.setProperty(JCAConstants.CCI_RECORD_NAME_KEY, recordName).addLabels(MAPPED_RECORD_LABELS);
        }
        String recordDescription = record.getRecordShortDescription();
        if (recordDescription != null) {
            context.setProperty(JCAConstants.CCI_RECORD_SHORT_DESC_KEY, recordDescription).addLabels(MAPPED_RECORD_LABELS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void mapTo(Context context, MappedRecordBindingData target) throws Exception {
        MappedRecord record = target.getRecord();
        for (Property property : context.getProperties()) {
            String name = property.getName();
            Object value = property.getValue();
            if (value == null) {
                continue;
            }
            if (name.equals(JCAConstants.CCI_RECORD_NAME_KEY)) {
                record.setRecordName(value.toString());
            } else if (name.equals(JCAConstants.CCI_RECORD_SHORT_DESC_KEY)) {
                record.setRecordShortDescription(value.toString());
            } else if (matches(name)) {
                record.put(name, value);
            }
        }
    }

}
