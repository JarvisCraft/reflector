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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.progrm_jarvis.reflector.wrapper.RConstructor.of;

public class RConstructorTest {

    @Test
    public void testOf() throws NoSuchMethodException {
        assertNotNull(of(PrivateStaticClass.class.getDeclaredConstructor()));
        assertNotNull(of(PrivateStaticClass.class.getDeclaredConstructor(int.class)));
        assertNotNull(of(PrivateStaticClass.class.getDeclaredConstructor(boolean.class)));
        assertNotNull(of(PrivateStaticClass.class.getDeclaredConstructor(String.class)));
    }

    @Test
    public void testConstruct() throws NoSuchMethodException {
        assertEquals(1, of(PrivateStaticClass.class.getDeclaredConstructor()).construct().value);

        {
            val constructor = of(PrivateStaticClass.class.getDeclaredConstructor(int.class));
            assertEquals(2, constructor.construct(2).value);
            assertThrows(IllegalArgumentException.class, constructor::construct);
            assertThrows(IllegalArgumentException.class, () -> constructor.construct(1, 2));
            assertThrows(IllegalArgumentException.class, () -> constructor.construct((Object) null));
            assertThrows(NullPointerException.class, () -> constructor.construct((Object[]) null));
        }

        {
            val constructor = of(PrivateStaticClass.class.getDeclaredConstructor(boolean.class));
            assertThrows(InvocationTargetException.class, () -> constructor.construct(true));
            assertThrows(InvocationTargetException.class, () -> constructor.construct(false));
            assertThrows(IllegalArgumentException.class, constructor::construct);
            assertThrows(IllegalArgumentException.class, () -> constructor.construct(1));
            assertThrows(IllegalArgumentException.class, () -> constructor.construct((Object) null));
            assertThrows(NullPointerException.class, () -> constructor.construct((Object[]) null));
        }

        {
            val constructor = of(PrivateStaticClass.class.getDeclaredConstructor(String.class));
            assertThrows(InvocationTargetException.class, () -> constructor.construct("hello"));
            assertThrows(InvocationTargetException.class, () -> constructor.construct((Object) null));
            assertThrows(IllegalArgumentException.class, constructor::construct);
            assertThrows(IllegalArgumentException.class, () -> constructor.construct(1));
            assertThrows(NullPointerException.class, () -> constructor.construct((Object[]) null));
        }
    }

    private static final class PrivateStaticClass {

        private int value;

        private PrivateStaticClass() {
            value = 1;
        }

        private PrivateStaticClass(final int value) {
            this.value = value;
        }

        private PrivateStaticClass(final boolean bool) throws SomeException {
            throw new SomeException();
        }

        private PrivateStaticClass(final String str) {
            throw new SomeRuntimeException();
        }

        private static final class SomeException extends Exception {}

        private static final class SomeRuntimeException extends RuntimeException {}
    }
}