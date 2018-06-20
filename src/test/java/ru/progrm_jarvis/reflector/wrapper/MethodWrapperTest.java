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

package ru.progrm_jarvis.reflector.wrapper;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static ru.progrm_jarvis.reflector.wrapper.MethodWrapper.of;

public class MethodWrapperTest {

    @Test
    public void testOf() throws NoSuchMethodException {
        assertThrows(NullPointerException.class, () -> of(null));
        assertNotNull(of(PrivateStaticClass.class.getDeclaredMethod("staticVoidMethod")));
        assertNotNull(of(PrivateStaticClass.class.getDeclaredMethod("staticStringMethod")));
        assertNotNull(of(PrivateStaticClass.class.getDeclaredMethod("voidMethod")));
        assertNotNull(of(PrivateStaticClass.class.getDeclaredMethod("stringMethod")));
    }

    @Test
    public void testInvoke() throws NoSuchMethodException {
        val instance = new PrivateStaticClass();

        {
            val method = of(PrivateStaticClass.class.getDeclaredMethod("staticVoidMethod"));

            assertNull(method.invokeStatic());
            assertNull(method.invoke(null));
            assertNull(method.invoke(instance));
            assertThrows(IllegalArgumentException.class, () -> method.invokeStatic(1));
            assertThrows(IllegalArgumentException.class, () -> method.invoke(null, 1));
            assertThrows(IllegalArgumentException.class, () -> method.invoke(instance, 1));
        }

        {
            val method = of(PrivateStaticClass.class.getDeclaredMethod("staticStringMethod"));

            assertEquals(method.invokeStatic(), "hello");
            assertEquals(method.invoke(null), "hello");
            assertEquals(method.invoke(instance), "hello");
            assertThrows(IllegalArgumentException.class, () -> method.invokeStatic(1));
            assertThrows(IllegalArgumentException.class, () -> method.invoke(null, 1));
            assertThrows(IllegalArgumentException.class, () -> method.invoke(instance, 1));
        }

        {
            val method = of(PrivateStaticClass.class.getDeclaredMethod("voidMethod"));

            assertThrows(NullPointerException.class, method::invokeStatic);
            assertThrows(NullPointerException.class, () -> method.invoke(null));
            assertNull(method.invoke(instance));
            assertThrows(IllegalArgumentException.class, () -> method.invoke(instance, 1));
        }

        {
            val method = of(PrivateStaticClass.class.getDeclaredMethod("stringMethod"));

            assertThrows(NullPointerException.class, method::invokeStatic);
            assertThrows(NullPointerException.class, () -> method.invoke(null));
            assertEquals(method.invoke(instance), "world");
            assertThrows(IllegalArgumentException.class, () -> method.invoke(instance, 1));
        }

        {
            val method = of(PrivateStaticClass.class.getDeclaredMethod("foo", boolean.class));

            assertThrows(IllegalArgumentException.class, method::invokeStatic);
            assertThrows(IllegalArgumentException.class, () -> method.invoke(null));
            assertThrows(IllegalArgumentException.class, () -> method.invoke(instance));
            assertEquals(method.invoke(instance, true), 1);
            assertEquals(method.invoke(instance, false), -1);
        }

        {
            val method = of(PrivateStaticClass.class.getDeclaredMethod("thrower"));
            assertThrows(InvocationTargetException.class, method::invokeStatic);
            assertThrows(InvocationTargetException.class, () -> method.invoke(null));
            assertThrows(InvocationTargetException.class, () -> method.invoke(instance));
        }
    }

    private static final class PrivateStaticClass {

        public static void staticVoidMethod() {}

        public static String staticStringMethod() {
            return "hello";
        }

        public void voidMethod() {}

        public String stringMethod() {
            return "world";
        }

        private static int foo(final boolean value) {
            return value ? 1 : -1;
        }

        private static void thrower() throws CustomException {
            throw new CustomException();
        }

        private static final class CustomException extends Exception {}
    }
}