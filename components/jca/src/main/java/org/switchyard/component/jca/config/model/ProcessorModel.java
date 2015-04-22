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
package org.switchyard.component.jca.config.model;

/**
 * binding.jca/outboundInteraction/processor model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public interface ProcessorModel extends BasePropertyContainerModel {

    /**
     * get Processor class name.
     * 
     * @return Endpoint class name
     */
    String getProcessorClassName();
    
    /**
     * set Processor class name.
     * 
     * @param processor Processor class name to set
     * @return {@link ProcessorModel} to suport method chaining
     */
    ProcessorModel setProcessorClassName(final String processor);

}
