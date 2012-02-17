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
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;
import org.switchyard.config.model.composer.v1.V1ContextMapperModel;
import org.switchyard.config.model.composer.v1.V1MessageComposerModel;
import org.switchyard.config.model.domain.DomainModel;
import org.switchyard.config.model.domain.HandlerModel;
import org.switchyard.config.model.domain.HandlersModel;
import org.switchyard.config.model.domain.PropertiesModel;
import org.switchyard.config.model.domain.PropertyModel;
import org.switchyard.config.model.domain.v1.V1DomainModel;
import org.switchyard.config.model.domain.v1.V1HandlerModel;
import org.switchyard.config.model.domain.v1.V1HandlersModel;
import org.switchyard.config.model.domain.v1.V1PropertiesModel;
import org.switchyard.config.model.domain.v1.V1PropertyModel;
import org.switchyard.config.model.resource.ResourceModel;
import org.switchyard.config.model.resource.v1.V1ResourceModel;
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
        } else if (name.equals(DomainModel.DOMAIN)) {
            return new V1DomainModel(config, desc);
        } else if (name.equals(ResourceModel.RESOURCE)) {
            return new V1ResourceModel(config, desc);
        } else if (name.equals(ContextMapperModel.CONTEXT_MAPPER)) {
            return new V1ContextMapperModel(config, desc);
        } else if (name.equals(MessageComposerModel.MESSAGE_COMPOSER)) {
            return new V1MessageComposerModel(config, desc);
        }
        return null;
    }

}
