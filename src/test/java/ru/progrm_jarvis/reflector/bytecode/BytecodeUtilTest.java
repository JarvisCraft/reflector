package ru.progrm_jarvis.reflector.bytecode;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;
import static ru.progrm_jarvis.reflector.TestUtil.getCtClass;
import static ru.progrm_jarvis.reflector.TestUtil.makeCtClass;

class BytecodeUtilTest {

    private static final String CLASSNAME_PREFIX = BytecodeUtilTest.class.getTypeName().concat("$");

    @Test
    @SuppressWarnings("ConstantConditions")
    void testDelegates() throws ClassNotFoundException, IllegalAccessException, InstantiationException,
            CannotCompileException, NotFoundException {

        val className = "$$lorem.ipsum.dolem.petya.Foo".concat(Long.toString((long) (Long.MAX_VALUE * Math.random())));

        val ctClass = makeCtClass(className);
        ctClass.addInterface(getCtClass(Serializable.class));
        ctClass.toClass();

        val clazz = Class.forName(className);
        assertNotNull(clazz);
        val instance = clazz.newInstance();
        assertNotNull(instance);

        assertTrue(instance instanceof Serializable);
    }

    @Test
    void testGetParents() throws NotFoundException {
        val zParents = BytecodeUtil.getParents(getCtClass(Z.class), false);

        assertEquals(4, zParents.size());
        assertTrue(zParents.contains(getCtClass(A.class)));
        assertTrue(zParents.contains(getCtClass(B.class)));
        assertTrue(zParents.contains(getCtClass(C.class)));
        assertTrue(zParents.contains(getCtClass(D.class)));
    }

    @Test
    void testIsChildSignature() throws NotFoundException {
        val parent = getCtClass(InterfaceDeclaringMethods.class);
        val notParent = getCtClass(InterfaceDeclaringOtherMethods.class);
        val child = getCtClass(ClassWithMethods.class);

        assertTrue(BytecodeUtil.isChildSignature(child.getDeclaredMethod("m1"), parent.getDeclaredMethod("m1")));
        assertFalse(BytecodeUtil.isChildSignature(child.getDeclaredMethod("m2"), notParent.getDeclaredMethod("m2")));
    }

    private interface Bar {
        byte lol();
    }

    private static class BarImpl implements Bar {
        @Override
        public byte lol() {
            return 123;
        }
    }

    private interface Baz {
        String lol();
    }

    private static class BazImpl implements Baz {
        @Override
        public String lol() {
            return "xD";
        }
    }

    private interface Lel {
        default int meme() {
            return 1337;
        }
    }

    private interface Lel2 extends Lel {
        default int meme2() {
            return 228;
        }
    }

    private static class Foo1 {}

    private static class Foo2 {}

    private static class FooI1 {}

    private static class FooI2 {}

    private static class Foo3 {}

    private static class Foo4 {}

    private interface Lox {

        void setS(final String s);
        String getS();
    }

    static class SomeObj {}

    private static abstract class LoxImpl implements Lox {
        private String s = "<3";
        private int num = 0xFF;
        private SomeObj obj = new SomeObj();
        private long l = 127;

        public LoxImpl() {
            l = -127;
        }

        @Override
        public String getS() {
            return s;
        }

        @Override
        public void setS(final String s) {
            this.s = s;
        }
    }

    private static final class EmptyClass {}

    private interface InterfaceDeclaringMethods {
        String m1(String a) throws RuntimeException, Error;
    }

    private interface InterfaceDeclaringOtherMethods {
        String m2(String a) throws RuntimeException, Error;
    }

    private class ClassWithMethods implements InterfaceDeclaringMethods {

        @Override
        public String m1(final String a) throws IllegalArgumentException {
            return "<3";
        }

        public Object m2(final String a) throws IllegalArgumentException {
            return "<3";
        }
    }

    private interface A {}

    private interface B {}

    private interface C extends B {}

    private static abstract class D {}

    private final class Z extends D implements A, C {}
}
