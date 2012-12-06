/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.switchyard.component.camel.amqp.model;

import org.switchyard.component.camel.jms.model.CamelJmsBindingModel;

/**
 * Represents the configuration settings for an Amqp endpoint in Camel.
 *
 * According to the Camel Documentation, Camel JMS and Camel AMQP configurations share
 * all options, thus the reason to extend {@link GenericMqBindingModel}.
 *
 * @author: <a href="mailto:eduardo.devera@gmail.com">Eduardo de Vera</a>
 */
public interface CamelAmqpBindingModel extends CamelJmsBindingModel {
}
