package ru.progrm_jarvis.reflector.bytecode.asm;

import lombok.NoArgsConstructor;
import lombok.val;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

@NoArgsConstructor
public class SafeClassDefiner implements ClassDefiner {

    private final Map<ClassLoader, GeneratedClassLoader> loaders = Collections.synchronizedMap(new WeakHashMap<>());

    @Override
    public Class<?> defineClass(ClassLoader parentLoader, String name, byte[] data) {
        GeneratedClassLoader loader = loaders.computeIfAbsent(parentLoader, GeneratedClassLoader::new);
        if (name != null) {
            synchronized (loader.getClassLoadingLock(name)) {
                if(loader.hasClass(name)) {
                    throw new IllegalStateException(String.format("%s already defined!", name));
                }
                return loader.define(name, data);
            }
        } else {
            return loader.define(null, data);
        }
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
        public Object getClassLoadingLock(String name) {
            return super.getClassLoadingLock(name);
        }

        public boolean hasClass(String name) {
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