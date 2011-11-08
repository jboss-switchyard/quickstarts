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
package org.switchyard.component.rules.exchange;

import static org.switchyard.Scope.EXCHANGE;
import static org.switchyard.component.rules.common.RulesConstants.ACTION_TYPE_VAR;

import org.apache.log4j.Logger;
import org.switchyard.BaseHandler;
import org.switchyard.Context;
import org.switchyard.HandlerException;
import org.switchyard.Property;
import org.switchyard.component.rules.common.RulesActionType;
import org.switchyard.component.rules.common.RulesConstants;
import org.switchyard.component.rules.config.model.RulesActionModel;

/**
 * Contains base RulesExchangeHandler functionality and/or utility methods.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseRulesExchangeHandler extends BaseHandler implements RulesExchangeHandler {

    private static final Logger LOGGER = Logger.getLogger(BaseRulesExchangeHandler.class);

    /**
     * Gets the RulesActionType from the Exchange Context.
     * @param context the Exchange Context
     * @param model the associated RulesActionModel
     * @return the RulesActionType
     */
    protected RulesActionType getRulesActionType(Context context, RulesActionModel model) {
        if (model != null) {
            RulesActionType rat = model.getType();
            if (rat != null) {
                return rat;
            }
        }
        Property property = context.getProperty(ACTION_TYPE_VAR, EXCHANGE);
        if (property != null) {
            Object value = property.getValue();
            if (value instanceof RulesActionType) {
                return (RulesActionType)value;
            } else if (value instanceof String) {
                return RulesActionType.fromAction((String)value);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            String msg = new StringBuilder()
                .append(getNullParameterMessage(null, ACTION_TYPE_VAR))
                .append("; defaulting to: ")
                .append(RulesActionType.EXECUTE.action())
                .toString();
            LOGGER.debug(msg);
        }
        return RulesActionType.EXECUTE;
    }

    /**
     * Gets the continue flag from the Exchange Context.
     * @param context the Exchange Context
     * @return the continue flag
     */
    protected boolean isContinue(Context context) {
        return getBoolean(context, RulesConstants.CONTINUE_VAR, false);
    }

    /**
     * Gets the dispose flag from the Exchange Context.
     * @param context the Exchange Context
     * @return the dispose flag
     */
    protected boolean isDispose(Context context) {
        return getBoolean(context, RulesConstants.DISPOSE_VAR, false);
    }

    /**
     * Gets a property value as a boolean from the Exchange Context.
     * @param context the Exchange Context
     * @param name the name of the boolean property
     * @param fallback if the property is null, what to fall back to
     * @return the property value
     */
    protected boolean getBoolean(Context context, String name, boolean fallback) {
        Property property = context.getProperty(name, EXCHANGE);
        if (property != null) {
            Object value = property.getValue();
            if (value instanceof Boolean) {
                return ((Boolean)value).booleanValue();
            } else if (value instanceof String) {
                return Boolean.parseBoolean((String)value);
            }
        }
        return fallback;
    }

    /**
     * Creates and error message for a null parameter.
     * @param rulesActionType the optional rules action type
     * @param parameterName the name of the parameter
     * @return the error message
     */
    protected String getNullParameterMessage(RulesActionType rulesActionType, String parameterName) {
        StringBuilder sb = new StringBuilder("implementation.rules: ");
        if (rulesActionType != null) {
            sb.append("[");
            sb.append(rulesActionType.action());
            sb.append("] ");
        }
        sb.append(parameterName);
        sb.append(" == null");
        return sb.toString();
    }

    /**
     * Throws a new exception for a null parameter.
     * @param rulesActionType the optional rules action type
     * @param parameterName the name of the parameter
     * @throws HandlerException the exception thrown for the null parameter
     */
    protected void throwNullParameterException(RulesActionType rulesActionType, String parameterName) throws HandlerException {
        throw new HandlerException(getNullParameterMessage(rulesActionType, parameterName));
    }

}
