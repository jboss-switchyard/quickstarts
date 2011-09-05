package org.switchyard.test;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.test.mixins.AbstractTestMixIn;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SwitchYardTestKitTest {

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
}
