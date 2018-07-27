package ru.progrm_jarvis.reflector;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;
import static ru.progrm_jarvis.reflector.ByteCodeInjector.create;

class ByteCodeInjectorTest {

    private final ByteCodeInjector classModifier = create();

    @Test
    void testCreate() {
        assertNotNull(create());
        assertNotNull(create(new ClassPool()));
        assertThrows(NullPointerException.class, () -> create(null));
    }

    @Test
    void testPoolMemberEquality() throws NotFoundException {
        assertSame(classModifier.get("java.lang.String"), classModifier.get("java.lang.String"));
        {
            final CtClass
                    foo1 = classModifier.makeClass("$$$MyFooClass"),
                    foo2 = classModifier.makeClass("$$$MyFooClass"),
                    bar = classModifier.makeClass("$$$MyBarClass");
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

        val ctClass = classModifier.makeClass(className);
        ctClass.addInterface(classModifier.get(Serializable.class.getCanonicalName()));
        ctClass.toClass();

        val clazz = Class.forName(className);
        assertNotNull(clazz);
        val instance = clazz.newInstance();
        assertNotNull(instance);

        assertTrue(instance instanceof Serializable);
    }

    @Test
    void testInject() throws CannotCompileException {
        val className = getClass().getTypeName().concat("$Foo");

        classModifier.inject(className, new String[]{Bar.class.getTypeName()}, BarImpl.class.getTypeName())
                .toClass();

        assertTrue(Bar.class.isAssignableFrom(Foo.class));

        assertEquals((byte) 123, ((Bar) new Foo()).lol());
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

    private static class Foo {}
}
