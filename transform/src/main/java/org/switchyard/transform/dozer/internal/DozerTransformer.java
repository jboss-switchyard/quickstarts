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

import java.util.List;

import javax.xml.namespace.QName;

import org.dozer.DozerBeanMapper;
import org.switchyard.common.xml.QNameUtil;
import org.switchyard.config.model.Scannable;
import org.switchyard.transform.BaseTransformer;

/**
 * Dozer {@link org.switchyard.transform.Transformer}.
 */
@Scannable(false)
public class DozerTransformer extends BaseTransformer {

    private DozerBeanMapper _dozerBeanMapper;

    /**
     * Constructor.
     * @param from From type.
     * @param to To type.
     * @param config config.
     */
    protected DozerTransformer(final QName from, final QName to, List<String> configs) {
        super(from, to);
        _dozerBeanMapper = new DozerBeanMapper();
        if (configs != null && !configs.isEmpty()) {
            _dozerBeanMapper.setMappingFiles(configs);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object transform(Object message) {
        return _dozerBeanMapper.map(message, QNameUtil.toJavaMessageType(getTo()));
    }
}
