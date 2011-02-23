package org.switchyard.component.bean.config.model;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.switchyard.component.bean.Service;
import org.switchyard.component.bean.config.model.v1.V1BeanComponentImplementationModel;
import org.switchyard.config.model.Scanner;
import org.switchyard.config.util.classpath.ClasspathScanner;
import org.switchyard.config.util.classpath.IsAnnotationPresentFilter;
import org.switchyard.config.util.classpath.ResourceExistsFilter;

/**
 * Bean Scanner.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanSwitchYardScanner implements Scanner<BeanComponentImplementationModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BeanComponentImplementationModel> scan(List<URL> urls) throws IOException {
        List<Class<?>> serviceClasses = scanForServiceBeans(urls);
        List<BeanComponentImplementationModel> beanModels = new ArrayList<BeanComponentImplementationModel>();

        for (Class<?> serviceClass : serviceClasses) {
            if (serviceClass.isInterface()) {
                continue;
            }
            if (Modifier.isAbstract(serviceClass.getModifiers())) {
                continue;
            }

            V1BeanComponentImplementationModel beanModel = new V1BeanComponentImplementationModel();
            beanModel.setClazz(serviceClass.getName());
            beanModels.add(beanModel);
        }

        return beanModels;
    }

    private List<Class<?>> scanForServiceBeans(List<URL> urls) throws IOException {
        IsAnnotationPresentFilter filter = new IsAnnotationPresentFilter(Service.class);
        ClasspathScanner serviceScanner = new ClasspathScanner(filter);

        for (URL url : urls) {
            // Only scan the url for @Services if the target contains a CDI beans.xml resource...
            if(ifBeansXMLOnPath(url)) {
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
