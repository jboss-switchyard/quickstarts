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

package org.switchyard.validate.config.model;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.switchyard.common.type.classpath.AbstractTypeFilter;
import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.config.model.Scannable;
import org.switchyard.config.model.Scanner; 
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.ScannerOutput;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardModel;
import org.switchyard.config.model.validate.ValidatesModel;
import org.switchyard.config.model.validate.v1.V1ValidatesModel;
import org.switchyard.validate.ValidatorTypes;
import org.switchyard.validate.ValidatorUtil;
import org.switchyard.validate.config.model.v1.V1JavaValidateModel;

/**
 * Scanner for {@link org.switchyard.validate.Validator} implementations.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class ValidateSwitchYardScanner implements Scanner<SwitchYardModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ScannerOutput<SwitchYardModel> scan(ScannerInput<SwitchYardModel> input) throws IOException {
        SwitchYardModel switchyardModel = new V1SwitchYardModel();
        ValidatesModel validatesModel = null;

        List<Class<?>> validatorClasses = scanForValidators(input.getURLs());
        for (Class<?> validator : validatorClasses) {
            List<ValidatorTypes> supportedValidators = ValidatorUtil.listValidations(validator);

            for (ValidatorTypes supportedValidate : supportedValidators) {
                JavaValidateModel validateModel = new V1JavaValidateModel();

                validateModel.setClazz(validator.getName());
                validateModel.setName(supportedValidate.getName());

                if (validatesModel == null) {
                    validatesModel = new V1ValidatesModel();
                    switchyardModel.setValidates(validatesModel);
                }
                validatesModel.addValidate(validateModel);
            }
        }

        return new ScannerOutput<SwitchYardModel>().setModel(switchyardModel);
    }

    private List<Class<?>> scanForValidators(List<URL> urls) throws IOException {
        AbstractTypeFilter filter = new ValidatorInstanceOfFilter();
        ClasspathScanner scanner = new ClasspathScanner(filter);
        for (URL url : urls) {
            scanner.scan(url);
        }

        return filter.getMatchedTypes();
    }

    private class ValidatorInstanceOfFilter extends AbstractTypeFilter {
        @Override
        public boolean matches(Class<?> clazz) {
            Scannable scannable = clazz.getAnnotation(Scannable.class);
            if (scannable != null && !scannable.value()) {
                // Marked as being non-scannable...
                return false;
            }
            return ValidatorUtil.isValidator(clazz);
        }
    }
}
