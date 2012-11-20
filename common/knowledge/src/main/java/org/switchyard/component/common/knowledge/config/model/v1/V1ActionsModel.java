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

import static org.switchyard.component.common.knowledge.config.model.ActionModel.ACTION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.component.common.knowledge.config.model.ActionModel;
import org.switchyard.component.common.knowledge.config.model.ActionsModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 1 ActionsModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1ActionsModel extends BaseModel implements ActionsModel {

    private List<ActionModel> _actions = new ArrayList<ActionModel>();

    /**
     * Creates a new OperationsModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1ActionsModel(String namespace) {
        super(new QName(namespace, ACTIONS));
        setModelChildrenOrder(ACTION);
    }

    /**
     * Creates a new OperationsModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1ActionsModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration action_config : config.getChildren(ACTION)) {
            ActionModel action = (ActionModel)readModel(action_config);
            if (action != null) {
                _actions.add(action);
            }
        }
        setModelChildrenOrder(ACTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<ActionModel> getActions() {
        return Collections.unmodifiableList(_actions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionsModel addAction(ActionModel action) {
        addChildModel(action);
        _actions.add(action);
        return this;
    }

}
