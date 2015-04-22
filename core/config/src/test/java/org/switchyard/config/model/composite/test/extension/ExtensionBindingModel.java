package org.switchyard.config.model.composite.test.extension;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1BindingModel;

public class ExtensionBindingModel extends V1BindingModel {

    private static final String NAME = "name";
    private static final String GROUP = "group";

    public ExtensionBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    public String getName() {
        return getConfig(NAME);
    }

    public String getGroup() {
        return getConfig(GROUP);
    }

    protected String getConfig(String configName) {
        Configuration config = getModelConfiguration().getFirstChild(configName);
        if (config != null) {
            return config.getValue();
        }
        return null;
    }

}
