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

package org.switchyard.component.common.selector;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.log4j.Logger;
import org.switchyard.component.common.selector.config.model.JavaOperationSelectorModel;
import org.switchyard.component.common.selector.config.model.OperationSelectorModel;

/**
 * A base OperationSelectorFactory which creates OperationSelector instance.
 * @param <T> message type
 */
public abstract class OperationSelectorFactory<T> {

    private static final Logger LOGGER = Logger.getLogger(OperationSelectorFactory.class);

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
            return operationSelectorFactory.newOperationSelector((Class<OperationSelector<T>>) javaModel.getClazz(), javaModel);
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
                LOGGER.error("Could not instantiate OperationSelector: " + custom.getClass().getName() + " - " + e.getMessage());
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
        ServiceLoader<OperationSelectorFactory> services = ServiceLoader.load(OperationSelectorFactory.class);
        for (OperationSelectorFactory factory : services) {
            factories.put(factory.getTargetClass(), factory);
        }
        return factories;
    }

}
