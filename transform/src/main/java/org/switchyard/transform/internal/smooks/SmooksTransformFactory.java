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
import org.switchyard.transform.Transformer;
import org.switchyard.transform.config.model.SmooksTransformModel;

import javax.xml.namespace.QName;

import static org.switchyard.transform.internal.smooks.XMLBindingTransformer.*;

/**
 * Smooks Transformer factory.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public final class SmooksTransformFactory {

    /**
     * Smooks transformation type.
     */
    private static enum SmooksTransformationType {
        SMOOKS,
        XML2JAVA,
        JAVA2XML
    }

    /**
     * Private constructor.
     */
    private SmooksTransformFactory() {
    }

    /**
     * Create a {@link Transformer} instance from the supplied {@link SmooksTransformModel}.
     * @param model The model.
     * @return The Transformer instance.
     */
    public static Transformer newTransformer(SmooksTransformModel model) {
        String transformType = model.getTransformType();
        String config = model.getConfig();
        QName from = model.getFrom();
        QName to = model.getTo();

        if (transformType == null || transformType.trim().length() == 0) {
            throw new RuntimeException("Invalid Smooks configuration model.  null or empty 'type' specification.");
        }
        if (config == null || config.trim().length() == 0) {
            throw new RuntimeException("Invalid Smooks configuration model.  null or empty 'config' specification.");
        }
        if (from == null) {
            throw new RuntimeException("Invalid Smooks configuration model.  null or 'from' specification.");
        }
        if (to == null) {
            throw new RuntimeException("Invalid Smooks configuration model.  null or 'to' specification.");
        }

        SmooksTransformationType transformationType = SmooksTransformationType.valueOf(transformType);

        Smooks smooks;
        try {
            smooks = new Smooks(config);
            smooks.createExecutionContext();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Smooks instance for config '" + config + "'.", e);
        }

        if (transformationType == SmooksTransformationType.JAVA2XML) {
            return newXMLBindingTransformer(from, to, smooks, BindingDirection.JAVA2XML);
        } else if (transformationType == SmooksTransformationType.XML2JAVA) {
            return newXMLBindingTransformer(from, to, smooks, BindingDirection.XML2JAVA);
        } else if (transformationType == SmooksTransformationType.SMOOKS) {
            return new SmooksTransformer(from, to, smooks, model);
        } else {
            throw new RuntimeException("Unhandled Smooks transformation type '" + transformationType + "'.");
        }
    }

    private static Transformer newXMLBindingTransformer(final QName from, final QName to, Smooks smooks, BindingDirection direction) {
        ModelSet beanModel = ModelSet.get(smooks.getApplicationContext());
        if (beanModel != null && !beanModel.getModels().isEmpty()) {
            return new XMLBindingTransformer(from, to, smooks, beanModel, direction);
        } else {
            throw new RuntimeException("Invalid " + direction + " binding configuration.  No <jb:bean> configurations found.");
        }
    }
}
