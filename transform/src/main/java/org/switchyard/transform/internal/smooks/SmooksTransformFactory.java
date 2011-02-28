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

package org.switchyard.transform.internal.smooks;

import org.milyn.Smooks;
import org.milyn.javabean.binding.model.ModelSet;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.config.model.Java2XmlTransformModel;
import org.switchyard.transform.config.model.SmooksTransformModel;
import org.switchyard.transform.config.model.Xml2JavaTransformModel;

import javax.xml.namespace.QName;

import static org.switchyard.transform.internal.smooks.XMLBindingTransformer.*;

/**
 * Smooks Transformer factory.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public final class SmooksTransformFactory {

    private SmooksTransformFactory() {}

    /**
     * Create a {@link Transformer} instance from the supplied {@link SmooksTransformModel}.
     * @param model The model.
     * @return The Transformer instance.
     */
    public static Transformer newTransformer(SmooksTransformModel model) {
        String config = model.getConfig();
        QName from = model.getFrom();
        QName to = model.getTo();

        if (config == null || config.trim().length() == 0) {
            throw new RuntimeException("Invalid Smooks configuration model.  null or empty 'config' specification.");
        }
        if (from == null) {
            throw new RuntimeException("Invalid Smooks configuration model.  null or 'from' specification.");
        }
        if (to == null) {
            throw new RuntimeException("Invalid Smooks configuration model.  null or 'to' specification.");
        }

        Smooks smooks;
        try {
            smooks = new Smooks(config);
            smooks.createExecutionContext();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Smooks instance for config '" + config + "'.", e);
        }

        return new SmooksTransformer(from, to, smooks, model);
    }

    /**
     * Create a {@link Transformer} instance from the supplied {@link Java2XmlTransformModel}.
     * @param model The model.
     * @return The Transformer instance.
     */
    public static Transformer newTransformer(Java2XmlTransformModel model) {
        return newXMLBindingTransformer(model, model.getConfig(), BindingDirection.JAVA2XML);
    }

    /**
     * Create a {@link Transformer} instance from the supplied {@link Xml2JavaTransformModel}.
     * @param model The model.
     * @return The Transformer instance.
     */
    public static Transformer newTransformer(Xml2JavaTransformModel model) {
        return newXMLBindingTransformer(model, model.getConfig(), BindingDirection.XML2JAVA);
    }

    private static Transformer newXMLBindingTransformer(TransformModel model, String config, BindingDirection direction) {
        QName from = model.getFrom();
        QName to = model.getTo();

        if (config == null || config.trim().length() == 0) {
            throw new RuntimeException("Invalid " + direction + " configuration model.  null or empty 'config' specification.");
        }
        if (from == null) {
            throw new RuntimeException("Invalid " + direction + " configuration model.  null or 'from' specification.");
        }
        if (to == null) {
            throw new RuntimeException("Invalid " + direction + " configuration model.  null or 'to' specification.");
        }

        Smooks smooks;
        try {
            smooks = new Smooks(config);
            smooks.createExecutionContext();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Smooks instance for java2xml config '" + config + "'.", e);
        }

        ModelSet beanModel = ModelSet.get(smooks.getApplicationContext());
        if (beanModel != null && !beanModel.getModels().isEmpty()) {
            return new XMLBindingTransformer(from, to, smooks, beanModel, direction);
        } else {
            throw new RuntimeException("Invalid " + direction + " binding configuration.  No <jb:bean> configurations found.");
        }
    }
}
