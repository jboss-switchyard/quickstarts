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

package org.switchyard.transform.smooks.internal;

import org.apache.log4j.Logger;
import org.milyn.Smooks;
import org.milyn.container.plugin.SourceFactory;
import org.milyn.javabean.binding.model.Bean;
import org.milyn.javabean.binding.model.ModelSet;
import org.milyn.javabean.binding.xml.XMLBinding;
import org.switchyard.SwitchYardException;
import org.switchyard.config.model.Scannable;
import org.switchyard.transform.BaseTransformer;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import java.io.IOException;

/**
 * Smooks XMLBinding {@link org.switchyard.transform.Transformer}.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@Scannable(false)
public class XMLBindingTransformer extends BaseTransformer {

    private static Logger _log = Logger.getLogger(XMLBindingTransformer.class);

    private XMLBinding _xmlBinding;
    private Bean _bean;
    private BindingDirection _direction;

    /**
     * Binding direction.
     */
    public static enum BindingDirection {
        /** The xml to java binding direction. */
        XML2JAVA,
        /** The java to xml binding directionx. */
        JAVA2XML
    }

    /**
     * Constructor.
     * @param from From type.
     * @param to To type.
     * @param smooks Smooks instance.
     * @param beanModel Bean model.
     * @param direction Binding direction.
     */
    protected XMLBindingTransformer(final QName from, final QName to, Smooks smooks, ModelSet beanModel, BindingDirection direction) {
        super(from, to);
        _xmlBinding = new XMLBinding(smooks);
        _xmlBinding.setOmitXMLDeclaration(true); // XML decl causes problems for StAX code used by SOAP handler
        _xmlBinding.intiailize();
        _bean = beanModel.getModels().values().iterator().next();
        _direction = direction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object transform(Object from) {
        if (from == null) {
            _log.debug("Null from payload passed to XMLBindingTransformer.  Returning null.");
            return null;
        }

        if (_direction == BindingDirection.XML2JAVA) {
            Source source = SourceFactory.getInstance().createSource(from);
            try {
                return _xmlBinding.fromXML(source, _bean.getBeanClass());
            } catch (IOException e) {
                throw new SwitchYardException("Exception while transforming from XML to '" + _bean.getBeanClass().getName() + "'.", e);
            }
        } else {
            if (!_bean.getBeanClass().isInstance(from)) {
                throw new SwitchYardException("Cannot transform to XML.  Input type is '" + from.getClass().getName() + "' but should be '" + _bean.getBeanClass().getName() + "'.");
            }

            return _xmlBinding.toXML(from);
        }
    }
}
