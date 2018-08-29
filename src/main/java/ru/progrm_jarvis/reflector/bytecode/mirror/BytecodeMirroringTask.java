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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Consumer;

@Value
@Builder(builderClassName = "Builder")
public class BytecodeMirroringTask implements Runnable {

    @Nonnull private CtClass target;
    private boolean allowDefrost;

    @Singular @Nonnull private Set<CtClass> interfaces;
    @Singular @Nonnull private Set<ClassMemberMirrorer<CtField>> fields;
    @Singular @Nonnull private Set<ClassMemberMirrorer<CtMethod>> methods;
    @Singular @Nonnull private Set<ClassMemberMirrorer<CtConstructor>> constructors;

    @Nullable private Consumer<CtClass> callback;

    @Override
    @SneakyThrows
    public void run() {
        if(target.isFrozen()) if (allowDefrost) target.defrost();
        else throw new RuntimeException(target.getName().concat(" is frozen and defrosting is not allowed"));

        for (val anInterface : interfaces) target.addInterface(anInterface);

        for (val method : methods) method.mirror(target);
        for (val field : fields) field.mirror(target);
        for (val constructor : constructors) constructor.mirror(target);

        if (callback != null) callback.accept(target);
    }
}
