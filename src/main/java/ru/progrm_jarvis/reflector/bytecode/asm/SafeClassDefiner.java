package ru.progrm_jarvis.reflector.bytecode.asm;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

@NoArgsConstructor
public class SafeClassDefiner implements ClassDefiner {

    private final Map<ClassLoader, GeneratedClassLoader> loaders = Collections.synchronizedMap(new WeakHashMap<>());

    @Override
    public Class<?> defineClass(@NonNull final ClassLoader parentLoader, @Nullable final String name,
                                @NonNull final byte[] bytecode) {
        // get (creating new if none exists) GeneratedClassLoader associated with this parent classloader
        val loader = loaders.computeIfAbsent(parentLoader, GeneratedClassLoader::new);
        if (name != null) synchronized (loader.getClassLoadingLock(name)) {
            if (loader.hasClass(name)) throw new IllegalStateException("Class ".concat(name)
                    .concat(" is already defined!"));

            return loader.define(name, bytecode);
        }

        return loader.define(null, bytecode);
    }

    private static class GeneratedClassLoader extends ClassLoader {

        static {
            ClassLoader.registerAsParallelCapable();
        }

        protected GeneratedClassLoader(ClassLoader parent) {
            super(parent);
        }

        private Class<?> define(String name, byte[] data) {
            if (name != null) {
                synchronized (getClassLoadingLock(name)) {
                    assert !hasClass(name);
                    Class<?> c = defineClass(name, data, 0, data.length);
                    resolveClass(c);
                    return c;
                }
            } else {
                val clazz = defineClass(null, data, 0, data.length);
                resolveClass(clazz);

                return clazz;
            }
        }

        @Override
        public Object getClassLoadingLock(final String className) {
            return super.getClassLoadingLock(className);
        }

        public boolean hasClass(@NonNull final String name) {
            synchronized (getClassLoadingLock(name)) {
                try {
                    Class.forName(name);
                    return true;
                } catch (ClassNotFoundException e) {
                    return false;
                }
            }
        }
    }
}