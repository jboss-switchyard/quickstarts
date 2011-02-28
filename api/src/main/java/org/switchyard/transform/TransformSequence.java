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

package org.switchyard.transform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.Message;

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
     * Create an {@link #associateWith(org.switchyard.Context) unassociated} sequence.
     */
    private TransformSequence() {
    }

    /**
     * Associate this instance with the supplied message context.
     *
     * @param msgCtx The message context. NB: Will be the Exchange once the "zap on send" issue is resolved.
     */
    public void associateWith(Context msgCtx) {
        msgCtx.setProperty(TransformSequence.class.getName(), this);
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

            Object result = transformer.transform(message.getContent());
            if (result != null) {
                message.setContent(result);

                // We can now remove the 1st element in the sequence.  2nd element will become the
                // "from" for the next transformation in the sequence, if one is required...
                _sequence.remove(0);
            } else {
                LOGGER.warn("Transformer '" + transformer.getClass().getName() + "' returned a null transformation result.  Check input payload matches requirements of the Transformer implementation.");
                break;
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
        TransformSequence transformSequence = get(exchange.getMessage());

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
        TransformSequence transformSequence = get(exchange.getMessage());

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
        TransformSequence transformSequence = get(message);

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

    private static TransformSequence get(final Message message) {
        if (message == null) {
            return null;
        }

        Context context = message.getContext();
        return (TransformSequence) context.getProperty(TransformSequence.class.getName());
    }
}
