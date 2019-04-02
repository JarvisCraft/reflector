package ru.progrm_jarvis.reflector.invoke;

import java.lang.invoke.MethodHandles.Lookup;
import lombok.*;
import lombok.experimental.Accessors;
import ru.progrm_jarvis.reflector.AccessHelper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import ru.progrm_jarvis.reflector.UnsafeUtil;

public class MethodHandleUtil {

    /**
     * Public lookup
     */
    private static final MethodHandles.Lookup PUBLIC_LOOKUP = MethodHandles.publicLookup(),
    /**
     * Full lookup
     */
            LOOKUP = tryGetTrustedLookup();

    public static <T, R> MethodHandleBuilder<T, R> create() {
        return new MethodHandleBuilder<>();
    }

    @SneakyThrows
    public static MethodHandle methodHandleFrom(@NonNull final Method method) {
        return AccessHelper.operateAndGet(method, LOOKUP::unreflect);
    }

    @SneakyThrows
    public static MethodHandle methodHandleFrom(@NonNull final Constructor constructor) {
        return AccessHelper.operateAndGet(constructor, LOOKUP::unreflectConstructor);
    }

    @SneakyThrows
    public static MethodHandle methodHandleFromGetter(@NonNull final Field field) {
        return AccessHelper.operateAndGet(field, LOOKUP::unreflectGetter);
    }

    @SneakyThrows
    public static MethodHandle methodHandleFromSetter(@NonNull final Field field) {
        return AccessHelper.operateAndGet(field, LOOKUP::unreflectSetter);
    }

    @SneakyThrows
    public static MethodHandle methodHandleFromSpecial(@NonNull final Method method,
                                                       @NonNull final Class<?> specialCaller) {
        return AccessHelper.operateAndGet(method, m -> LOOKUP.unreflectSpecial(m, specialCaller));
    }

    private static Lookup tryGetTrustedLookup() {
        // Just to initialize the class
        MethodHandles.publicLookup();
        try {
            Field implLookupField = Lookup.class.getDeclaredField("IMPL_LOOKUP");
            return (Lookup) UnsafeUtil.UNSAFE.getObject(UnsafeUtil.UNSAFE.staticFieldBase(implLookupField),
                UnsafeUtil.UNSAFE.staticFieldOffset(implLookupField));
        } catch (NoSuchFieldException ex) {
            throw new AssertionError("IMPL_LOOKUP is missing!", ex);
        }
    }

    /**
     * Builder based on {@link MethodHandle}
     *
     * @param <T> type of class whose methods are accessed
     * @param <R> type of returned value
     */
    @Data
    @Accessors(chain = true, fluent = true)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MethodHandleBuilder<T, R> {

        /**
         * Whether method-handle is of static method
         */
        private boolean isStatic;

        /**
         * Target class (reference) containing method
         */
        @NonNull protected Class<T> reference;

        /**
         * Name of a method
         */
        @NonNull private String name;

        /**
         * Lookup to find the method
         */
        @NonNull protected MethodHandles.Lookup lookup = LOOKUP;

        /**
         * Type of method
         */
        @NonNull private MethodType methodType;

        ///////////////////////////////////////////////////////////////////////////
        // Lookup
        ///////////////////////////////////////////////////////////////////////////

        /**
         * Set this builder's lookup to full full
         *
         * @return
         *
         * @see #LOOKUP
         */
        @SuppressWarnings("unchecked")
        public MethodHandleBuilder<T, R> fullLookup() {
            lookup = LOOKUP;

            return this;
        }

        @SuppressWarnings("unchecked")
        public MethodHandleBuilder<T, R> publicLoopkup() {
            lookup = PUBLIC_LOOKUP;

            return this;
        }

        @SuppressWarnings("unchecked")
        public MethodHandleBuilder<T, R> returnType(@NonNull final Class<R> returnType) {
            methodType = methodType == null
                    ? methodType = MethodType.methodType(returnType) : methodType.changeReturnType(returnType);

            return this;
        }

        @SuppressWarnings("unchecked")
        public <TNew> MethodHandleBuilder<TNew, R> changeReference(@NonNull final Class<TNew> reference) {
            return ((MethodHandleBuilder<TNew, R>) this).reference(reference);
        }

        @SuppressWarnings("unchecked")
        public <RNew> MethodHandleBuilder<T, RNew> changeReturnType(@NonNull final Class<RNew> returnType) {
            return ((MethodHandleBuilder<T, RNew>) this).returnType(returnType);
        }

        @SuppressWarnings("unchecked")
        public Class<R> returnType() {
            return methodType == null ? null : (Class<R>) methodType.returnType();
        }

        @SuppressWarnings("unchecked")
        public MethodHandleBuilder<T, R> parameterTypes(@NonNull final Class<?>[] parameterTypes) {
            methodType = MethodType
                    .methodType(methodType == null ? Object.class : methodType.returnType(), parameterTypes);

            return this;
        }

        public Class<?>[] parameterTypes() {
            return methodType == null ? new Class[0] : methodType.parameterArray();
        }

        protected void assertParameters() {
            Objects.requireNonNull(reference, "reference is null");
            Objects.requireNonNull(name, "name is null");
            Objects.requireNonNull(lookup, "lookup is null");
            Objects.requireNonNull(methodType, "methodType is null");
        }

        private MethodType getMethodType() {
            return Objects.requireNonNull(methodType, "methodType is null");
        }

        @SneakyThrows
        public MethodHandle build() {
            assertParameters();

            return isStatic
                    ? lookup.findStatic(reference, name, getMethodType())
                    : lookup.findVirtual(reference, name, getMethodType());
        }
    }
}
