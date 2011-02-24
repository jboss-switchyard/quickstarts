package org.switchyard.component.bean.config.model;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.List;

import org.switchyard.component.bean.Service;
import org.switchyard.component.bean.config.model.v1.V1BeanComponentImplementationModel;
import org.switchyard.config.model.Scanner;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.ScannerOutput;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.v1.V1ComponentModel;
import org.switchyard.config.model.composite.v1.V1CompositeModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardModel;
import org.switchyard.config.util.classpath.ClasspathScanner;
import org.switchyard.config.util.classpath.IsAnnotationPresentFilter;
import org.switchyard.config.util.classpath.ResourceExistsFilter;

/**
 * Bean Scanner.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanSwitchYardScanner implements Scanner<SwitchYardModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ScannerOutput<SwitchYardModel> scan(ScannerInput<SwitchYardModel> input) throws IOException {
        SwitchYardModel switchyardModel = new V1SwitchYardModel();
        CompositeModel compositeModel = new V1CompositeModel();
        compositeModel.setName(input.getName());
        switchyardModel.setComposite(compositeModel);

        List<Class<?>> serviceClasses = scanForServiceBeans(input.getURLs());

        for (Class<?> serviceClass : serviceClasses) {
            if (serviceClass.isInterface()) {
                continue;
            }
            if (Modifier.isAbstract(serviceClass.getModifiers())) {
                continue;
            }

            ComponentModel componentModel = new V1ComponentModel();
            String componentName;
            Class<?>[] componentIfaces = serviceClass.getInterfaces();
            if (componentIfaces.length > 0) {
                componentName = componentIfaces[0].getSimpleName();
            } else {
                componentName = serviceClass.getSimpleName();
            }
            componentModel.setName(componentName);
            compositeModel.addComponent(componentModel);

            BeanComponentImplementationModel beanModel = new V1BeanComponentImplementationModel();
            beanModel.setClazz(serviceClass.getName());
            componentModel.setImplementation(beanModel);
        }

        return new ScannerOutput<SwitchYardModel>().setModel(switchyardModel);
    }

    private List<Class<?>> scanForServiceBeans(List<URL> urls) throws IOException {
        IsAnnotationPresentFilter filter = new IsAnnotationPresentFilter(Service.class);
        ClasspathScanner serviceScanner = new ClasspathScanner(filter);

        for (URL url : urls) {
            // Only scan the url for @Services if the target contains a CDI beans.xml resource...
            if (ifBeansXMLOnPath(url)) {
                serviceScanner.scan(url);
            }
        }

        return filter.getMatchedTypes();
    }

    private boolean ifBeansXMLOnPath(URL url) throws IOException {
        ResourceExistsFilter beansXmlFilter = new ResourceExistsFilter("META-INF/beans.xml");
        ClasspathScanner beansXmlScanner = new ClasspathScanner(beansXmlFilter);

        beansXmlScanner.scan(url);
        return beansXmlFilter.resourceExists();
    }
}
