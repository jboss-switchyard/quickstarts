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

package org.switchyard.validate.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.config.model.validate.ValidatesModel;
import org.switchyard.validate.Validator;
import org.switchyard.validate.ValidatorRegistry;
import org.switchyard.validate.ValidateMessages;

/**
 * {@link ValidatorRegistry} loader class.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class ValidatorRegistryLoader {

    /**
     * Logger.
     */
    private static Logger _log = Logger.getLogger(ValidatorRegistryLoader.class);
    /**
     * Classpath location for out-of-the-box validation configurations.
     */
    public static final String VALIDATES_XML = "META-INF/switchyard/validates.xml";

    /**
     * Validators
     */
    private List<Validator> _validators = new LinkedList<Validator>();
    /**
     * The registry instance into which the validates were loaded.
     */
    private ValidatorRegistry _validatorRegistry;

    /**
     * Public constructor.
     * @param validatorRegistry The registry instance.
     */
    public ValidatorRegistryLoader(ValidatorRegistry validatorRegistry) {
        if (validatorRegistry == null) {
            throw ValidateMessages.MESSAGES.nullValidatorRegistryArgument();
        }
        this._validatorRegistry = validatorRegistry;
    }

    /**
     * Register a set of validators in the validate registry associated with this deployment.
     * @param validates The validates model.
     * @throws DuplicateValidatorException an existing validator has already been registered for the from and to types
     */
    public void registerValidators(ValidatesModel validates) throws DuplicateValidatorException {
        if (validates == null) {
            return;
        }

        try {
            for (ValidateModel validateModel : validates.getValidates()) {
                Collection<Validator<?>> validators = ValidatorUtil.newValidators(validateModel);

                for (Validator<?> validator : validators) {
                    if (_validatorRegistry.hasValidator(validator.getName())) {
                        Validator<?> registeredValidator = _validatorRegistry.getValidator(validator.getName());
                        throw ValidateMessages.MESSAGES.failedToRegisterValidator(toDescription(validator), toDescription(registeredValidator));
                    }

                    _log.debug("Adding validator =>"
                            + ", Name:" + validator.getName());
                    _validatorRegistry.addValidator(validator);
                    _validators.add(validator);
                }
            }
        } catch (DuplicateValidatorException e) {
            throw e;
        } catch (RuntimeException e) {
            // If there was an exception for any reason... remove all Validator instance that have
            // already been registered with the domain...
            unregisterValidators();
            throw e;
        }
    }

    /**
     * Unregister all validators.
     */
    public void unregisterValidators() {
        for (Validator validator : _validators) {
            _validatorRegistry.removeValidator(validator);
        }
    }

    /**
     * Load the out of the box validators.
     * <p/>
     * Scans the classpath for {@link #VALIDATES_XML} runtime configuration resources.
     */
    public void loadOOTBValidates() {
        try {
            List<URL> resources = Classes.getResources(VALIDATES_XML, getClass());

            for (URL resource : resources) {
                InputStream configStream = resource.openStream();

                try {
                    ValidatesModel validatesModel = new ModelPuller<ValidatesModel>().pull(configStream);
                    registerValidators(validatesModel);
                } catch (final DuplicateValidatorException e) {
                    _log.debug(e.getMessage(), e);
                } finally {
                    configStream.close();
                }
            }
        } catch (IOException e) {
            throw ValidateMessages.MESSAGES.errorReadingValidator(VALIDATES_XML, e);
        }
    }

    private String toDescription(Validator<?> validator) {
        return validator.getClass().getName() + "(" + validator.getName() + ")";
    }
}
