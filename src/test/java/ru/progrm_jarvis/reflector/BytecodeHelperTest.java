package ru.progrm_jarvis.reflector;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class BytecodeHelperTest {

    /* todo
    private final BytecodeHelper bytecodeHelper = create();
    private static final String CLASSNAME_PREFIX = BytecodeHelperTest.class.getTypeName().concat("$");

    @Test
    void testCreate() {
        assertNotNull(create());
        assertNotNull(create(new ClassPool()));
        assertThrows(NullPointerException.class, () -> create(null));
    }

    @Test
    void testPoolMemberEquality() throws NotFoundException {
        assertSame(bytecodeHelper.get("java.lang.String"), bytecodeHelper.get("java.lang.String"));
        {
            final CtClass
                    foo1 = bytecodeHelper.makeClass("$$$MyFooClass"),
                    foo2 = bytecodeHelper.makeClass("$$$MyFooClass"),
                    bar = bytecodeHelper.makeClass("$$$MyBarClass");
            assertNotSame(foo1, foo2);
            assertNotEquals(foo1, bar);
            assertNotEquals(foo2, bar);
        }
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testDelegates() throws ClassNotFoundException, IllegalAccessException, InstantiationException,
            CannotCompileException, NotFoundException {

        val className = "$$lorem.ipsum.dolem.petya.Foo".concat(Long.toString((long) (Long.MAX_VALUE * Math.random())));

        val ctClass = bytecodeHelper.makeClass(className);
        ctClass.addInterface(bytecodeHelper.get(Serializable.class.getCanonicalName()));
        ctClass.toClass();

        val clazz = Class.forName(className);
        assertNotNull(clazz);
        val instance = clazz.newInstance();
        assertNotNull(instance);

        assertTrue(instance instanceof Serializable);
    }

    @Test
    void testInject() throws CannotCompileException {
        bytecodeHelper.inject(CLASSNAME_PREFIX.concat("Foo1"), true,
                new String[]{Bar.class.getTypeName()}, BarImpl.class.getTypeName())
                .toClass();
        assertTrue(Bar.class.isAssignableFrom(Foo1.class));
        assertEquals((byte) 123, ((Bar) new Foo1()).lol());

        bytecodeHelper.inject(CLASSNAME_PREFIX.concat("Foo2"), true,
                new String[]{Bar.class.getTypeName(), Baz.class.getTypeName()},
                BarImpl.class.getTypeName(), BazImpl.class.getTypeName())
                .toClass();
        assertTrue(Bar.class.isAssignableFrom(Foo2.class));
        assertEquals((byte) 123, ((Bar) new Foo2()).lol());
        assertEquals("xD", ((Baz) new Foo2()).lol());
    }

    @Test
    void testInjectBuilder() throws CannotCompileException {
        bytecodeHelper.injectionByNameBuilder()
                .className(CLASSNAME_PREFIX.concat("Foo3"))
                .interfaceName(Bar.class.getTypeName())
                .implementationName(BarImpl.class.getTypeName())
                .inject()
                .toClass();
        assertTrue(Bar.class.isAssignableFrom(Foo3.class));
        assertEquals((byte) 123, ((Bar) new Foo3()).lol());

        bytecodeHelper.injectionByNameBuilder()
                .className(CLASSNAME_PREFIX.concat("Foo4"))
                .interfaceName(Bar.class.getTypeName())
                .interfaceName(Baz.class.getTypeName())
                .implementationName(BarImpl.class.getTypeName())
                .implementationName(BazImpl.class.getTypeName())
                .inject()
                .toClass();
        assertTrue(Bar.class.isAssignableFrom(Foo4.class));
        assertEquals((byte) 123, ((Bar) new Foo4()).lol());
        assertEquals("xD", ((Baz) new Foo4()).lol());
    }

    @Test
    void testInjectInterfaces() throws CannotCompileException {
        bytecodeHelper.inject(CLASSNAME_PREFIX.concat("FooI1"), true,
                new String[]{Lel.class.getTypeName()}, Lel.class.getTypeName())
                .toClass();
        assertTrue(Lel.class.isAssignableFrom(FooI1.class));
        assertEquals(1337, ((Lel) new FooI1()).meme());

        bytecodeHelper.inject(CLASSNAME_PREFIX.concat("FooI2"), true,
                new String[]{Lel2.class.getTypeName()}, Lel2.class.getTypeName())
                .toClass();
        assertTrue(Lel.class.isAssignableFrom(FooI2.class));
        assertEquals(1337, ((Lel) new FooI2()).meme());
        assertEquals(228, ((Lel2) new FooI2()).meme2());
    }

    @Test
    void testInjectInterfacesWithFields() throws CannotCompileException, NoSuchFieldException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        val emptyClass = bytecodeHelper.injectionByNameBuilder()
                .className(CLASSNAME_PREFIX.concat("EmptyClass"))
                .interfaceName(Lox.class.getTypeName())
                .implementationName(LoxImpl.class.getTypeName())
                .copyFields(true)
                .inject()
                .toClass();
        assertNotNull(emptyClass);
        System.out.println();

        val emptyClassInstance = new EmptyClass();
        assertSame(emptyClass, emptyClassInstance.getClass());
        val sField = emptyClass.getDeclaredField("s");
        assertNotNull(sField);
        assertTrue(Modifier.isPrivate(sField.getModifiers()));
        assertThrows(IllegalAccessException.class, () -> sField.get("hahaha"));

        val getMethod = emptyClass.getDeclaredMethod("getS");
        assertNotNull(getMethod);
        val setMethod = emptyClass.getDeclaredMethod("setS", String.class);
        assertNotNull(setMethod);

        sField.setAccessible(true);
        {
            val fieldValue = sField.get(emptyClassInstance); // put in separate statement to check accessibility
            assertEquals(fieldValue, getMethod.invoke(emptyClassInstance));
        }
        assertEquals("<3", getMethod.invoke(emptyClassInstance));

        setMethod.invoke(emptyClassInstance, ":)");

        assertEquals(sField.get(emptyClassInstance), getMethod.invoke(emptyClassInstance));
        assertEquals(":)", getMethod.invoke(emptyClassInstance));
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
    */
}
