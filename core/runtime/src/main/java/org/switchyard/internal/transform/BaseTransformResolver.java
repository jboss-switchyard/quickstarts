package org.switchyard.internal.transform;

import java.util.LinkedList;

import javax.xml.namespace.QName;

import org.switchyard.transform.TransformResolver;
import org.switchyard.transform.TransformSequence;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerRegistry;

/**
 * Base implementation of the TransformResolver strategy that facilitates resolving of 
 * direct/indirect transform sequences.
 */
public class BaseTransformResolver implements TransformResolver {
    
    /**
     * The maximum number of edges to travel in the graph to connect two nodes.
     */
    public static final int DEFAULT_HOPS = 2;
    
    private int _hops = DEFAULT_HOPS;
    private TransformerRegistry _registry;

    /**
     * Create a new TransformResolver instance.
     */    
    public BaseTransformResolver() {
        
    }

    /**
     * Create a new transform resolver that will traverse the graph the specified distance
     * when searching for transform sequences.
     * @param numHops max number of edges to travel when searching for a sequence
     */
    public BaseTransformResolver(int numHops) {
        _hops = numHops;
    }

    /**
     * Create a new TransformResolver instance and associate it with a TransformRegistry.
     * @param registry registry to use when searching for a transform path
     */
    public BaseTransformResolver(TransformerRegistry registry) {
        _registry = registry;
    }
    
    /**
     * Create a new TransformResolver instance and associate it with a TransformRegistry.
     * @param registry set of transformers to add to registry
     * @param numHops max number of edges to travel when searching for a sequence
     */
    public BaseTransformResolver(TransformerRegistry registry, int numHops) {
        _registry = registry;
        _hops = numHops;
    }


    @Override
    public TransformSequence resolveSequence(QName from, QName to) {
        // if either one of these is null then there's no chance a sequence will be found
        if (from == null || to == null) {
            return null;
        }
        
        // if there's a direct hit, set that and bail
        if (_registry.hasTransformer(from, to)) {
            return TransformSequence.from(from).to(to);
        }

        TransformSequence transformSequence = null;
        LinkedList<QName> path = new LinkedList<QName>();
        
        // walk the graph to see if we can resolve the path
        if (resolvePath(path, from, to, _hops)) {
            transformSequence = TransformSequence.from(from);
            for (QName type : path) {
                transformSequence.to(type);
            }
        }
        return transformSequence;
    }

    /**
     * Get the associated TransformerRegistry.
     * @return TransformerRegistry.
     */
    public TransformerRegistry getRegistry() {
        return _registry;
    }

    /**
     * Sets the TransformerRegistry instance.
     * @param registry transformer registry
     */
    public void setRegistry(TransformerRegistry registry) {
        this._registry = registry;
    }

    /**
     * Recursive depth-first(ish) search for connected types in the transform registry.  There
     * is no path weighting applied, so the first connection wins. There is no logic to detect 
     * cycles since the limit parameter prevents infinite loops.
     */
    @SuppressWarnings("rawtypes")
    boolean resolvePath(LinkedList<QName> path, QName fromType, QName toType, int limit) {
        // check search limit
        if (limit < 0) {
            return false;
        }
        --limit;
        
        // have we arrived at our destination?
        if (fromType.equals(toType)) {
            return true;
        }
        
        // go fish
        for (Transformer fromT : _registry.getTransformersFrom(fromType)) {
            if (resolvePath(path, fromT.getTo(), toType, limit)) {
                path.addFirst(fromT.getTo());
                return true;
            }
        }
        
        return false;
    }
    
}
