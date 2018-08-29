/*
 *  Copyright 2018 Petr P.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy testOf the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.progrm_jarvis.reflector;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ru.progrm_jarvis.reflector.Reflector.newUnsafeInstance;

@SuppressWarnings("WeakerAccess")
class ReflectorTest {

    @Test
    void testDigForField() throws Exception {
        assertEquals(
                Optional.of(new ClassMember<>(Object3.class, Object3.class.getDeclaredField("baz"))),
                Reflector.digForField(Object3.class, field -> field.getName().equals("baz"), Object.class)
        );

        assertEquals(Optional.of(
                new ClassMember<>(Object2.class, Object2.class.getDeclaredField("bar"))),
                Reflector.digForField(Object3.class, field -> field.getName().equals("bar"), Object.class)
        );

        assertEquals(
                Optional.of(new ClassMember<>(Object1.class, Object1.class.getDeclaredField("foo"))),
                Reflector.digForField(Object3.class, field -> field.getName().equals("foo"), Object.class)
        );

        assertEquals(
                Optional.of(new ClassMember<>(Object4.class, Object4.class.getDeclaredField("field1"))),
                Reflector.digForField(Object4.class, field -> field.getName().equals("field1"), Object.class)
        );

        assertNull(Reflector.digForField(Object4.class,
                field -> field.getName().equals("lol"), Object.class).orElse(null)
        );

        assertNull(Reflector.digForField(Object3.class,
                field -> field.getName().equals("foo"), Object2.class).orElse(null)
        );
    }

    @Test
    void testDigForMethod() throws Exception {
        assertEquals(
                Optional.of(new ClassMember<>(Object3.class, Object3.class.getDeclaredMethod("baz"))),
                Reflector.digForMethod(Object3.class, field -> field.getName().equals("baz"), Object.class)
        );

        assertEquals(
                Optional.of(new ClassMember<>(Object2.class, Object2.class.getDeclaredMethod("bar"))),
                Reflector.digForMethod(Object3.class, field -> field.getName().equals("bar"), Object.class)
        );

        assertEquals(
                Optional.of(new ClassMember<>(Object1.class, Object1.class.getDeclaredMethod("foo"))),
                Reflector.digForMethod(Object3.class, field -> field.getName().equals("foo"), Object.class)
        );

        assertEquals(
                Optional.of(new ClassMember<>(Interface3.class, Interface3.class.getDeclaredMethod("i3"))),
                Reflector.digForMethod(Object4.class, field -> field.getName().equals("i3"), Object.class)
        );

        assertEquals(
                Optional.of(new ClassMember<>(Interface2.class, Interface2.class.getDeclaredMethod("i2"))),
                Reflector.digForMethod(Object4.class, field -> field.getName().equals("i2"), Object.class)
        );

        assertEquals(
                Optional.of(new ClassMember<>(Interface1.class, Interface1.class.getDeclaredMethod("i1"))),
                Reflector.digForMethod(Object4.class, field -> field.getName().equals("i1"), Object.class)
        );

        assertEquals(
                Optional.of(new ClassMember<>(Object4.class, Object4.class.getDeclaredMethod("method1"))),
                Reflector.digForMethod(Object4.class, field -> field.getName().equals("method1"), Object.class)
        );

        assertNull(Reflector.digForMethod(Object4.class,
                field -> field.getName().equals("lol"), Object.class).orElse(null)
        );

        assertNull(Reflector.digForMethod(Object3.class,
                field -> field.getName().equals("foo"), Object2.class).orElse(null)
        );

        assertNull(Reflector.digForMethod(Object3.class,
                field -> field.getName().equals("i1"), Interface2.class).orElse(null)
        );
    }

    @Test
    @SuppressWarnings({"AssertEqualsBetweenInconvertibleTypes", "JavaReflectionMemberAccess"})
    void testDigForConstructor() throws Exception {
        assertEquals(
                Optional.of(new ClassMember<>(Object3.class, Object3.class.getDeclaredConstructor())),
                Reflector.digForConstructor(Object3.class,
                        constructor -> constructor.getName().endsWith("$Object3"), Object.class)
        );

        assertEquals(
                Optional.of(new ClassMember<>(Object2.class, Object2.class.getDeclaredConstructor())),
                Reflector.digForConstructor(Object3.class,
                        constructor -> constructor.getName().endsWith("$Object2"), Object.class)
        );

        assertEquals(Optional.of(new ClassMember<>(Object1.class, Object1.class.getDeclaredConstructor())),
                Reflector.digForConstructor(Object3.class,
                        constructor -> constructor.getName().endsWith("$Object1"), Object.class)
        );

        assertEquals(
                Optional.of(new ClassMember<>(Object4.class, Object4.class.getDeclaredConstructor())),
                Reflector.digForConstructor(Object4.class,
                        constructor -> constructor.getName().endsWith("$Object4"), Object.class)
        );

        assertEquals(
                Optional.of(new ClassMember<>(Object5.class, Object5.class.getDeclaredConstructor(getClass()))),
                Reflector.digForConstructor(Object5.class,
                        constructor -> constructor.getName().endsWith("$Object5")
                                && constructor.getParameterTypes()[0] == ReflectorTest.class, Object.class)
        );

        assertNull(Reflector.digForConstructor(Object4.class,
                constructor -> constructor.getName().equals("lol"), Object.class).orElse(null)
        );

        assertNull(Reflector.digForConstructor(Object3.class,
                constructor -> constructor.getName().endsWith("$Object1"), Object2.class).orElse(null)
        );
    }

    @Test
    void testUnsafeInstance() {
        assertNotEquals(new Object99(), newUnsafeInstance(Object99.class));
        assertEquals(0, newUnsafeInstance(Object99.class).value);
    }

    private static abstract class Object1 {
        private String foo = "";

        protected abstract void foo();

        public Object1() {}
    }

    private static abstract class Object2 extends Object1 {
        private String bar = "";

        protected abstract void bar();

        public Object2() {}
    }

    private static abstract class Object3 extends Object2 {
        private String baz = "";

        protected abstract void baz();

        public Object3() {}
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

    private static abstract class Object4 extends Object2 implements Interface2, Interface3 {
        private String field1 = "";

        protected abstract void method1();

        public Object4() {}
    }

    private abstract class Object5 {
        public Object5() {}
    }

    public class Object99 {
        private int value;

        public Object99() {
            value = 1;
        }
    }
}