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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.camel.CamelContext;
import org.apache.camel.TypeConverter;
import org.apache.camel.impl.converter.DefaultTypeConverter;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.config.model.transform.v1.V1TransformsModel;
import org.switchyard.metadata.JavaTypes;
import org.switchyard.transform.TransformerRegistry;
import org.switchyard.transform.config.model.JavaTransformModel;
import org.switchyard.transform.config.model.TransformNamespace;
import org.switchyard.transform.config.model.v1.V1JavaTransformModel;

/**
 * Extracts the types that have been registered in Camel with a corresponding {@link TypeConverter}.
 * This is simply a way to find out what converters are available in Camel and make this information
 * available to SwitchYard.
 * 
 * @author Daniel Bevenius
 *
 */
public class CamelTypeConverterExtractor extends DefaultTypeConverter {
    
    private Map<QName, Set<QName>> _transformTypes = new HashMap<QName, Set<QName>>();
    private CamelContext _camelContext;

    /**
     * Sole constuctor. 
     * 
     * @param camelContext the CamelContext to be used.
     */
    public CamelTypeConverterExtractor(final CamelContext camelContext) {
        super(camelContext.getPackageScanClassResolver(), camelContext.getInjector(), camelContext.getDefaultFactoryFinder());
        _camelContext = camelContext;
    }
    
    /**
     * Initializes Camel's type converters by loading default type converters and annotated converters.
     * 
     * @throws Exception if an error occurs while loading the type converters.
     */
    public void init() throws Exception {
        _camelContext.addService(this);
    }
    
    /**
     * Will return the SwitchYard representations (QName) for the 'from and 'to' types
     * registered within Camel's TypeConverter registry.
     * 
     * @return Map where the key is the fromType and the value a set of toTypes.
     */
    public Map<QName, Set<QName>> getTransformTypes() {
        for (Entry<TypeMapping, TypeConverter> entry : typeMappings.entrySet())
        {
            final TypeMapping mapping = entry.getKey();
            final QName fromType = JavaTypes.toMessageType(mapping.getFromType());
            final QName toType = JavaTypes.toMessageType(mapping.getToType());
            getToTypesFor(fromType).add(toType);
        }
        
        return Collections.unmodifiableMap(_transformTypes);
    }
    
    private Set<QName> getToTypesFor(final QName fromType) {
        if (!_transformTypes.containsKey(fromType)) {
            _transformTypes.put(fromType, new HashSet<QName>());
        }
        return _transformTypes.get(fromType);
    }
    
    /**
     * Returns a TransformsModel which will not include transformers are already registered in
     * the {@link TransformerRegistry} in SwitchYard.
     * 
     * @param transformerRegistry SwitchYard's {@link TransformerRegistry}.
     *
     * @return {@link TransformsModel} the populated TransformsModel
     */
    public TransformsModel getTransformsModel(final TransformerRegistry transformerRegistry) {
        final TransformsModel transforms = new V1TransformsModel(TransformNamespace.DEFAULT.uri());
        for (Entry<QName, Set<QName>> entry : getTransformTypes().entrySet()) {
            final QName from = entry.getKey();
            final Set<QName> toTypes = entry.getValue();
            for (QName to : toTypes) {
                final V1JavaTransformModel transform = new V1JavaTransformModel(TransformNamespace.DEFAULT.uri());
                transform.setFrom(from);
                transform.setTo(to);
                transform.setClazz(CamelTransformer.class.getName());
                if (!isTransformRegistred(transform, transformerRegistry)) {
                    transforms.addTransform(transform);
                }
            }
        }
        return transforms;
    }
    
    private boolean isTransformRegistred(final JavaTransformModel transform, final TransformerRegistry transformerRegistry) {
        if (transformerRegistry == null) {
            return false;
        }
        
        return transformerRegistry.hasTransformer(transform.getFrom(), transform.getTo());
    }

    /**
     * Returns a TransformsModel that does not check if transformers are already registered within
     * the {@link TransformerRegistry} in SwitchYard.
     * 
     * @return {@link TransformsModel} populated with the passed in types.
     */
    public TransformsModel getTransformsModel() {
        return getTransformsModel(null);
    }
}
