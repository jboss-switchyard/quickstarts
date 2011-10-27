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

package org.switchyard.validate;

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
import org.switchyard.exception.DuplicateValidatorException;
import org.switchyard.exception.SwitchYardException;

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
            throw new IllegalArgumentException("null 'validatorRegistry' argument.");
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
                        throw new DuplicateValidatorException("Failed to register Validator '" + toDescription(validator)
                                + "'.  A Transformer for these types is already registered: '"
                                + toDescription(registeredValidator) + "'.");
                    }

                    _log.debug("Adding transformer =>"
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
            throw new SwitchYardException("Error reading out-of-the-box Validator configurations from classpath (" + VALIDATES_XML + ").", e);
        }
    }

    private String toDescription(Validator<?> validator) {
        return validator.getClass().getName() + "(" + validator.getName() + ")";
    }
}
