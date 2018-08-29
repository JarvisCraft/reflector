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

import javassist.*;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ClassMemberMirrorer<T extends CtMember> {

    T mirror(@NotNull final CtClass targetClass) throws Throwable;

    static ClassMemberMirrorer<CtField> mirrorerOf(@NonNull final CtField sourceField) {
        return target -> {
            val newField = new CtField(sourceField, target);
            target.addField(newField);

            return newField;
        };
    }

    static ClassMemberMirrorer<CtField> mirrorerOf(@NonNull final CtField sourceField,
                                                   @Nullable final CtField.Initializer initializer) {
        if (initializer == null) return mirrorerOf(sourceField);
        return target -> {
            val newField = new CtField(sourceField, target);
            target.addField(newField, initializer);

            return newField;
        };
    }

    static ClassMemberMirrorer<CtField> mirrorerOf(@NonNull final CtField sourceField,
                                                   @Nullable final String initializer) {
        if (initializer == null || initializer.isEmpty()) return mirrorerOf(sourceField);
        return target -> {
            val newField = new CtField(sourceField, target);
            target.addField(newField, initializer);

            return newField;
        };
    }

    static ClassMemberMirrorer<CtMethod> mirrorerOf(@NonNull final CtMethod sourceMethod) {
        return target -> {
            val newMethod = CtNewMethod.copy(sourceMethod, target, null);
            target.addMethod(newMethod);

            return newMethod;
        };
    }
}
