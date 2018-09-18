package ru.progrm_jarvis.reflector.bytecode.mirror;

import javassist.CtClass;
import javassist.NotFoundException;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static ru.progrm_jarvis.reflector.TestUtil.getCtClass;
import static ru.progrm_jarvis.reflector.bytecode.mirror.ClassMemberMirrorer.mirrorerOf;

class BytecodeMirroringTaskTest {

    private static final int VALUE_1337 = 1337;

    @Test
    @SuppressWarnings("ConstantConditions")
    void testRun() throws NotFoundException {
        {
            val abstractClassImpl2CtClass = getCtClass(AbstractClassImpl2.class);

            BytecodeMirroringTask.builder()
                    // target
                    .target(getCtClass(getClass().getTypeName().concat("$TargetClass")))
                    // delegators
                    .delegator(getCtClass(Interface1.class))
                    .delegator(getCtClass(Interface2.class))
                    // implementation details
                    .method(mirrorerOf(getCtClass(AbstractClassImpl1.class).getDeclaredMethod("i1")))
                    .method(mirrorerOf(getCtClass(InterfaceImpl.class).getDeclaredMethod("i2")))
                    .method(mirrorerOf(abstractClassImpl2CtClass.getDeclaredMethod("iPlusPlus")))
                    .method(mirrorerOf(abstractClassImpl2CtClass.getDeclaredMethod("getLeet")))
                    // fields
                    .field(mirrorerOf(abstractClassImpl2CtClass.getDeclaredField("VALUE1")))
                    .field(mirrorerOf(abstractClassImpl2CtClass.getDeclaredField("i")))
                    // callback
                    .callback(CtClass::toClass)
                    // build and run
                    .build()
                    .run();
        }

        // create instance
        val instance = new TargetClass();
        @SuppressWarnings("RedundantCast") // used for suppressing compile-time errors due to incompatible types
        val instanceObject = (Object) instance;

        assertThrows(ExistingMethodReturn.class, instance::existingMethod);

        // Interface1
        assertTrue(instanceObject instanceof Interface1);
        {
            val castInstance = (Interface1) instanceObject;
            assertThrows(I1Return.class, castInstance::i1);
        }

        // Interface2
        assertTrue(instanceObject instanceof Interface2);
        {
            val castInstance = (Interface2) instanceObject;
            assertThrows(I2Return.class, castInstance::i2);

            assertEquals(VALUE_1337, castInstance.getLeet());

            int i = 0;
            for (int j = 0; j < 10; j++) {
                assertEquals(i++, castInstance.iPlusPlus());
            }
        }
    }

    private interface Interface1 {
        void i1();
    }

    private interface Interface2 {
        void i2();

        int iPlusPlus();

        int getLeet();
    }

    private abstract static class AbstractClassImpl1 implements Interface1 {
        @Override
        public void i1() {
            throw new I1Return();
        }
    }

    private interface InterfaceImpl extends Interface2 {
        @Override
        default void i2() {
            throw new I2Return();
        }
    }

    private abstract static class AbstractClassImpl2 implements Interface2{

        private final int VALUE1 = VALUE_1337;

        private int i = 0;

        @Override
        public int iPlusPlus() {
            return i++;
        }

        @Override
        public int getLeet() {
            return VALUE1;
        }
    }

    private static final class TargetClass {

        public void existingMethod() {
            throw new ExistingMethodReturn();
        }
    }

    /*
    Used to indicate that the method is working correctly when it returns void
     */
    private static final class ExistingMethodReturn extends RuntimeException {}

    /*
    Used to indicate that the method is working correctly when it returns void
     */
    private static final class I1Return extends RuntimeException {}

    /*
    Used to indicate that the method is working correctly when it returns void
     */
    private static final class I2Return extends RuntimeException {}
}