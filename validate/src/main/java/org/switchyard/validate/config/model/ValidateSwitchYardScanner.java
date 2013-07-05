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

package org.switchyard.validate.config.model;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.switchyard.common.cdi.CDIUtil;
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
import org.switchyard.validate.config.model.v1.V1JavaValidateModel;
import org.switchyard.validate.internal.ValidatorTypes;
import org.switchyard.validate.internal.ValidatorUtil;

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

                String bean = CDIUtil.getNamedAnnotationValue(validator);
                if (bean != null) {
                    validateModel.setBean(bean);
                } else {
                    validateModel.setClazz(validator.getName());
                }
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
