/*
 *  Copyright 2018 Petr P.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.progrm_jarvis.reflector.bytecode;

import javassist.*;
import javassist.compiler.ast.ASTree;
import lombok.*;
import lombok.experimental.Delegate;
import ru.progrm_jarvis.reflector.Reflector;
import ru.progrm_jarvis.reflector.util.ThrowableUtil;
import ru.progrm_jarvis.reflector.wrapper.FieldWrapper;
import ru.progrm_jarvis.reflector.wrapper.MethodWrapper;
import ru.progrm_jarvis.reflector.wrapper.fast.FastFieldWrapper;
import ru.progrm_jarvis.reflector.wrapper.fast.FastMethodWrapper;

import java.util.Collection;

/**
 * Utilities for byte-code manipulations,
 * this is also a facade above {@link ClassPool} all whose methods are available
 */
@RequiredArgsConstructor(staticName = "create")
public class BytecodeHelper {

    @Delegate
    @NonNull @Getter private final ClassPool classPool;

    private static final Class<Object> CT_CLASS_TYPE_CLASS = Reflector.classForName("javassist.CtClassType");
    private static final Class<?> CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_CLASS
            = Reflector.classForName("javassist.FieldInitLink");

    private static final MethodWrapper<CtField, CtField.Initializer> CT_FIELD_GET_INIT_METHOD
            = FastMethodWrapper.from(ThrowableUtil.executeChecked(() -> CtField.class.getDeclaredMethod("getInit")));
    private static final MethodWrapper<CtField, ASTree> CT_FIELD_GET_INIT_AST_METHOD
            = FastMethodWrapper.from(ThrowableUtil.executeChecked(() -> CtField.class.getDeclaredMethod("getInitAST")));

    private static final FieldWrapper<Object, Object> CT_CLASS_TYPE_FIELD_INITIALIZERS_FIELD
            = FastFieldWrapper.from(ThrowableUtil.executeChecked(
                    () -> CT_CLASS_TYPE_CLASS.getDeclaredField("fieldInitializers")));

    private static final FieldWrapper<Object, Object> CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_NEXT_FIELD
            = FastFieldWrapper.from(ThrowableUtil.executeChecked(
                    () -> CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_CLASS.getDeclaredField("next")));
    private static final FieldWrapper<Object, CtField> CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_FIELD_FIELD
            = FastFieldWrapper.from(ThrowableUtil.executeChecked(
                    () -> CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_CLASS.getDeclaredField("field")));
    private static final FieldWrapper<Object, CtField.Initializer> CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_INIT_FIELD
            = FastFieldWrapper.from(ThrowableUtil.executeChecked(
                    () -> CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_CLASS.getDeclaredField("init")));

    public CtField.Initializer getInit(@NonNull final CtField ctField) {
        return CT_FIELD_GET_INIT_METHOD.invoke(ctField);
    }

    public ASTree getInitAst(@NonNull final CtField ctField) {
        return CT_FIELD_GET_INIT_AST_METHOD.invoke(ctField);
    }

    /**
     * Creates new instance of byte-code injector using default {@link ClassPool}.
     *
     * @return newly created instance of byte-code injector
     */
    public static BytecodeHelper create() {
        return new BytecodeHelper(ClassPool.getDefault());
    }

    @SneakyThrows
    public void copyMethod(@NonNull final CtMethod ctMethod, @NonNull final CtClass ctClass) {
        ctClass.addMethod(CtNewMethod.copy(ctMethod, ctClass, null));
    }

    @SneakyThrows
    public void copyConstructor(@NonNull final CtConstructor ctConstructor, @NonNull final CtClass ctClass) {
        ctClass.addConstructor(CtNewConstructor.copy(ctConstructor, ctClass, null));
    }

    /*
    @NotNull
    @SneakyThrows
    private CtFieldInitializer lookupFieldInitializer(@NonNull final CtField ctField) {
        // if the CtField object itself has an Initializer object then use it
        // otherwise perform a lookup of this init in CtClass parent of this field
        System.out.println("===");
        System.out.println(":::: " + ctField);
        {
            val initializer = getInit(ctField);
            System.out.println("Init: " + initializer);
            if (initializer != null) return new CtFieldInitializer(initializer, null);
        }

        val ctClass = ctField.getDeclaringClass();

        // lookup constructors
        for (val ctConstructor : ctClass.getConstructors()) {
            System.out.println(ctConstructor);
            System.out.println(ctConstructor.isEmpty());
            System.out.println(ctConstructor.isClassInitializer());
            System.out.println(ctConstructor.isConstructor());
        }
        // otherwise try using initializers field of CtClassType if possible to find one
        if (CT_CLASS_TYPE_CLASS.isAssignableFrom(ctClass.getClass())) {
            ctClass.addField(new CtField(ctClass, "myField" + Math.random(), ctClass), "null;");
            var fieldInitializers = CT_CLASS_TYPE_FIELD_INITIALIZERS_FIELD.getValue(ctClass);
            System.out.println("initializers: " + fieldInitializers);
            if (fieldInitializers == null) return CtFieldInitializer.EMPTY;

            do {
                System.out.println(CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_FIELD_FIELD.getValue(fieldInitializers));
                if (CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_FIELD_FIELD.getValue(fieldInitializers)
                        == ctField) return new CtFieldInitializer(
                                CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_INIT_FIELD.getValue(fieldInitializers), null
                );
            }
            while ((fieldInitializers = CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_NEXT_FIELD.getValue(fieldInitializers))
                    != null);
        }

        return CtFieldInitializer.EMPTY;
    }

    @SneakyThrows
    public void copyField(@NonNull final CtField ctField, @NonNull final CtClass ctClass) {
        val fieldInitializer = lookupFieldInitializer(ctField);

        if (fieldInitializer == CtFieldInitializer.EMPTY) ctClass.addField(new CtField(ctField, ctClass));
        else {
            if (fieldInitializer.initializer == null) ctClass.addField(new CtField(ctField, ctClass));
            else ctClass.addField(new CtField(ctField, ctClass), fieldInitializer.initializer);

            if (fieldInitializer.constructor != null) ctClass.addConstructor(constr);
        }
    }
    */

    @SneakyThrows
    private CtField.Initializer lookupFieldInitializer(@NonNull final CtField ctField) {
        // if the CtField object itself has an Initializer object then use it
        // otherwise perform a lookup of this init in CtClass parent of this field
        {
            val initializer = getInit(ctField);
            if (initializer != null) return initializer;
        }

        val ctClass = ctField.getDeclaringClass();

        // lookup for initializers field of CtClassType if possible to find one
        if (CT_CLASS_TYPE_CLASS.isAssignableFrom(ctClass.getClass())) {
            ctClass.addField(new CtField(ctClass, "myField" + Math.random(), ctClass), "null;");
            var fieldInitializers = CT_CLASS_TYPE_FIELD_INITIALIZERS_FIELD.getValue(ctClass);
            if (fieldInitializers == null) return null;

            do {
                System.out.println(CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_FIELD_FIELD.getValue(fieldInitializers));
                if (CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_FIELD_FIELD.getValue(fieldInitializers)
                        == ctField) return CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_INIT_FIELD.getValue(fieldInitializers);
            }
            while ((fieldInitializers = CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_NEXT_FIELD.getValue(fieldInitializers))
                    != null);
        }

        return null;
    }

    @SneakyThrows
    public void copyField(@NonNull final CtField ctField, @NonNull final CtClass ctClass) {
        val initializer = lookupFieldInitializer(ctField);

        if (initializer == null) ctClass.addField(new CtField(ctField, ctClass));
        else ctClass.addField(new CtField(ctField, ctClass), initializer);
    }
    /**
     * Implements all given interfaces in class specified using those classes which do also implement them.
     *
     * @param ctClass "compile-time" to update
     * @param copyFields whether to copy fields from implementations
     * @param ctInterfaces "compile-time" classes which target class implements
     * @param ctImplementations "compile-time" classes which implement given interfaces
     * @return instance of "compile-time" class updated
     */
    @SneakyThrows
    @Builder(builderMethodName = "injectionBuilder", buildMethodName = "inject", builderClassName = "InjectionBuilder")
    public CtClass inject(@NonNull final CtClass ctClass, final boolean copyFields,
                          @NonNull @Singular final Collection<CtClass> ctInterfaces,
                          @NonNull @Singular final Collection<CtClass> ctImplementations) {
        return inject(
                ctClass, copyFields, ctInterfaces.toArray(new CtClass[0]), ctImplementations.toArray(new CtClass[0])
        );
    }

    /**
     * Implements all given interfaces in class specified using those classes which do also implement them.
     *
     * @param className name of class to update
     * @param copyFields whether to copy fields from implementations
     * @param interfaceNames names of classes which target class implements
     * @param implementationNames names of classes which implement given interfaces
     * @return instance of "compile-time" class updated
     */
    @SneakyThrows
    public CtClass inject(@NonNull final String className, final boolean copyFields,
                          final String[] interfaceNames, final String... implementationNames) {
        return inject(
                classPool.get(className), copyFields, classPool.get(interfaceNames), classPool.get(implementationNames)
        );
    }

    /**
     * Implements all given interfaces in class specified using those classes which do also implement them.
     *
     * @param ctClass "compile-time" class to update
     * @param copyFields whether to copy fields from implementations
     * @param ctInterfaces "compile-time" classes which target class implements
     * @param ctImplementations "compile-time" classes which implement given interfaces
     * @return instance of "compile-time" class updated
     */
    @SneakyThrows
    public CtClass inject(@NonNull final CtClass ctClass, final boolean copyFields,
                          @NonNull final CtClass[] ctInterfaces, @NonNull final CtClass... ctImplementations) {
        // implement interfaces
        ctClass.setInterfaces(ctInterfaces);
        // copy methods
        // check each method in each implementation
        for (val ctImplementation : ctImplementations) {
            if (copyFields) for (val field : ctImplementation.getDeclaredFields()) copyField(field, ctClass);

            methodAdding:
            for (val ctMethod : ctImplementation.getDeclaredMethods()) {
                // check whether method belongs to one of interfaces
                System.out.println("Decl of " +ctMethod+" is "+ctMethod.getDeclaringClass().getName());
                val declaringClass = ctMethod.getDeclaringClass();
                CtClass[] implementedInterfaces = null; // lazily initialized

                for (val ctInterface : ctInterfaces) {
                    System.out.println("Checking: " + ctInterface.getName());
                    if (ctInterface == declaringClass) {
                        System.out.println("+");

                        // add method copy setting declaring class to the one updated
                        copyMethod(ctMethod, ctClass);

                        // don't continue check as the class declaring this method have already been found
                        continue methodAdding;
                    } else {
                        System.out.println("-");
                        if (implementedInterfaces == null) implementedInterfaces = ctMethod.getDeclaringClass()
                                .getInterfaces(); // lazy initialization of implementedInterfaces

                        for (val implementedInterface : implementedInterfaces) if (implementedInterface
                                == ctInterface) {
                            // add method copy setting declaring class to the one updated
                            copyMethod(ctMethod, ctClass);

                            // don't continue check as the class declaring this method have already been found
                            continue methodAdding;
                        }
                    }
                }
            }
        }

        return ctClass;
    }

    /**
     * Implements all given interfaces in class specified using those classes which do also implement them.
     *
     * @param className name of class to update
     * @param copyFields whether to copy fields from implementations
     * @param interfaceNames names of classes which target class implements
     * @param implementationNames names of classes which implement given interfaces
     * @return instance of "compile-time" class updated
     */
    @SneakyThrows
    @Builder(builderMethodName = "injectionByNameBuilder", buildMethodName = "inject",
            builderClassName = "InjectionByNameBuilder")
    public CtClass inject(@NonNull final String className, final boolean copyFields,
                          @NonNull @Singular final Collection<String> interfaceNames,
                          @NonNull @Singular final Collection<String> implementationNames) {
        return inject(
                className, copyFields, interfaceNames.toArray(new String[0]), implementationNames.toArray(new String[0])
        );
    }

    @Data
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class CtFieldInitializer {

        public static final CtFieldInitializer EMPTY = new CtFieldInitializer(null, null);

        @NonNull private CtField.Initializer initializer;
        @NonNull private CtConstructor constructor;
    }
}
