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

package ru.progrm_jarvis.reflector.wrapper.fast;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FastMethodWrapperTest {

    @Test
    @SuppressWarnings("unchecked")
    void testInvoke() throws NoSuchMethodException {
        val fooFooMethod = FastMethodWrapper.<Foo, Integer>from(Foo.class.getDeclaredMethod("foo"));

        assertEquals(1, fooFooMethod.invoke(new Foo()).intValue());

    }

    private static class Foo {
        private int foo() {
            return 1;
        }
    }
}