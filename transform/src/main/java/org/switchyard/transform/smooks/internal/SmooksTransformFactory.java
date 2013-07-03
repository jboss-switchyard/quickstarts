/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

package org.switchyard.transform.smooks.internal;

import org.milyn.Smooks;
import org.milyn.javabean.binding.model.ModelSet;
import org.switchyard.SwitchYardException;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.config.model.SmooksTransformModel;
import org.switchyard.transform.config.model.SmooksTransformType;
import org.switchyard.transform.internal.TransformerFactory;

import javax.xml.namespace.QName;

/**
 * Smooks Transformer factory.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SmooksTransformFactory implements TransformerFactory<SmooksTransformModel> {

    /**
     * Create a {@link Transformer} instance from the supplied {@link SmooksTransformModel}.
     * @param model The model.
     * @return The Transformer instance.
     */
    public Transformer newTransformer(SmooksTransformModel model) {
        String transformType = model.getTransformType();
        String config = model.getConfig();
        QName from = model.getFrom();
        QName to = model.getTo();

        if (transformType == null || transformType.trim().length() == 0) {
            throw new SwitchYardException("Invalid Smooks configuration model.  null or empty 'type' specification.");
        }
        if (config == null || config.trim().length() == 0) {
            throw new SwitchYardException("Invalid Smooks configuration model.  null or empty 'config' specification.");
        }
        if (from == null) {
            throw new SwitchYardException("Invalid Smooks configuration model.  null or 'from' specification.");
        }
        if (to == null) {
            throw new SwitchYardException("Invalid Smooks configuration model.  null or 'to' specification.");
        }

        SmooksTransformType transformationType = SmooksTransformType.valueOf(transformType);

        Smooks smooks;
        try {
            smooks = new Smooks(config);
            smooks.createExecutionContext();
        } catch (Exception e) {
            throw new SwitchYardException("Failed to create Smooks instance for config '" + config + "'.", e);
        }

        Transformer transformer;

        if (transformationType == SmooksTransformType.JAVA2XML) {
            transformer = newXMLBindingTransformer(from, to, smooks, XMLBindingTransformer.BindingDirection.JAVA2XML);
        } else if (transformationType == SmooksTransformType.XML2JAVA) {
            transformer = newXMLBindingTransformer(from, to, smooks, XMLBindingTransformer.BindingDirection.XML2JAVA);
        } else if (transformationType == SmooksTransformType.SMOOKS) {
            transformer = new SmooksTransformer(from, to, smooks, model);
        } else {
            throw new SwitchYardException("Unhandled Smooks transformation type '" + transformationType + "'.");
        }

        transformer.setFrom(model.getFrom());
        transformer.setTo(model.getTo());

        return transformer;
    }

    private static Transformer newXMLBindingTransformer(final QName from, final QName to, Smooks smooks, XMLBindingTransformer.BindingDirection direction) {
        ModelSet beanModel = ModelSet.get(smooks.getApplicationContext());
        if (beanModel != null && !beanModel.getModels().isEmpty()) {
            return new XMLBindingTransformer(from, to, smooks, beanModel, direction);
        } else {
            throw new SwitchYardException("Invalid " + direction + " binding configuration.  No <jb:bean> configurations found.");
        }
    }
}
