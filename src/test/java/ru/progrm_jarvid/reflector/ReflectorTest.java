package ru.progrm_jarvid.reflector;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static ru.progrm_jarvid.reflector.Reflector.*;

public class ReflectorTest {

    @Test
    public void testDigForField() throws Exception {
        assertEquals(Optional.of(new ClassMember<>(Object3.class, Object3.class.getDeclaredField("baz"))),
                digForField(Object3.class, field -> field.getName().equals("baz"), Object.class));

        assertEquals(Optional.of(new ClassMember<>(Object2.class, Object2.class.getDeclaredField("bar"))),
                digForField(Object3.class, field -> field.getName().equals("bar"), Object.class));

        assertEquals(Optional.of(new ClassMember<>(Object1.class, Object1.class.getDeclaredField("foo"))),
                digForField(Object3.class, field -> field.getName().equals("foo"), Object.class));

        assertEquals(Optional.of(new ClassMember<>(Object4.class, Object4.class.getDeclaredField("field1"))),
                digForField(Object4.class, field -> field.getName().equals("field1"), Object.class));

        assertNull(digForField(Object4.class, field -> field.getName().equals("lol"), Object.class).orElse(null));

        assertNull(digForField(Object3.class, field -> field.getName().equals("foo"), Object2.class).orElse(null));
    }
    @Test
    public void testDigForMethod() throws Exception {
        assertEquals(Optional.of(new ClassMember<>(Object3.class, Object3.class.getDeclaredMethod("baz"))),
                digForMethod(Object3.class, field -> field.getName().equals("baz"), Object.class));

        assertEquals(Optional.of(new ClassMember<>(Object2.class, Object2.class.getDeclaredMethod("bar"))),
                digForMethod(Object3.class, field -> field.getName().equals("bar"), Object.class));

        assertEquals(Optional.of(new ClassMember<>(Object1.class, Object1.class.getDeclaredMethod("foo"))),
                digForMethod(Object3.class, field -> field.getName().equals("foo"), Object.class));

        assertEquals(Optional.of(new ClassMember<>(Interface3.class, Interface3.class.getDeclaredMethod("i3"))),
                digForMethod(Object4.class, field -> field.getName().equals("i3"), Object.class));

        assertEquals(Optional.of(new ClassMember<>(Interface2.class, Interface2.class.getDeclaredMethod("i2"))),
                digForMethod(Object4.class, field -> field.getName().equals("i2"), Object.class));

        assertEquals(Optional.of(new ClassMember<>(Interface1.class, Interface1.class.getDeclaredMethod("i1"))),
                digForMethod(Object4.class, field -> field.getName().equals("i1"), Object.class));

        assertEquals(Optional.of(new ClassMember<>(Object4.class, Object4.class.getDeclaredMethod("method1"))),
                digForMethod(Object4.class, field -> field.getName().equals("method1"), Object.class));

        assertNull(digForMethod(Object4.class, field -> field.getName().equals("lol"), Object.class).orElse(null));

        assertNull(digForMethod(Object3.class, field -> field.getName().equals("foo"), Object2.class).orElse(null));

        assertNull(digForMethod(Object3.class, field -> field.getName().equals("i1"), Interface2.class).orElse(null));
    }

    private abstract class Object1 {
        private String foo = "";
        protected abstract void foo();
    }

    private abstract class Object2 extends Object1 {
        private String bar = "";
        protected abstract void bar();
    }

    private abstract class Object3 extends Object2 {
        private String baz = "";
        protected abstract void baz();
    }

    private interface Interface1 {
        void i1();
    }

    private interface Interface2 extends Interface1 {
        void i2();
    }

    private interface Interface3 {
        void i3();
    }

    private abstract class Object4 extends Object2 implements Interface2, Interface3 {
        private String field1 = "";
        protected abstract void method1();
    }
}