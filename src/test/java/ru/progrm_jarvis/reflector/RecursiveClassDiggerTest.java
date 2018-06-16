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

package ru.progrm_jarvis.reflector;

import org.junit.Test;
import ru.progrm_jarvis.reflector.util.Possible;

import static org.junit.Assert.*;

public class RecursiveClassDiggerTest {

    @Test
    public void testDig() throws Throwable {
        assertEquals(Object3.class, RecursiveClassDigger.<Object3, Void, Throwable>dig(Object3.class,
                clazz -> clazz.getSimpleName().equals("Object3") ? Possible.empty() : null, Object.class)
                .orElseThrow(AssertionError::new).getOwner());

        assertEquals(Object2.class, RecursiveClassDigger.<Object3, Void, Throwable>dig(Object3.class,
                clazz -> clazz.getSimpleName().equals("Object2") ? Possible.empty() : null, Object.class)
                .orElseThrow(AssertionError::new).getOwner());

        assertNull(RecursiveClassDigger.<Object3, Void, Throwable>dig(Object3.class,
                clazz -> clazz.getSimpleName().equals("SomeObject") ? Possible.empty() : null, Object.class)
                .orElse(null));

        assertNull(RecursiveClassDigger.<Object3, Void, Throwable>dig(Object3.class,
                clazz -> clazz.getSimpleName().equals("Object1") ? Possible.empty() : null, Object2.class)
                .orElse(null));
    }

    @Test
    public void testDigWithInterfaces() throws Throwable {
        assertEquals(Object4.class, RecursiveClassDigger.<Void, Throwable>digWithInterfaces(Object4.class,
                clazz -> clazz.getSimpleName().equals("Object4") ? Possible.empty() : null, Object.class)
                .orElseThrow(AssertionError::new).getOwner());

        assertEquals(Interface2.class, RecursiveClassDigger.<Void, Throwable>digWithInterfaces(Object4.class,
                clazz -> clazz.getSimpleName().equals("Interface2") ? Possible.empty() : null, Object.class)
                .orElseThrow(AssertionError::new).getOwner());

        assertEquals(Interface1.class, RecursiveClassDigger.<Void, Throwable>digWithInterfaces(Object4.class,
                clazz -> clazz.getSimpleName().equals("Interface1") ? Possible.empty() : null, Object.class)
                .orElseThrow(AssertionError::new).getOwner());

        assertEquals(Interface3.class, RecursiveClassDigger.<Void, Throwable>digWithInterfaces(Object4.class,
                clazz -> clazz.getSimpleName().equals("Interface3") ? Possible.empty() : null, Object.class)
                .orElseThrow(AssertionError::new).getOwner());

        assertNull(RecursiveClassDigger.<Void, Throwable>digWithInterfaces(Object4.class,
                clazz -> clazz.getSimpleName().equals("SomeInterface") ? Possible.empty() : null, Object2.class)
                .orElse(null));

        assertNull(RecursiveClassDigger.<Void, Throwable>digWithInterfaces(Object4.class,
                clazz -> clazz.getSimpleName().equals("Interface1") ? Possible.empty() : null, Interface2.class)
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