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

import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static ru.progrm_jarvis.reflector.UnsafeUtil.useUnsafeAndGet;

public class UnsafeUtilTest {

    @Test
    public void testUseUnsafeAndGet() throws Throwable {
        assertNotEquals(new Object1(), useUnsafeAndGet(unsafe -> unsafe.allocateInstance(Object1.class)));
        assertEquals(0, ((Object1) useUnsafeAndGet(unsafe -> unsafe.allocateInstance(Object1.class))).value);
    }
    @Test
    public void testUseUnsafe() throws Throwable {
        // TODO: 04.06.2018
    }

    @EqualsAndHashCode
    private class Object1 {
        private byte value;

        public Object1() {
            this.value = 1;
        }
    }
}