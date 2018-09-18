package ru.progrm_jarvis.reflector;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import lombok.NonNull;

public final class TestUtil {

    private TestUtil() {
        throw new RuntimeException();
    }

    public static ClassPool CLASS_POOL = ClassPool.getDefault();

    public static CtClass getCtClass(@NonNull final String className) throws NotFoundException {
        return CLASS_POOL.get(className);
    }

    public static CtClass getCtClass(@NonNull final Class<?> clazz) throws NotFoundException {
        return getCtClass(clazz.getTypeName());
    }

    public static CtClass makeCtClass(@NonNull final String className) {
        return CLASS_POOL.makeClass(className);
    }
}
