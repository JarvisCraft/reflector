package ru.progrm_jarvis.reflector;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import lombok.val;

/**
 * Utilities for byte-code manipulations.
 * This is also a facade above {@link ClassPool} which delegates all its available methods
 */
@RequiredArgsConstructor(staticName = "create")
public class ByteCodeInjector extends ClassPool {

    @Delegate
    @NonNull
    private final ClassPool classPool;

    /**
     * Creates new instance of byte-code injector using default {@link ClassPool}
     *
     * @return newly created instance of byte-code injector
     */
    public static ByteCodeInjector create() {
        return new ByteCodeInjector(ClassPool.getDefault());
    }

    /**
     * Implements all given interfaces in class specified using those classes which do also implement them.
     *
     * @param ctClass class to update
     * @param ctInterfaces classes which target class implements
     * @param ctImplementations classes which implement given interfaces
     */
    @SneakyThrows
    public void inject(@NonNull final CtClass ctClass,
                          @NonNull final CtClass[] ctInterfaces, @NonNull final CtClass[] ctImplementations) {
        // implement interfaces
        ctClass.setInterfaces(ctInterfaces);
        // copy methods
        // check each method in each implementation
        for (val ctImplementation : ctImplementations) for (val ctMethod : ctImplementation.getDeclaredMethods()) {
            methodCheck: // check whether method belongs to one of interfaces
            for (val implementedInterface : ctMethod.getDeclaringClass().getInterfaces()) for (val ctInterface
                    : ctInterfaces) if (implementedInterface == ctInterface) {
                    // add method copy setting declaring class to the one updated
                    ctClass.addMethod(CtNewMethod.copy(ctMethod, ctClass, null));
                    // don't continue check as the class declaring this method have already been found
                    break methodCheck;
                }
        }
    }

    /**
     * Implements all given interfaces in class specified using those classes which do also implement them.
     *
     * @param className name of class to update
     * @param interfaceNames names of classes which target class implements
     * @param implementationNames names of classes which implement given interfaces
     * @return instance of "compile-time" class
     */
    @SneakyThrows
    public CtClass inject(@NonNull final String className,
                          @NonNull final String[] interfaceNames, @NonNull final String... implementationNames) {
        val ctClass = classPool.get(className);

        // interfaces which this class should implement
        val ctInterfaces = classPool.get(interfaceNames);
        // classes which implement those interfaces
        val ctImplementations = classPool.get(implementationNames);

        inject(ctClass, ctInterfaces, ctImplementations);

        return ctClass;
    }
}
