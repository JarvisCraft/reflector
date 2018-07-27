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

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.progrm_jarvis.reflector.AccessHelper.operate;

class AccessHelperTest {

    @Test
    @SuppressWarnings("ConstantConditions")
    void testOperateField() throws Throwable {
        val object = new Object1();
        operate(object.getClass().getDeclaredField("field"), field -> field.set(object, null));
        assertNull(object.field);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testOperateMethod() throws Throwable {
        val object = new Object1();
        operate(object.getClass().getDeclaredMethod("foo"), method -> method.invoke(object));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testOperateConstructor() throws Throwable {
        operate(Object1.class.getDeclaredConstructor(), constructor -> constructor.newInstance());
    }

    private static class Object1 {
        private Object1() {}

        private final Object field = 0;

        private void foo() {}
    }
}