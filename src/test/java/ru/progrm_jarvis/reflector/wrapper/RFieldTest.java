package ru.progrm_jarvis.reflector.wrapper;

import lombok.val;
import org.junit.Test;

import java.nio.charset.Charset;
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