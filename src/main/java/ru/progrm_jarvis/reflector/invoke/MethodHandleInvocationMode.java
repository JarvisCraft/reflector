package ru.progrm_jarvis.reflector.invoke;

import java.lang.invoke.MethodHandle;

public enum MethodHandleInvocationMode implements MethodHandleInvoker {
    /**
     * Mode in which method handle can use only exact argument types.
     * For example for ,Long(Integer) signature only
     */
    EXACT {
        @Override
        public Object apply(final MethodHandle methodHandle, final Object... arguments) throws Throwable {
            return methodHandle.invokeExact(arguments);
        }
    },
    /**
     * Mode in which method handle performs casts and unboxing but does not support array arguments
     */
    NORMAL {
        @Override
        public Object apply(final MethodHandle methodHandle, final Object... arguments) throws Throwable {
            return methodHandle.invoke(arguments);
        }
    },
    ARGUMENTS {
        @Override
        public Object apply(final MethodHandle methodHandle, final Object... arguments) throws Throwable {
            return methodHandle.invokeWithArguments(arguments);
        }
    }
}
