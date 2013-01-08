package org.switchyard.component.camel.common.marshaller;

import static org.switchyard.config.model.composer.ContextMapperModel.CONTEXT_MAPPER;
import static org.switchyard.config.model.composer.MessageComposerModel.MESSAGE_COMPOSER;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.component.camel.common.model.CamelBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseMarshaller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composer.v1.V1ContextMapperModel;
import org.switchyard.config.model.composer.v1.V1MessageComposerModel;
import org.switchyard.config.model.composite.BindingModel;

/**
 * Base class for camel marshallers. Provides name - {@link ModelCreator} registry
 * which eliminates if-elseif conditions from extensions.
 * 
 * @author Lukasz Dywicki
 */
public class BaseModelMarshaller extends BaseMarshaller {

    private Map<QName, ModelCreator<? extends Model>> _registrations =
        new HashMap<QName, ModelCreator<? extends Model>>();

    /**
     * Default namespace of registered models.
     */
    private String _namespace;

    /**
     * Creates marshaller with given descriptor and bounds by default bindings
     * and other elements to given namespace.
     * 
     * @param desc Descriptor.
     * @param defaultNamespace Default namespace of model elements.
     */
    protected BaseModelMarshaller(Descriptor desc, String defaultNamespace) {
        super(desc);
        _namespace = defaultNamespace;

        register(_namespace, CONTEXT_MAPPER, new ModelCreator<Model>() {
            @Override
            public V1ContextMapperModel create(Configuration config, Descriptor descriptor) {
                return new V1ContextMapperModel(config, descriptor);
            }
        });
        register(_namespace, MESSAGE_COMPOSER, new ModelCreator<Model>() {
            @Override
            public V1MessageComposerModel create(Configuration config, Descriptor descriptor) {
                return new V1MessageComposerModel(config, descriptor);
            }
        });
    }

    @Override
    public Model read(Configuration config) {
        if (_registrations.containsKey(config.getQName())) {
            return _registrations.get(config.getQName()).create(config, getDescriptor());
        }
        return null;
    }

    /**
     * Registers element handler.
     * 
     * @param namespace Element namespace.
     * @param name Element name.
     * @param callback Model creator which produces model instance for element.
     */
    protected <T extends Model> void register(String namespace, String name, ModelCreator<T> callback) {
        _registrations.put(new QName(namespace, name), callback);
    }

    /**
     * Registers binding element.
     * 
     * @param namespace Binding namespace.
     * @param name Binding element name, without binding. prefix.
     * @param callback Model creator which produces model instance for element.
     */
    protected <T extends CamelBindingModel> void registerBinding(String namespace, String name, ModelCreator<T> callback) {
        register(namespace, BindingModel.BINDING + "." + name, callback);
    }

    /**
     * Registers element handler under default namespace.
     * 
     * @param name Element name.
     * @param callback Model creator which produces model instance for element.
     */
    protected <T extends Model> void register(String name, ModelCreator<T> callback) {
        register(_namespace, name, callback);
    }

    /**
     * Registers binging element handler under default namespace.
     * 
     * @param name Binding name.
     * @param callback Model creator which produces model instance for element.
     */
    protected <T extends CamelBindingModel> void registerBinding(String name, ModelCreator<T> callback) {
        registerBinding(_namespace, name, callback);
    }

}
