package ru.progrm_jarvis.reflector;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;
import lombok.*;
import lombok.experimental.Delegate;

import java.util.Collection;

/**
 * Utilities for byte-code manipulations.
 * This is also a facade above {@link ClassPool} which delegates all its available methods
 */
@RequiredArgsConstructor(staticName = "create")
public class ByteCodeInjector {

    @Delegate
    @NonNull @Getter private final ClassPool classPool;

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
     * @param ctClass "compile-time" class to update
     * @param ctInterfaces "compile-time" classes which target class implements
     * @param ctImplementations "compile-time" classes which implement given interfaces
     * @return instance of "compile-time" class updated
     */
    @SneakyThrows
    public CtClass inject(@NonNull final CtClass ctClass,
                          @NonNull final CtClass[] ctInterfaces, @NonNull final CtClass... ctImplementations) {
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

        return ctClass;
    }

    /**
     * Implements all given interfaces in class specified using those classes which do also implement them.
     *
     * @param ctClass "compile-time" to update
     * @param ctInterfaces "compile-time" classes which target class implements
     * @param ctImplementations "compile-time" classes which implement given interfaces
     * @return instance of "compile-time" class updated
     */
    @SneakyThrows
    @Builder(builderMethodName = "injectionBuilder", buildMethodName = "inject", builderClassName = "InjectionBuilder")
    public CtClass inject(@NonNull final CtClass ctClass, @NonNull @Singular final Collection<CtClass> ctInterfaces,
                       @NonNull @Singular final Collection<CtClass> ctImplementations) {
        return inject(ctClass, ctInterfaces.toArray(new CtClass[0]), ctImplementations.toArray(new CtClass[0]));
    }

    /**
     * Implements all given interfaces in class specified using those classes which do also implement them.
     *
     * @param className name of class to update
     * @param interfaceNames names of classes which target class implements
     * @param implementationNames names of classes which implement given interfaces
     * @return instance of "compile-time" class updated
     */
    @SneakyThrows
    public CtClass inject(@NonNull final String className,
                          final String[] interfaceNames, final String... implementationNames) {
        return inject(classPool.get(className), classPool.get(interfaceNames), classPool.get(implementationNames));
    }

    /**
     * Implements all given interfaces in class specified using those classes which do also implement them.
     *
     * @param className name of class to update
     * @param interfaceNames names of classes which target class implements
     * @param implementationNames names of classes which implement given interfaces
     * @return instance of "compile-time" class updated
     */
    @SneakyThrows
    @Builder(builderMethodName = "injectionByNameBuilder", buildMethodName = "inject",
            builderClassName = "InjectionByNameBuilder")
    public CtClass inject(@NonNull final String className,
                          @NonNull @Singular final Collection<String> interfaceNames,
                          @NonNull @Singular final Collection<String> implementationNames) {
        return inject(className, interfaceNames.toArray(new String[0]), implementationNames.toArray(new String[0]));
    }
}
