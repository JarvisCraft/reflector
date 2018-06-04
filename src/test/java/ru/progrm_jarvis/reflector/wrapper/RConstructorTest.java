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