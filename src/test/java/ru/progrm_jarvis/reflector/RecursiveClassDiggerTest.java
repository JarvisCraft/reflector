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
import ru.progrm_jarvis.reflector.util.ValueContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RecursiveClassDiggerTest {

    @Test
    void testDig() throws Throwable {
        assertEquals(Object3.class, RecursiveClassDigger.<Object3, Void, Throwable>dig(Object3.class,
                clazz -> clazz.getSimpleName().equals("Object3") ? ValueContainer.empty() : null, Object.class)
                .orElseThrow(AssertionError::new).getOwner());

        assertEquals(Object2.class, RecursiveClassDigger.<Object3, Void, Throwable>dig(Object3.class,
                clazz -> clazz.getSimpleName().equals("Object2") ? ValueContainer.empty() : null, Object.class)
                .orElseThrow(AssertionError::new).getOwner());

        assertNull(RecursiveClassDigger.<Object3, Void, Throwable>dig(Object3.class,
                clazz -> clazz.getSimpleName().equals("SomeObject") ? ValueContainer.empty() : null, Object.class)
                .orElse(null));

        assertNull(RecursiveClassDigger.<Object3, Void, Throwable>dig(Object3.class,
                clazz -> clazz.getSimpleName().equals("Object1") ? ValueContainer.empty() : null, Object2.class)
                .orElse(null));
    }

    @Test
    void testDigWithInterfaces() throws Throwable {
        assertEquals(Object4.class, RecursiveClassDigger.<Void, Throwable>digWithInterfaces(Object4.class,
                clazz -> clazz.getSimpleName().equals("Object4") ? ValueContainer.empty() : null, Object.class)
                .orElseThrow(AssertionError::new).getOwner());

        assertEquals(Interface2.class, RecursiveClassDigger.<Void, Throwable>digWithInterfaces(Object4.class,
                clazz -> clazz.getSimpleName().equals("Interface2") ? ValueContainer.empty() : null, Object.class)
                .orElseThrow(AssertionError::new).getOwner());

        assertEquals(Interface1.class, RecursiveClassDigger.<Void, Throwable>digWithInterfaces(Object4.class,
                clazz -> clazz.getSimpleName().equals("Interface1") ? ValueContainer.empty() : null, Object.class)
                .orElseThrow(AssertionError::new).getOwner());

        assertEquals(Interface3.class, RecursiveClassDigger.<Void, Throwable>digWithInterfaces(Object4.class,
                clazz -> clazz.getSimpleName().equals("Interface3") ? ValueContainer.empty() : null, Object.class)
                .orElseThrow(AssertionError::new).getOwner());

        assertNull(RecursiveClassDigger.<Void, Throwable>digWithInterfaces(Object4.class,
                clazz -> clazz.getSimpleName().equals("SomeInterface") ? ValueContainer.empty() : null, Object2.class)
                .orElse(null));

        assertNull(RecursiveClassDigger.<Void, Throwable>digWithInterfaces(Object4.class,
                clazz -> clazz.getSimpleName().equals("Interface1") ? ValueContainer.empty() : null, Interface2.class)
                .orElse(null));
    }

    private abstract class Object1 {
    }

    private abstract class Object2 extends Object1 {
    }

    private abstract class Object3 extends Object2 {
    }

    private interface Interface1 {
    }

    private interface Interface2 extends Interface1 {
    }

    private interface Interface3 {
    }

    private abstract class Object4 extends Object2 implements Interface2, Interface3 {
    }
}