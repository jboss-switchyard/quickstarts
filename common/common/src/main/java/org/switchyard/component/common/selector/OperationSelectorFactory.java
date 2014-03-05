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

package org.switchyard.component.common.selector;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.switchyard.common.type.Classes;
import org.switchyard.common.util.ProviderRegistry;
import org.switchyard.component.common.CommonCommonLogger;
import org.switchyard.config.model.selector.OperationSelectorModel;
import org.switchyard.config.model.selector.JavaOperationSelectorModel;
import org.switchyard.selector.OperationSelector;

/**
 * A base OperationSelectorFactory which creates OperationSelector instance.
 * @param <T> message type
 */
public abstract class OperationSelectorFactory<T> {

    /**
     * Component developer should implement this message to specify the type of source object.
     * @return the type of source object
     */
    public abstract Class<T> getTargetClass();

    /**
     * Component developer should implement this message to provide their default/fallback implementation
     * if the OperationSelectorModel passed into {@link OperationSelectorFactory#newOperationSelector(OperationSelectorModel)}
     * doesn't specify (or specifies a bad) operation selector class to use.
     * @return the default/fallback operation selector implementation
     */
    public abstract Class<? extends OperationSelector<T>> getDefaultOperationSelectorClass();

    /**
     * Create a new OperationSelector from configuration model.
     * @param model OperationSelectorModel
     * @return new OperationSelector instance
     */
    @SuppressWarnings("unchecked")
    public final OperationSelector<T> newOperationSelector(OperationSelectorModel model) {
        if (model == null) {
            return null;
        }
        
        OperationSelectorFactory<T> operationSelectorFactory = OperationSelectorFactory.getOperationSelectorFactory(getTargetClass());
        if (model instanceof JavaOperationSelectorModel) {
            JavaOperationSelectorModel javaModel = JavaOperationSelectorModel.class.cast(model);
            return operationSelectorFactory.newOperationSelector((Class<OperationSelector<T>>)Classes.forName(javaModel.getClazz()), javaModel);
        } else {
            return operationSelectorFactory.newOperationSelector(operationSelectorFactory.getDefaultOperationSelectorClass(), model);
        }
    }
    
    /**
     * Constructs a new OperationSelector.
     * @param custom OperationSelector class
     * @param model OperationSelector model
     * @return the new OperationSelector instance
     */
    public final OperationSelector<T> newOperationSelector(Class<? extends OperationSelector<T>> custom, OperationSelectorModel model) {
        OperationSelector<T> operationSelector = null;
        if (custom != null) {
            try {
                Constructor<? extends OperationSelector<T>> constructor = custom.getConstructor(OperationSelectorModel.class);
                operationSelector = constructor.newInstance(model);
            } catch (Exception e) {
                CommonCommonLogger.ROOT_LOGGER.couldNotInstantiateOperationSelector(custom.getClass().getName(), e.getMessage());
            }
        }
        return operationSelector;
    }

    /**
     * Constructs a new OperationSelectorFactory that is known to be able to construct OperationSelectors
     * of the specified type.
     * @param <F> the type of source object
     * @param targetClass the target OperationSelector class
     * @return the new OperationSelectorFactory instance
     */
    @SuppressWarnings("unchecked")
    public static final <F> OperationSelectorFactory<F> getOperationSelectorFactory(Class<F> targetClass) {
        return (OperationSelectorFactory<F>)getOperationSelectorFactories().get(targetClass);
    }

    /**
     * Gets a map of all known OperationSelectorFactories, keyed by their supported source object type.
     * @return the OperationSelectorFactories map
     */
    @SuppressWarnings("rawtypes")
    public static final Map<Class, OperationSelectorFactory> getOperationSelectorFactories() {
        Map<Class, OperationSelectorFactory> factories = new HashMap<Class, OperationSelectorFactory>();
        List<OperationSelectorFactory> services = ProviderRegistry.getProviders(OperationSelectorFactory.class);
        for (OperationSelectorFactory factory : services) {
            factories.put(factory.getTargetClass(), factory);
        }
        return factories;
    }

}
