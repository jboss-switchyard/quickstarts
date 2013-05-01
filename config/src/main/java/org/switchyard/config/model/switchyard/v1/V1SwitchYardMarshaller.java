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
package org.switchyard.config.model.switchyard.v1;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseMarshaller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.domain.DomainModel;
import org.switchyard.config.model.domain.HandlerModel;
import org.switchyard.config.model.domain.HandlersModel;
import org.switchyard.config.model.domain.SecuritiesModel;
import org.switchyard.config.model.domain.SecurityModel;
import org.switchyard.config.model.domain.v1.V1DomainModel;
import org.switchyard.config.model.domain.v1.V1HandlerModel;
import org.switchyard.config.model.domain.v1.V1HandlersModel;
import org.switchyard.config.model.domain.v1.V1SecuritiesModel;
import org.switchyard.config.model.domain.v1.V1SecurityModel;
import org.switchyard.config.model.property.PropertiesModel;
import org.switchyard.config.model.property.PropertyModel;
import org.switchyard.config.model.property.v1.V1PropertiesModel;
import org.switchyard.config.model.property.v1.V1PropertyModel;
import org.switchyard.config.model.resource.ResourceModel;
import org.switchyard.config.model.resource.v1.V1ResourceModel;
import org.switchyard.config.model.selector.JavaOperationSelectorModel;
import org.switchyard.config.model.selector.OperationSelectorModel;
import org.switchyard.config.model.selector.RegexOperationSelectorModel;
import org.switchyard.config.model.selector.XPathOperationSelectorModel;
import org.switchyard.config.model.selector.v1.V1JavaOperationSelectorModel;
import org.switchyard.config.model.selector.v1.V1RegexOperationSelectorModel;
import org.switchyard.config.model.selector.v1.V1StaticOperationSelectorModel;
import org.switchyard.config.model.selector.v1.V1XPathOperationSelectorModel;
import org.switchyard.config.model.switchyard.ArtifactModel;
import org.switchyard.config.model.switchyard.ArtifactsModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.config.model.transform.v1.V1TransformsModel;
import org.switchyard.config.model.validate.ValidatesModel;
import org.switchyard.config.model.validate.v1.V1ValidatesModel;

/**
 * Marshalls switchyard Models.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1SwitchYardMarshaller extends BaseMarshaller {

    private static final String ESB_INTERFACE = "interface.esb";
    /**
     * Constructs a new V1SwitchYardMarshaller with the specified Descriptor.
     * @param desc the Descriptor
     */
    public V1SwitchYardMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        Descriptor desc = getDescriptor();
        if (name.equals(SwitchYardModel.SWITCHYARD)) {
            return new V1SwitchYardModel(config, desc);
        } else if (name.equals(TransformsModel.TRANSFORMS)) {
            return new V1TransformsModel(config, desc);
        } else if (name.equals(ValidatesModel.VALIDATES)) {
            return new V1ValidatesModel(config, desc);
        } else if (name.equals(ArtifactsModel.ARTIFACTS)) {
            return new V1ArtifactsModel(config, desc);
        } else if (name.equals(ArtifactModel.ARTIFACT)) {
            return new V1ArtifactModel(config, desc);
        } else if (name.equals(PropertiesModel.PROPERTIES)) {
            return new V1PropertiesModel(config, desc);
        } else if (name.equals(PropertyModel.PROPERTY)) {
            return new V1PropertyModel(config, desc);
        } else if (name.equals(HandlersModel.HANDLERS)) {
            return new V1HandlersModel(config, desc);
        } else if (name.equals(HandlerModel.HANDLER)) {
            return new V1HandlerModel(config, desc);
        } else if (name.equals(SecuritiesModel.SECURITIES)) {
            return new V1SecuritiesModel(config, desc);
        } else if (name.equals(SecurityModel.SECURITY)) {
            return new V1SecurityModel(config, desc);
        } else if (name.equals(DomainModel.DOMAIN)) {
            return new V1DomainModel(config, desc);
        } else if (name.equals(ResourceModel.RESOURCE)) {
            return new V1ResourceModel(config, desc);
        } else if (name.equals(ESB_INTERFACE)) {
            return new V1EsbInterfaceModel(config, desc);
        } else if (name.startsWith(OperationSelectorModel.OPERATION_SELECTOR)) {
            if (name.equals(OperationSelectorModel.OPERATION_SELECTOR)) {
                return new V1StaticOperationSelectorModel(config, desc);
            } else if (name.endsWith(XPathOperationSelectorModel.XPATH)) {
                return new V1XPathOperationSelectorModel(config, desc);
            } else if (name.endsWith(RegexOperationSelectorModel.REGEX)) {
                return new V1RegexOperationSelectorModel(config, desc);
            } else if (name.endsWith(JavaOperationSelectorModel.JAVA)) {
                return new V1JavaOperationSelectorModel(config, desc);
            }
        }
        return null;
    }

}
