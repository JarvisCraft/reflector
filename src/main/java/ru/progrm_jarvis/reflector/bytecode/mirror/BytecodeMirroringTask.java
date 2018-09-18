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

package ru.progrm_jarvis.reflector.bytecode.mirror;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import lombok.*;
import ru.progrm_jarvis.reflector.util.function.CheckedConsumer;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

import static ru.progrm_jarvis.reflector.bytecode.BytecodeUtil.OBJECT_CT_CLASS;

@Value
@Builder(builderClassName = "Builder")
public class BytecodeMirroringTask implements Runnable {

    @Nonnull private CtClass target;
    private boolean allowDefrost = false;

    @NonNull @Singular private Set<CtClass> delegators;
    @NonNull private MultipleSuperClassesPolicy multipleSuperClassesPolicy = MultipleSuperClassesPolicy.FAIL;

    @NonNull @Singular private Set<ClassMemberMirrorer<CtField>> fields;
    @NonNull @Singular private Set<ClassMemberMirrorer<CtMethod>> methods;
    @NonNull @Singular private Set<ClassMemberMirrorer<CtConstructor>> constructors;
    @NonNull @Singular private Set<ClassMemberMirrorer<CtConstructor>> initializers;

    @NonNull @Singular private List<CheckedConsumer<CtClass>> callbacks;
    private boolean failOnCallbackException = true;

    @Override
    @SneakyThrows
    public void run() {
        if(target.isFrozen()) if (allowDefrost) target.defrost();
        else throw new RuntimeException(target.getName().concat(" is frozen and defrosting is not allowed"));

        {
            var superClass = target.getSuperclass();
            for (val delegator : delegators) if (delegator.isInterface()) target.addInterface(delegator);
            else {
                if (superClass == null || superClass == OBJECT_CT_CLASS
                        || multipleSuperClassesPolicy.assertCanUseLast(target, superClass, delegator)) {
                    target.setSuperclass(delegator);
                    superClass = delegator;
                }
            }

            // super class's constructor should be called sometimes
            //System.out.println("================");
            //System.out.println("Super: " + target.getSuperclass());
            //for (final CtConstructor ctConstructor : target.getDeclaredConstructors()) {// TODO: 14.09.2018 remove
            //    System.out.println("Decl. constr.: " + ctConstructor);
            //    System.out.println("Calls super: " + ctConstructor.callsSuper());
            //}
            //System.out.println("=====");
            //for (val ctConstructor : superClass.getDeclaredConstructors()) {
            //    System.out.println("Constr. of super: " + ctConstructor);
            //    System.out.println("Calls super: " + ctConstructor.callsSuper());
            //    target.addConstructor(CtNewConstructor.copy(ctConstructor, target, null));
            //}
        }

        for (val method : methods) method.mirror(target);
        for (val field : fields) field.mirror(target);
        for (val constructor : constructors) constructor.mirror(target);
        for (val initializer : initializers) initializer.mirror(target);

        for (val callback : callbacks) try {
            callback.consume(target);
        } catch (final Throwable throwable) {
            if (failOnCallbackException) throw throwable;
        }
    }

    public enum MultipleSuperClassesPolicy {
        FAIL {
            @Override
            public boolean assertCanUseLast(@NonNull final CtClass target, @NonNull final CtClass superClass,
                                            @NonNull final CtClass newSuperClass) {
                throw new IllegalArgumentException("Target ".concat(target.getName())
                        .concat(" already has a superclass ").concat(superClass.getName()));
            }
        },
        USE_FIRST {
            @Override
            public boolean assertCanUseLast(@NonNull final CtClass target, @NonNull final CtClass superClass,
                                            @NonNull final CtClass newSuperClass) {
                return false;
            }
        },
        USE_LAST {
            @Override
            public boolean assertCanUseLast(@NonNull final CtClass target, @NonNull final CtClass superClass,
                                            @NonNull final CtClass newSuperClass) {
                return true;
            }
        };

        public abstract boolean assertCanUseLast(CtClass target, CtClass superClass, CtClass newSuperClass);
    }
}
