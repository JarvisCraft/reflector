/*
 *  Copyright 2018 Petr P.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
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
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static ru.progrm_jarvis.reflector.wrapper.RField.*;

import static org.junit.Assert.*;

public class RFieldTest {

    @Test
    public void testGetValue() throws Throwable {
        assertEquals(
                "Hello".length(),
                ((char[]) of(String.class.getDeclaredField("value")).getValue("Hello")).length
        );

        assertEquals(
                StandardCharsets.UTF_8,
                of(StandardCharsets.class.getDeclaredField("UTF_8")).getValue()
        );
    }

    @Test
    public void testSetValue() throws Throwable {
        {
            val testString = "abc";
            of(String.class.getDeclaredField("value")).setValue(testString, "def".toCharArray());
            assertEquals(testString, "def");
        }
        {
            of(Object1.class.getDeclaredField("field")).setValue(1);
            assertEquals(1, Object1.field);
        }
    }

    private static class Object1 {
        private static final Object field = 0;
    }
}