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

package org.switchyard.handlers;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.SwitchYardException;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.runtime.RuntimeLogger;
import org.switchyard.runtime.RuntimeMessages;
import org.switchyard.validate.ValidationResult;
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
                ValidationResult result = applyValidator(exchange, validator);
                if (!result.isValid()) {
                    throw RuntimeMessages.MESSAGES.validatorFailed(validator.getClass().getName(), result.getDetail());
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
            ValidationResult result = applyValidator(exchange, validator);
            if (!result.isValid()) {
                RuntimeLogger.ROOT_LOGGER.validatorFailed(validator.getClass().getName(), result.getDetail());
            }
        }
    }

    private Validator<?> get(Exchange exchange) {
        Property contentType = exchange.getContext().getProperty(Exchange.CONTENT_TYPE);
        Property validatedType = exchange.getContext().getProperty(KEY_VALIDATED_TYPE);

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
    private ValidationResult applyValidator(Exchange exchange, Validator validator) {
        Message message = exchange.getMessage();
        ValidationResult validationResult = null;
        if (Message.class.isAssignableFrom(validator.getType())) {
            validationResult = validator.validate(message);
        } else {
            validationResult = validator.validate(message.getContent(validator.getType()));
        }

        if (validationResult.isValid()) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("Validated Message (" + System.identityHashCode(message)
                        + ") with name '" + validator.getName() + "' using validator type '" + validator.getType() + "'.");
            }
       }

       exchange.getContext().setProperty(KEY_VALIDATED_TYPE, validator.getType())
           .addLabels(BehaviorLabel.TRANSIENT.label());
       return validationResult;
    }
}

