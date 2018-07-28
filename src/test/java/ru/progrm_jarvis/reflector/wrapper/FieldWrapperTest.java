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
import static ru.progrm_jarvis.reflector.wrapper.FieldWrapper.of;

public class FieldWrapperTest {

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

        assertNotNull(of(PrivateStaticClass_UpdateTest.class.getDeclaredField("intField")));
        assertNotNull(of(PrivateStaticClass_UpdateTest.class.getDeclaredField("finalIntField")));
        assertNotNull(of(PrivateStaticClass_UpdateTest.class.getDeclaredField("staticIntField")));
        assertNotNull(of(PrivateStaticClass_UpdateTest.class.getDeclaredField("stringField")));
        assertNotNull(of(PrivateStaticClass_UpdateTest.class.getDeclaredField("finalStringField")));
        assertNotNull(of(PrivateStaticClass_UpdateTest.class.getDeclaredField("staticStringField")));
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

        {
            val field = of(PrivateStaticClass_SetTest.class.getDeclaredField("staticFinalStringField"));

            field.setValue("lol");
            assertEquals("lol", field.getValue());
            field.setValue(null, "hi");
            assertEquals("hi", field.getValue());
            field.setValue(instance, "zzz");
            assertEquals("zzz", field.getValue());

            assertThrows(IllegalArgumentException.class, () -> field.setValue(0));
            assertThrows(IllegalArgumentException.class, () -> field.setValue(null, 0));
            assertThrows(IllegalArgumentException.class, () -> field.setValue(instance, 0));
        }

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

    @Test
    public void testUpdateValue() throws NoSuchFieldException {
        val instance = new PrivateStaticClass_UpdateTest();
        {
            val field = FieldWrapper.<PrivateStaticClass_UpdateTest, Integer>of(
                    PrivateStaticClass_UpdateTest.class.getDeclaredField("intField")
            );
            assertThrows(NullPointerException.class, () -> field.updateValue(2));
            assertThrows(NullPointerException.class, () -> field.updateValue(null, 2));

            assertEquals(0, (int) field.updateValue(instance, value -> value + 5 + 1));
            assertEquals(6, (int) field.getValue(instance));
            assertEquals((int) field.updateValue(instance, 10), 6);
            assertEquals((int) field.getValue(instance), 10);

            assertThrows(IllegalArgumentException.class, () -> field.updateValue(instance, (Integer) null));
        }

        {
            val field = FieldWrapper.<PrivateStaticClass_UpdateTest, Integer>of(
                    PrivateStaticClass_UpdateTest.class.getDeclaredField("finalIntField")
            );
            assertThrows(NullPointerException.class, () -> field.updateValue(2));
            assertThrows(NullPointerException.class, () -> field.updateValue(null, 2));

            assertEquals(10, (int) field.updateValue(instance, value -> value + 5 + 1));
            assertEquals(16, (int) field.getValue(instance));
            assertEquals((int) field.updateValue(instance, 20), 16);
            assertEquals((int) field.getValue(instance), 20);

            assertThrows(IllegalArgumentException.class, () -> field.updateValue(instance, (Integer) null));
        }

        {
            val field = FieldWrapper.<PrivateStaticClass_UpdateTest, Integer>of(
                    PrivateStaticClass_UpdateTest.class.getDeclaredField("staticIntField")
            );
            assertEquals(0, (int) field.updateValue(2));
            assertEquals(2, (int) field.getValue());
            assertEquals(2, (int) field.getValue(null));
            assertEquals(2, (int) field.getValue(instance));

            assertEquals(2, (int) field.updateValue(null, 4));
            assertEquals(4, (int) field.getValue());
            assertEquals(4, (int) field.getValue(null));
            assertEquals(4, (int) field.getValue(instance));

            assertEquals(4, (int) field.updateValue(value -> value + 5 + 1));
            assertEquals(10, (int) field.getValue());
            assertEquals(10, (int) field.getValue(null));
            assertEquals(10, (int) field.getValue(instance));

            assertThrows(IllegalArgumentException.class, () -> field.updateValue((Integer) null));
            assertThrows(IllegalArgumentException.class, () -> field.updateValue(null, (Integer) null));
            assertThrows(IllegalArgumentException.class, () -> field.updateValue(instance, (Integer) null));
        }

        {
            val field = FieldWrapper.<PrivateStaticClass_UpdateTest, String>of(
                    PrivateStaticClass_UpdateTest.class.getDeclaredField("stringField")
            );
            assertThrows(NullPointerException.class, () -> field.updateValue("q"));
            assertThrows(NullPointerException.class, () -> field.updateValue(null, "w"));

            assertEquals("hello", field.updateValue(instance, value -> value.concat(" world")));
            assertEquals("hello world", field.getValue(instance));
            assertEquals(field.updateValue(instance, "<3"), "hello world");
            assertEquals(field.getValue(instance), "<3");

            assertEquals("<3", field.updateValue(instance, (String) null));
            assertNull(field.getValue(instance));
        }

        {
            val field = FieldWrapper.<PrivateStaticClass_UpdateTest, String>of(
                    PrivateStaticClass_UpdateTest.class.getDeclaredField("finalStringField")
            );
            assertThrows(NullPointerException.class, () -> field.updateValue("q"));
            assertThrows(NullPointerException.class, () -> field.updateValue(null, "w"));

            assertEquals("value1", field.updateValue(instance, "yay"));
            assertEquals("yay", field.getValue(instance));

            assertEquals("yay", field.updateValue(instance, value -> value.concat("ay")));
            assertEquals("yayay", field.getValue(instance));

            assertEquals("yayay", field.updateValue(instance, (String) null));
            assertNull(field.getValue(instance));
        }

        {
            val field = FieldWrapper.<PrivateStaticClass_UpdateTest, String>of(
                    PrivateStaticClass_UpdateTest.class.getDeclaredField("staticStringField")
            );
            assertEquals("hi", field.updateValue("one"));
            assertEquals("one", field.getValue());
            assertEquals("one", field.getValue(null));
            assertEquals("one", field.getValue(instance));

            assertEquals("one", field.updateValue(null, "two"));
            assertEquals("two", field.getValue());
            assertEquals("two", field.getValue(null));
            assertEquals("two", field.getValue(instance));

            assertEquals("two", field.updateValue(instance, "three"));
            assertEquals("three", field.getValue());
            assertEquals("three", field.getValue(null));
            assertEquals("three", field.getValue(instance));

            assertEquals("three", field.updateValue(value -> value.replace("h", "")));
            assertEquals("tree", field.getValue());
            assertEquals("tree", field.getValue(null));
            assertEquals("tree", field.getValue(instance));

            assertEquals("tree", field.updateValue((String) null));
            assertNull(field.getValue());
            assertNull(field.getValue(null));
            assertNull(field.getValue(instance));
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

    private static class PrivateStaticClass_UpdateTest {
        private int intField = 0;
        private final int finalIntField = 10;
        private static int staticIntField = 0;
        private String stringField = "hello";
        private final String finalStringField = "value1";
        private static String staticStringField = "hi";
    }
}