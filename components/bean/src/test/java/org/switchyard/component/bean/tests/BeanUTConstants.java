package org.switchyard.component.bean.tests;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.switchyard.component.bean.deploy.CDIBeanServiceDescriptorTest;

public final class BeanUTConstants {
    public static final List<Package> BEAN_SCANNER_BLACK_LIST =
            Collections.unmodifiableList(
                    Arrays.asList(new Package[] {CDIBeanServiceDescriptorTest.class.getPackage()}));
}
