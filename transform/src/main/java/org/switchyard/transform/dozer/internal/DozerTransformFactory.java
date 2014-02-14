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

package org.switchyard.transform.dozer.internal;

import java.util.ArrayList;
import java.util.List;

import org.switchyard.common.xml.QNameUtil;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.config.model.DozerFileEntryModel;
import org.switchyard.transform.config.model.DozerMappingFilesModel;
import org.switchyard.transform.config.model.DozerTransformModel;
import org.switchyard.transform.internal.TransformMessages;
import org.switchyard.transform.internal.TransformerFactory;

import javax.xml.namespace.QName;

/**
 * Dozer Transformer factory.
 */
public class DozerTransformFactory implements TransformerFactory<DozerTransformModel> {

    /**
     * Create a {@link Transformer} instance from the supplied {@link DozerTransformModel}.
     * @param model The model.
     * @return The Transformer instance.
     */
    public Transformer<?,?> newTransformer(DozerTransformModel model) {
        QName from = model.getFrom();
        QName to = model.getTo();
        if (from == null || !QNameUtil.isJavaMessageType(from)) {
            throw TransformMessages.MESSAGES.invalidFromTypeForDozerTransformer(from);
        }
        if (to == null || !QNameUtil.isJavaMessageType(to)) {
            throw TransformMessages.MESSAGES.invalidToTypeForDozerTransformer(to);
        }

        List<String> mappingFiles = new ArrayList<String>();
        DozerMappingFilesModel files = model.getDozerMappingFiles();
        if (files != null) {
            for (DozerFileEntryModel entry : files.getEntries()) {
                String filename = entry.getFile();
                if (filename != null && !filename.isEmpty()) {
                    mappingFiles.add(filename);
                }
            }
        }

        return new DozerTransformer(from, to, mappingFiles);
    }
}
