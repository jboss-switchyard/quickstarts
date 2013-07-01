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
package org.switchyard.component.camel.transformer;

import javax.xml.namespace.QName;

import org.switchyard.common.xml.QNameUtil;
import org.switchyard.transform.BaseTransformer;

/**
 * A SwitchYard transformer that delegates to Apache Camel's converter framework.
 * </p> 
 * 
 * This gives SwitchYard components that use the SwitchYard Camel component to have 
 * access to all converters that are provided by Camel and at the same time enables 
 * the from and to types to be explicitely declared on the SwitchYard contracts.  
 * 
 * @author Daniel Bevenius
 *
 */
public class CamelTransformer extends BaseTransformer {

    private CamelConverter _camelConverter = CamelConverter.instance();

    /**
     * No-args constructor.
     */
    public CamelTransformer() {
    }

    /**
     * 
     * @param from The QName of the type that this transformer is capable of transforming from
     * @param to The QName of the type that this transformer is capable of transforming to
     */
    public CamelTransformer(final QName from, final QName to) {
        super(from, to);
    }

    @Override
    public Object transform(final Object from) {
        if (from == null) {
            return null;
        }

        final Class<?> toType = QNameUtil.toJavaMessageType(getTo());
        return _camelConverter.convert(toType, from);
    }

}
