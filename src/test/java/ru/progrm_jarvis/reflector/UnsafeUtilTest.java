package ru.progrm_jarvis.reflector;

import lombok.EqualsAndHashCode;
import org.junit.Test;

import static ru.progrm_jarvis.reflector.UnsafeUtil.*;

import static org.junit.Assert.*;

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