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

package org.switchyard.transform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.label.BehaviorLabel;

/**
 * Transformation sequence/pipeline.
 * <p/>
 * Allows the stringing together of a sequence of transformers and then associating that
 * with a Message context e.g.
 * <pre>
 * TransformSequence.from("a").to("b").to("c').associateWith(messageContext);
 * </pre>
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public final class TransformSequence implements Serializable {

    /**
     * Serial UID
     */
    static final long serialVersionUID = -1;
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TransformSequence.class);

    /**
     * Transform Sequence.
     */
    private List<QName> _sequence = new ArrayList<QName>();

    /**
     * Create an unassociated sequence.
     */
    private TransformSequence() {
    }

    /**
     * Associate this instance with the supplied message context.
     * @param message associate the transform to this message
     */
    public void associateWith(Message message) {
        message.getContext().setProperty(TransformSequence.class.getName(), this)
            .addLabels(BehaviorLabel.TRANSIENT.label());
    }

    /**
     * Start the transformation sequence.
     *
     * @param typeName The from type.
     * @return The sequence.
     */
    public static TransformSequence from(final QName typeName) {
        TransformSequence newSequence = new TransformSequence();
        newSequence.add(typeName);
        return newSequence;
    }

    /**
     * Add to the transformation sequence.
     *
     * @param typeName The from type.
     * @return The sequence.
     */
    public TransformSequence to(final QName typeName) {
        add(typeName);
        return this;
    }

    /**
     * Apply this {@link TransformSequence} to the supplied {@link Message} instance.
     * @param message Message instance.
     * @param registry Transformation Registry.
     */
    public void apply(Message message, TransformerRegistry registry) {

        while (_sequence.size() > 1) {
            QName from = _sequence.get(0);
            QName to = _sequence.get(1);
            Transformer transformer = registry.getTransformer(from, to);

            if (transformer == null) {
                break;
            }
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Transforming Message (" + System.identityHashCode(message) + ") from '" + transformer.getFrom() + "' to '" 
                    + transformer.getTo() + "' using transformer type '" + transformer.getClass().getName() + "'.");
            }

            Object result;
            if (Message.class.isAssignableFrom(transformer.getFromType())) {
                // A returned object just indicates that the transformation took place.
                result = transformer.transform(message);
            } else {
                // A returned object indicates that the transformation took place and is
                // used as the new Message payload.
                result = transformer.transform(message.getContent(transformer.getFromType()));
                message.setContent(result);
            }
            
            // We can now remove the 1st element in the sequence.  2nd element will become the
            // "from" for the next transformation in the sequence, if one is required...
            _sequence.remove(0);

            if (result == null) {
                String msg ="Transformer '" + transformer.getClass().getName() + "' returned a null transformation result when transforming from type '" 
                    + from + "' to type '" + to + "'. Check input payload matches requirements of the Transformer implementation.";
                LOGGER.warn(msg);
            }
        }
    }

    /**
     * Get the current message type for the specified exchange.
     *
     * @param exchange The exchange.
     * @return The current exchange message type, or null if
     *         no TransformSequence is set on the exchange.
     */
    public static QName getCurrentMessageType(final Exchange exchange) {
        TransformSequence transformSequence = get(exchange);

        if (transformSequence != null && !transformSequence._sequence.isEmpty()) {
            return transformSequence._sequence.get(0);
        }

        return null;
    }

    /**
     * Get the target message type for the specified exchange phase.
     *
     * @param exchange The exchange.
     * @return The target exchange message type, or null if
     *         no TransformSequence is set on the exchange.
     */
    public static QName getTargetMessageType(final Exchange exchange) {
        TransformSequence transformSequence = get(exchange);

        if (transformSequence != null && !transformSequence._sequence.isEmpty()) {
            // Return the last entry in the sequence...
            return transformSequence._sequence.get(transformSequence._sequence.size() - 1);
        }

        return null;
    }

    /**
     * Utility assertion method for checking if the source to destination transformations
     * have been applied to the Exchange.
     *
     * @param exchange The exchange instance.
     * @return True if the transformations have been applied (or are not specified), otherwise false.
     */
    public static boolean assertTransformsApplied(final Exchange exchange) {
        QName fromName = getCurrentMessageType(exchange);
        QName toName = getTargetMessageType(exchange);

        if (fromName != null && toName != null && !fromName.equals(toName)) {
            return false;
        }

        return true;
    }

    /**
     * Apply the active exchange transformation sequence to the supplied
     * Exchange Message.
     *
     * @param exchange The Exchange instance.
     * @param registry The transformation registry.
     */
    public static void applySequence(final Exchange exchange, final TransformerRegistry registry) {
        Message message = exchange.getMessage();
        TransformSequence transformSequence = get(exchange);

        if (transformSequence == null) {
            return;
        }

        transformSequence.apply(message, registry);
    }

    private void add(final QName typeName) {
        if (typeName == null) {
            throw new IllegalArgumentException("null 'typeName' arg passed.");
        }
        _sequence.add(typeName);
    }

    private static TransformSequence get(final Exchange exchange) {
        Property sequenceProperty = exchange.getContext().getProperty(TransformSequence.class.getName());
        if (sequenceProperty != null) {
            return (TransformSequence)sequenceProperty.getValue();
        } else {
            return null;
        }
    }
}
