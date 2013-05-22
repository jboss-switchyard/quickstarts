/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.common.knowledge.config.model.v1;

import static org.switchyard.component.common.knowledge.config.model.OutputModel.OUTPUT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.config.model.OutputModel;
import org.switchyard.component.common.knowledge.config.model.OutputsModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 1 OutputsModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1OutputsModel extends BaseModel implements OutputsModel {

    private List<OutputModel> _outputs = new ArrayList<OutputModel>();

    /**
     * Creates a new V1OutputsModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1OutputsModel(String namespace) {
        super(XMLHelper.createQName(namespace, OUTPUTS));
        setModelChildrenOrder(OUTPUT);
    }

    /**
     * Creates a new V1OutputsModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1OutputsModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration output_config : config.getChildren(OUTPUT)) {
            OutputModel output = (OutputModel)readModel(output_config);
            if (output != null) {
                _outputs.add(output);
            }
        }
        setModelChildrenOrder(OUTPUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<OutputModel> getOutputs() {
        return Collections.unmodifiableList(_outputs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputsModel addOutput(OutputModel output) {
        addChildModel(output);
        _outputs.add(output);
        return this;
    }

}
