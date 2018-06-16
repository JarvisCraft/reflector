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

import org.junit.Test;

import static ru.progrm_jarvis.reflector.wrapper.RConstructor.*;

import static org.junit.Assert.*;

public class RConstructorTest {

    @Test
    public void testConstruct() throws Throwable {
        assertEquals("", of(String.class.getDeclaredConstructor()).construct());

        assertEquals("Hello world", of(String.class.getDeclaredConstructor(byte[].class))
                .construct((Object) "Hello world".getBytes())
        );
    }
}