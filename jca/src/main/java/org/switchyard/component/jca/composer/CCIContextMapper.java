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
package org.switchyard.component.jca.composer;

import javax.resource.cci.MappedRecord;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.component.jca.JCAConstants;
import org.switchyard.component.common.composer.BaseContextMapper;

/**
 * CCIContextMapper.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class CCIContextMapper extends BaseContextMapper<MappedRecord> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapFrom(MappedRecord source, Context context) throws Exception {
        String recordName = source.getRecordName();
        if (recordName != null) {
            context.setProperty(JCAConstants.CCI_RECORD_NAME_KEY, recordName, Scope.EXCHANGE)
                    .addLabels(JCAComposition.JCA_MESSAGE_PROPERTY);
        }
        String recordDescription = source.getRecordShortDescription();
        if (recordDescription != null) {
            context.setProperty(JCAConstants.CCI_RECORD_SHORT_DESC_KEY, recordDescription, Scope.EXCHANGE)
                    .addLabels(JCAComposition.JCA_MESSAGE_PROPERTY);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void mapTo(Context context, MappedRecord target) throws Exception {
        for (Property property : context.getProperties(Scope.EXCHANGE)) {
            String name = property.getName();
            Object value = property.getValue();
            if (value == null) {
                continue;
            }
            
            if (name.equals(JCAConstants.CCI_RECORD_NAME_KEY)) {
                target.setRecordName(value.toString());
            } else if (name.equals(JCAConstants.CCI_RECORD_SHORT_DESC_KEY)) {
                target.setRecordShortDescription(value.toString());
            } else if (matches(name)) {
                    target.put(name, value);
            }
        }
    }

}
