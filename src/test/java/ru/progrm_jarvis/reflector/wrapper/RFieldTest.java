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

import static org.junit.jupiter.api.Assertions.*;
import static ru.progrm_jarvis.reflector.wrapper.RField.of;

public class RFieldTest {

    @Test
    public void testOf() throws NoSuchFieldException {
        assertThrows(NullPointerException.class, () -> of(null));

        assertNotNull(of(PrivateStaticClass_GetTest.class.getDeclaredField("staticObjectField")));
        assertNotNull(of(PrivateStaticClass_GetTest.class.getDeclaredField("staticIntField")));
        assertNotNull(of(PrivateStaticClass_GetTest.class.getDeclaredField("staticFinalStringField")));
        assertNotNull(of(PrivateStaticClass_GetTest.class.getDeclaredField("objectField")));
        assertNotNull(of(PrivateStaticClass_GetTest.class.getDeclaredField("intField")));
        assertNotNull(of(PrivateStaticClass_GetTest.class.getDeclaredField("finalStringField")));

        assertNotNull(of(PrivateStaticClass_SetTest.class.getDeclaredField("staticObjectField")));
        assertNotNull(of(PrivateStaticClass_SetTest.class.getDeclaredField("staticIntField")));
        assertNotNull(of(PrivateStaticClass_SetTest.class.getDeclaredField("staticFinalStringField")));
        assertNotNull(of(PrivateStaticClass_SetTest.class.getDeclaredField("objectField")));
        assertNotNull(of(PrivateStaticClass_SetTest.class.getDeclaredField("intField")));
        assertNotNull(of(PrivateStaticClass_SetTest.class.getDeclaredField("finalStringField")));
    }

    @Test
    public void testGetValue() throws NoSuchFieldException {
        val instance = new PrivateStaticClass_GetTest();

        {
            val field = of(PrivateStaticClass_GetTest.class.getDeclaredField("staticObjectField"));

            assertEquals('a', field.getValue());
            assertEquals('a', field.getValue(null));
            assertEquals('a', field.getValue(instance));
        }

        {
            val field = of(PrivateStaticClass_GetTest.class.getDeclaredField("staticIntField"));

            assertEquals(0, field.getValue());
            assertEquals(0, field.getValue(null));
            assertEquals(0, field.getValue(instance));
        }

        {
            val field = of(PrivateStaticClass_GetTest.class.getDeclaredField("staticFinalStringField"));

            assertEquals("hello", field.getValue());
            assertEquals("hello", field.getValue(null));
            assertEquals("hello", field.getValue(instance));
        }

        {
            val field = of(PrivateStaticClass_GetTest.class.getDeclaredField("objectField"));

            assertThrows(NullPointerException.class, field::getValue);
            assertThrows(NullPointerException.class, () -> field.getValue(null));
            assertEquals('b', field.getValue(instance));
        }

        {
            val field = of(PrivateStaticClass_GetTest.class.getDeclaredField("intField"));

            assertThrows(NullPointerException.class, field::getValue);
            assertThrows(NullPointerException.class, () -> field.getValue(null));
            assertEquals(1, field.getValue(instance));
        }

        {
            val field = of(PrivateStaticClass_GetTest.class.getDeclaredField("finalStringField"));

            assertThrows(NullPointerException.class, field::getValue);
            assertThrows(NullPointerException.class, () -> field.getValue(null));
            assertEquals("world", field.getValue(instance));
        }
    }

    @Test
    public void testSetValue() throws NoSuchFieldException {
        val instance = new PrivateStaticClass_SetTest();

        {
            val field = of(PrivateStaticClass_SetTest.class.getDeclaredField("staticObjectField"));

            field.setValue(null, "hi");
            assertEquals("hi", field.getValue());

            field.setValue('b');
            assertEquals('b', field.getValue());

            field.setValue(instance, "lol");
            assertEquals("lol", field.getValue());

            field.setValue(null);
            assertNull(field.getValue());
        }

        {
            val field = of(PrivateStaticClass_SetTest.class.getDeclaredField("staticIntField"));

            field.setValue(null, 8);
            assertEquals(8, field.getValue());

            field.setValue(7);
            assertEquals(7, field.getValue());

            field.setValue(instance, 9);
            assertEquals(9, field.getValue());

            assertThrows(IllegalArgumentException.class, () -> field.setValue("hi"));
            assertThrows(IllegalArgumentException.class, () -> field.setValue(null, "hi"));
            assertThrows(IllegalArgumentException.class, () -> field.setValue(null));
            assertThrows(IllegalArgumentException.class, () -> field.setValue(null, null));
            assertThrows(IllegalArgumentException.class, () -> field.setValue(instance, null));
        }

        /*
        //
        // static final fields can not be modified due to JVM limitations (see inlining)
        // so this aub-test behaves unexpectedly
        //
        {
            val field = of(PrivateStaticClass_SetTest.class.getDeclaredField("staticFinalStringField"));


            assertThrows(IllegalAccessException.class, () -> field.setValue("lol"));
            assertThrows(IllegalAccessException.class, () -> field.setValue(null, "hi"));
            assertThrows(IllegalAccessException.class, () -> field.setValue(instance, "zzz"));

            assertThrows(IllegalAccessException.class, () -> field.setValue(0));
            assertThrows(IllegalAccessException.class, () -> field.setValue(null, 0));
        }
        */

        {
            val field = of(PrivateStaticClass_SetTest.class.getDeclaredField("objectField"));

            assertThrows(NullPointerException.class, () -> field.setValue(null, "hi"));

            assertThrows(NullPointerException.class, () -> field.setValue('b'));

            field.setValue(instance, "lol");
            assertEquals("lol", field.getValue(instance));

            field.setValue(instance, null);
            assertNull(field.getValue(instance));
        }

        {
            val field = of(PrivateStaticClass_SetTest.class.getDeclaredField("intField"));

            assertThrows(NullPointerException.class, () -> field.setValue(null, 8));

            assertThrows(NullPointerException.class, () -> field.setValue(7));

            field.setValue(instance, 9);
            assertEquals(9, field.getValue(instance));

            assertThrows(NullPointerException.class, () -> field.setValue("hi"));
            assertThrows(NullPointerException.class, () -> field.setValue(null, "hi"));
            assertThrows(NullPointerException.class, () -> field.setValue(null));
            assertThrows(NullPointerException.class, () -> field.setValue(null, null));
            assertThrows(IllegalArgumentException.class, () -> field.setValue(instance, null));
        }

        {
            val field = of(PrivateStaticClass_SetTest.class.getDeclaredField("finalStringField"));

            assertThrows(NullPointerException.class, () -> field.setValue("lol"));

            assertThrows(NullPointerException.class, () -> field.setValue(null, "hi"));

            field.setValue(instance, "zzz");
            assertEquals("zzz", field.getValue(instance));

            field.setValue(instance, null);
            assertNull(field.getValue(instance));

            assertThrows(NullPointerException.class, () -> field.setValue(0));
            assertThrows(NullPointerException.class, () -> field.setValue(null, 0));
            assertThrows(IllegalArgumentException.class, () -> field.setValue(instance, 0));
        }
    }

    private static class PrivateStaticClass_GetTest {
        private static Object staticObjectField = 'a';
        private static int staticIntField = 0;
        private static final String staticFinalStringField = "hello";

        private Object objectField = 'b';
        private int intField = 1;
        private final String finalStringField = "world";
    }

    private static class PrivateStaticClass_SetTest {
        private static Object staticObjectField = 'a';
        private static int staticIntField = 0;
        private static final String staticFinalStringField = "hello";

        private Object objectField = 'b';
        private int intField = 1;
        private final String finalStringField = "world";
    }
}