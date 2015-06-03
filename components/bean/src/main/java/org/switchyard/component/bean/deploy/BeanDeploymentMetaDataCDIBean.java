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

package org.switchyard.component.bean.deploy;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * BeanDeploymentMetaData CDI Bean.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanDeploymentMetaDataCDIBean implements Bean {

    /**
     * Bean deployment metadata.
     */
    private BeanDeploymentMetaData _beanMetaData;
    /**
     * CDI bean qualifiers.  See CDI Specification.
     */
    private Set<Annotation> _qualifiers;

    /**
     * Public constructor.
     * @param beanDeploymentMetaData Bean metadata.
     */
    public BeanDeploymentMetaDataCDIBean(BeanDeploymentMetaData beanDeploymentMetaData) {
        _beanMetaData = beanDeploymentMetaData;
        this._qualifiers = new HashSet<Annotation>();
        this._qualifiers.add(new AnnotationLiteral<Default>() {});
        this._qualifiers.add(new AnnotationLiteral<Any>() {});
    }

    /**
     * Get the BeanDeploymentMetaData.
     * @return The BeanDeploymentMetaData.
     */
    public BeanDeploymentMetaData getBeanMetaData() {
        return _beanMetaData;
    }

    /**
     * Obtains the {@linkplain javax.enterprise.inject bean types} of the bean.
     *
     * @return the {@linkplain javax.enterprise.inject bean types}
     */
    public Set<Type> getTypes() {
        Set<Type> types = new HashSet<Type>();
        types.add(BeanDeploymentMetaData.class);
        types.add(Object.class);
        return types;
    }

    /**
     * Obtains the {@linkplain javax.inject.Qualifier qualifiers} of the bean.
     *
     * @return the {@linkplain javax.inject.Qualifier qualifiers}
     */
    public Set<Annotation> getQualifiers() {
        return _qualifiers;
    }

    /**
     * Obtains the {@linkplain javax.enterprise.inject EL name} of a bean, if it has one.
     *
     * @return the {@linkplain javax.enterprise.inject EL name}
     */
    public String getName() {
        return null;
    }

    /**
     * Obtains the {@linkplain javax.enterprise.inject.Stereotype stereotypes}
     * of the bean.
     *
     * @return the set of {@linkplain javax.enterprise.inject.Stereotype stereotypes}
     */
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    /**
     * The bean {@linkplain Class class} of the managed bean or session bean or
     * of the bean that declares the producer method or field.
     *
     * @return the bean {@linkplain Class class}
     */
    public Class<?> getBeanClass() {
        return BeanDeploymentMetaData.class;
    }

    /**
     * Determines if the bean is an
     * {@linkplain javax.enterprise.inject.Alternative alternative}.
     *
     * @return <tt>true</tt> if the bean is an
     *         {@linkplain javax.enterprise.inject.Alternative alternative},
     *         and <tt>false</tt> otherwise.
     */
    public boolean isAlternative() {
        return false;
    }

    /**
     * Determines if
     * {@link javax.enterprise.context.spi.Contextual#create(javax.enterprise.context.spi.CreationalContext)}
     * sometimes return a null value.
     *
     * @return <tt>true</tt> if the {@code create()} method may return a null
     *         value, and <tt>false</tt> otherwise
     */
    public boolean isNullable() {
        return false;
    }

    /**
     * Obtains the {@link javax.enterprise.inject.spi.InjectionPoint} objects
     * representing injection points of the bean, that will be validated by the
     * container at initialization time.
     *
     * @return the set of {@linkplain javax.enterprise.inject.spi.InjectionPoint injection points} of the bean
     */
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    /**
     * Obtains the {@linkplain javax.enterprise.context scope} of the bean.
     *
     * @return the {@linkplain javax.enterprise.context scope}
     */
    public Class<? extends Annotation> getScope() {
        return ApplicationScoped.class;
    }

    /**
     * Create a new instance of the contextual type. Instances should
     * use the given {@link javax.enterprise.context.spi.CreationalContext}
     * when obtaining contextual references to inject, in order to ensure
     * that any dependent objects are associated with the contextual instance
     * that is being created. An implementation may call
     * {@link javax.enterprise.context.spi.CreationalContext#push(Object)}
     * between instantiation and injection to help the container minimize the
     * use of client proxy objects.
     *
     * @param creationalContext the context in which this instance is being created
     * @return the contextual instance
     */
    public Object create(CreationalContext creationalContext) {
        return _beanMetaData;
    }

    /**
     * Destroy an instance of the contextual type. Implementations should
     * call {@link javax.enterprise.context.spi.CreationalContext#release()}
     * to allow the container to destroy dependent objects of the contextual
     * instance.
     *
     * @param instance          the contextual instance to destroy
     * @param creationalContext the context in which this instance was created
     */
    public void destroy(Object instance, CreationalContext creationalContext) {
    }
}
