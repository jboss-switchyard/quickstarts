package org.switchyard.test;

import java.io.StringReader;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.config.Configuration;
import org.switchyard.config.ConfigurationPuller;
import org.switchyard.test.mixins.AbstractTestMixIn;
import org.switchyard.test.mixins.PropertyMixIn;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SwitchYardTestKitTest {

    @Test
    public void test_property_mixin() throws Exception {
        PropertyMixIn pmi = SwitchYardTestKit.newMixInInstance(PropertyMixIn.class, this);
        Assert.assertNotNull(pmi);
        pmi.set("test.name", "ThyName");
        pmi.set("test.value", Integer.valueOf(100));
        String xml = "<test name='${test.name}'>${test.value}</test>";
        Configuration config = new ConfigurationPuller().pull(new StringReader(xml));
        Assert.assertEquals("ThyName", config.getAttribute("name"));
        Assert.assertEquals("100", config.getValue());
    }

    @Test
    public void test_factory_mixin_creation_static_method() {
        MixIn1 mixIn = SwitchYardTestKit.newMixInInstance(MixIn1.class, this);
        Assert.assertNotNull(mixIn);
        Assert.assertEquals(MixIn1.class.getName(), mixIn.getaVar());
    }

    @Test
    public void test_factory_mixin_creation_nonstatic_method() {
        MixIn2 mixIn = SwitchYardTestKit.newMixInInstance(MixIn2.class, this);
        Assert.assertNotNull(mixIn);
        Assert.assertEquals(MixIn2.class.getName(), mixIn.getaVar());
    }

    @Test
    public void test_mixin_dependency_required() throws Exception {
        SwitchYardTestKit testkit = new SwitchYardTestKit(new MixInDependencyRequiredTest());
        testkit.start();
        List<TestMixIn> mixins = testkit.getMixIns();
        Assert.assertEquals(2, mixins.size());
        MixIn3 mixin5 = MixIn3.class.cast(mixins.get(0));
        MixIn3 mixin4 = MixIn3.class.cast(mixins.get(1));
        Assert.assertEquals(MixIn5.class, mixin5.getClass());
        Assert.assertTrue(mixin5.initialized());
        Assert.assertEquals(MixIn4.class, mixin4.getClass());
        Assert.assertTrue(mixin4.initialized());
        
        testkit.cleanup();
        Assert.assertFalse(mixin4.initialized());
        Assert.assertFalse(mixin5.initialized());
    }
    
    @Test
    public void test_mixin_dependency_optional() throws Exception {
        SwitchYardTestKit testkit = new SwitchYardTestKit(new MixInDependencyOptionalTest());
        testkit.start();
        List<TestMixIn> mixins = testkit.getMixIns();
        Assert.assertEquals(3, mixins.size());
        MixIn3 mixin5 = MixIn3.class.cast(mixins.get(0));
        MixIn3 mixin6 = MixIn3.class.cast(mixins.get(1));
        MixIn3 mixin4 = MixIn3.class.cast(mixins.get(2));
        Assert.assertEquals(MixIn5.class, mixin5.getClass());
        Assert.assertTrue(mixin5.initialized());
        Assert.assertEquals(MixIn6.class, mixin6.getClass());
        Assert.assertTrue(mixin6.initialized());
        Assert.assertEquals(MixIn4.class, mixin4.getClass());
        Assert.assertTrue(mixin4.initialized());
        
        testkit.cleanup();
        Assert.assertFalse(mixin4.initialized());
        Assert.assertFalse(mixin5.initialized());
        Assert.assertFalse(mixin6.initialized());
    }
    
    public static MixIn1 createMixIn1() {
        MixIn1 mixIn1 = new MixIn1();
        mixIn1.setaVar(MixIn1.class.getName());
        return mixIn1;
    }

    public MixIn2 createMixIn2() {
        MixIn2 mixIn2 = new MixIn2();
        mixIn2.setaVar(MixIn2.class.getName());
        return mixIn2;
    }

    public static class MixIn1 extends AbstractTestMixIn {
        private String aVar;

        public String getaVar() {
            return aVar;
        }

        public void setaVar(String aVar) {
            this.aVar = aVar;
        }
    }

    public static class MixIn2 extends AbstractTestMixIn {
        private String aVar;

        public String getaVar() {
            return aVar;
        }

        public void setaVar(String aVar) {
            this.aVar = aVar;
        }
    }
    
    public static class MixIn3 extends AbstractTestMixIn {
        boolean initialized = false;
        
        @Override
        public void initialize() {
            synchronized(this) {
                if (initialized) {
                    Assert.fail(this.getClass().getName() + ": initialized twice");
                } else {
                    initialized = true;
                }
            }
        }
        
        @Override
        public void uninitialize() {
            synchronized(this) {
                if (!initialized) {
                    Assert.fail(this.getClass().getName() + ": uninitialize() is called before initialized");
                } else {
                    initialized = false;
                }
            }
        }
        
        public boolean initialized() {
            return initialized;
        }
    }
    
    @MixInDependencies(required=MixIn5.class, optional=MixIn6.class)
    public static class MixIn4 extends MixIn3 {
    }
    
    public static class MixIn5 extends MixIn3 {
    }
    
    public static class MixIn6 extends MixIn3 {
    }
    
    @SwitchYardTestCaseConfig(mixins=MixIn4.class)
    public class MixInDependencyRequiredTest {
        @Test
        public void dummy() {}
    }
    
    @SwitchYardTestCaseConfig(mixins={MixIn4.class, MixIn6.class})
    public class MixInDependencyOptionalTest {
        @Test
        public void dummy() {}
    }
}
