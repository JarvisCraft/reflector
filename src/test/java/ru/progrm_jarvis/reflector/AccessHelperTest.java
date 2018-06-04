package ru.progrm_jarvis.reflector;

import lombok.val;
import org.junit.Test;

import static ru.progrm_jarvis.reflector.AccessHelper.*;

import static org.junit.Assert.*;

public class AccessHelperTest {

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testOperateField() throws Throwable {
        val object = new Object1();
        operate(object.getClass().getDeclaredField("field"), field -> field.set(object, null));
        assertNull(object.field);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testOperateMethod() throws Throwable {
        val object = new Object1();
        operate(object.getClass().getDeclaredMethod("foo"), method -> method.invoke(object));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testOperateConstructor() throws Throwable {
        operate(Object1.class.getDeclaredConstructor(), constructor -> constructor.newInstance());
    }

    private static class Object1 {
        private Object1() {}

        private final Object field = 0;

        private void foo() {}
    }
}