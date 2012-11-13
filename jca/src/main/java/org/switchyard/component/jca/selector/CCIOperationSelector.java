/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.jca.selector;

import java.io.StringReader;

import javax.resource.cci.MappedRecord;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.switchyard.component.common.selector.BaseOperationSelector;
import org.switchyard.component.jca.composer.MappedRecordBindingData;
import org.switchyard.config.model.selector.OperationSelectorModel;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * An example of OperationSelector implementation for CCI MappedRecord.
 */
public class CCIOperationSelector extends BaseOperationSelector<MappedRecordBindingData> {

    /** key name for lookup the operation from map. */
    public static final String KEY = "operationSelector";
    
    /**
     * Constructor.
     * @param model OperationSelectorModel
     */
    public CCIOperationSelector(OperationSelectorModel model) {
        super(model);
    }

    @Override
    protected Document extractDomDocument(MappedRecordBindingData binding) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(extractString(binding)));
        return builder.parse(is);
    }

    @Override
    protected String extractString(MappedRecordBindingData binding) throws Exception {
        MappedRecord content = binding.getRecord();
        return String.class.cast(content.get(KEY));
    }

}
