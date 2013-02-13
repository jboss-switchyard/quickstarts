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

package org.switchyard.handlers;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Labels;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.validate.Validator;
import org.switchyard.validate.ValidatorRegistry;

/**
 * ExchangeHandler implementation used to introduce validations to the
 * exchange handler chain.  The core runtime automatically creates a
 * ValidateHandler and attaches it to the consumer handler chain for every
 * exchange.  ValidateHandler can also be used in the service provider's
 * chain by using the <code>ValidatorHandler(Validator<?>)</code>
 * constructor.
 *
 */
public class ValidateHandler extends BaseHandler {

    private static final String KEY_VALIDATED_TYPE = "org.switchyard.validatedType";
    
    private static Logger _logger = Logger.getLogger(ValidateHandler.class);

    private ValidatorRegistry _registry;

    /**
     * Create a new ValidateHandler.  The specified ValidatorRegistry will
     * be used to locate validators for each handled exchange.
     * @param registry validation registry to use for lookups of validator
     */
    public ValidateHandler(ValidatorRegistry registry) {
        _registry = registry;
    }

    /**
     * Validate the current message on the exchange.
     * @param exchange exchange
     * @throws HandlerException handler exception
     */
    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        Validator<?> validator = get(exchange);
        if (validator != null) {
            try {
                if (!applyValidator(exchange, validator)) {
                    throw new HandlerException("Validator '" + validator.getClass().getName()
                            + "' returned false.  Check input payload matches requirements of the Validator implementation.");
                }
            } catch (SwitchYardException syEx) {
                // Validators which throw SwitchYardException should be reported as HandlerException
                throw new HandlerException(syEx.getMessage());
            }
        }
    }

    @Override
    public void handleFault(Exchange exchange) {
        Validator<?> validator = get(exchange);
        if (validator != null) {
            if (!applyValidator(exchange, validator)) {
                _logger.warn("Validator '" + validator.getClass().getName()
                        + "' returned false.  Check input payload matches requirements of the Validator implementation.");
            }
        }
    }
    
    private Validator<?> get(Exchange exchange) {
        Property contentType = exchange.getContext().getProperty(
                Exchange.CONTENT_TYPE, Scope.activeScope(exchange));
        Property validatedType = exchange.getContext().getProperty(
                KEY_VALIDATED_TYPE, Scope.activeScope(exchange));
        
        if (contentType != null) {
            if (validatedType != null && contentType.getValue().equals(validatedType.getValue())) {
                // Avoid to apply same validator twice. That may occur if any transformer is not applied and
                // then the ValidateHandler is triggered twice with same contentType in the same exchange.
                return null;
            }
            return _registry.getValidator((QName)contentType.getValue());
        } else {
            return null;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private boolean applyValidator(Exchange exchange, Validator validator) {
        Message message = exchange.getMessage();
        boolean validated = false;
        if (Message.class.isAssignableFrom(validator.getType())) {
            validated = validator.validate(message);
        } else {
            validated = validator.validate(message.getContent(validator.getType()));
        }

        if (validated) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("Validated Message (" + System.identityHashCode(message)
                        + ") with name '" + validator.getName() + "' using validator type '" + validator.getType() + "'.");
            }
       }
        exchange.getContext().setProperty(
                KEY_VALIDATED_TYPE, validator.getType(), Scope.activeScope(exchange)).addLabels(Labels.TRANSIENT);
        return validated;
    }
}

