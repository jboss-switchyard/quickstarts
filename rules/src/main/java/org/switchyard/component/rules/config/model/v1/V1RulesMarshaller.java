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
package org.switchyard.component.rules.config.model.v1;

import static org.switchyard.component.rules.config.model.ChannelModel.CHANNEL;
import static org.switchyard.component.rules.config.model.FactsModel.FACTS;
import static org.switchyard.component.rules.config.model.GlobalsModel.GLOBALS;
import static org.switchyard.component.rules.config.model.RulesActionModel.ACTION;

import org.switchyard.component.common.rules.config.model.v1.V1CommonRulesMarshaller;
import org.switchyard.component.rules.config.model.RulesComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.ComponentImplementationModel;

/**
 * A CompositeMarshaller which can also create RulesComponentImplementationModels.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1RulesMarshaller extends V1CommonRulesMarshaller {

    /**
     * The complete local name ("implementation.rules").
     */
    private static final String IMPLEMENTATION_RULES = ComponentImplementationModel.IMPLEMENTATION + "." + RulesComponentImplementationModel.RULES;

    /**
     * Required constructor called via reflection.
     *
     * @param desc the Descriptor
     */
    public V1RulesMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * Reads in the Configuration, looking for "implementation.rules".
     * If not found, it falls back to the super class (V1CompositeMarshaller).
     *
     * @param config the Configuration
     * @return the Model
     */
    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        if (IMPLEMENTATION_RULES.equals(name)) {
            return new V1RulesComponentImplementationModel(config, getDescriptor());
        } else if (ACTION.equals(name)) {
            return new V1RulesActionModel(config, getDescriptor());
        } else if (CHANNEL.equals(name)) {
            return new V1ChannelModel(config, getDescriptor());
        } else if (GLOBALS.equals(name)) {
            return new V1GlobalsModel(config, getDescriptor());
        } else if (FACTS.equals(name)) {
            return new V1FactsModel(config, getDescriptor());
        }
        return super.read(config);
    }

}
