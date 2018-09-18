package ru.progrm_jarvis.reflector.bytecode;

import javassist.*;
import javassist.compiler.ast.ASTree;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import ru.progrm_jarvis.reflector.Reflector;
import ru.progrm_jarvis.reflector.util.ThrowableUtil;
import ru.progrm_jarvis.reflector.wrapper.FieldWrapper;
import ru.progrm_jarvis.reflector.wrapper.MethodWrapper;
import ru.progrm_jarvis.reflector.wrapper.fast.FastFieldWrapper;
import ru.progrm_jarvis.reflector.wrapper.fast.FastMethodWrapper;

import java.util.*;

@UtilityClass
public class BytecodeUtil {

    public static final ClassPool DEFAULT_CLASS_POOL = ClassPool.getDefault();

    public static final CtClass OBJECT_CT_CLASS = ThrowableUtil
            .executeChecked(() -> DEFAULT_CLASS_POOL.get(Object.class.getTypeName()));

    private final Class<Object> CT_CLASS_TYPE_CLASS = Reflector.classForName("javassist.CtClassType");
    private final Class<?> CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_CLASS
            = Reflector.classForName("javassist.FieldInitLink");

    private final MethodWrapper<CtField, CtField.Initializer> CT_FIELD_GET_INIT_METHOD
            = FastMethodWrapper.from(ThrowableUtil.executeChecked(() -> CtField.class.getDeclaredMethod("getInit")));
    private final MethodWrapper<CtField, ASTree> CT_FIELD_GET_INIT_AST_METHOD
            = FastMethodWrapper.from(ThrowableUtil.executeChecked(() -> CtField.class.getDeclaredMethod("getInitAST")));

    private final FieldWrapper<Object, Object> CT_CLASS_TYPE_FIELD_INITIALIZERS_FIELD
            = FastFieldWrapper.from(ThrowableUtil.executeChecked(
            () -> CT_CLASS_TYPE_CLASS.getDeclaredField("fieldInitializers")));

    private final FieldWrapper<Object, Object> CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_NEXT_FIELD
            = FastFieldWrapper.from(ThrowableUtil.executeChecked(
            () -> CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_CLASS.getDeclaredField("next")));
    private final FieldWrapper<Object, CtField> CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_FIELD_FIELD
            = FastFieldWrapper.from(ThrowableUtil.executeChecked(
            () -> CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_CLASS.getDeclaredField("field")));
    private final FieldWrapper<Object, CtField.Initializer> CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_INIT_FIELD
            = FastFieldWrapper.from(ThrowableUtil.executeChecked(
            () -> CT_CLASS_TYPE_CLASS_FIELD_INIT_LINK_CLASS.getDeclaredField("init")));

    public CtField.Initializer getInit(@NonNull final CtField ctField) {
        return CT_FIELD_GET_INIT_METHOD.invoke(ctField);
    }

    public ASTree getInitAst(@NonNull final CtField ctField) {
        return CT_FIELD_GET_INIT_AST_METHOD.invoke(ctField);
    }

    /**
     * Recursively finds all parents of class.
     *
     * @param ctClass class whose parents to find
     * @param includeSelf include class itself to set
     * @return all parents of class
     */
    @SneakyThrows
    public Set<CtClass> getParents(@NonNull final CtClass ctClass, final boolean includeSelf) {
        val parents = new HashSet<CtClass>();

        if (includeSelf) parents.add(ctClass);

        if (!ctClass.isInterface()) {
            val superClass = ctClass.getSuperclass();
            if (superClass != null && superClass != OBJECT_CT_CLASS) parents.addAll(getParents(superClass, true));
        }

        for (val anInterface : ctClass.getInterfaces()) parents.addAll(getParents(anInterface, true));

        return parents;
    }

    /**
     * Recursively finds all parents of class including itself.
     *
     * @param ctClass class whose parents to find
     * @return all parents of class including itself
     */
    @SneakyThrows
    public Set<CtClass> getParents(@NonNull final CtClass ctClass) {
        return getParents(ctClass, true);
    }

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
            var fieldInitializers = CT_CLASS_TYPE_FIELD_INITIALIZERS_FIELD.getValue(ctClass);
            if (fieldInitializers == null) return null;

            do {
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

    @SneakyThrows
    public void copyMethod(@NonNull final CtMethod ctMethod, @NonNull final CtClass ctClass) {
        ctClass.addMethod(CtNewMethod.copy(ctMethod, ctClass, null));
    }

    @SneakyThrows
    public void copyConstructor(@NonNull final CtConstructor ctConstructor, @NonNull final CtClass ctClass) {
        ctClass.addConstructor(CtNewConstructor.copy(ctConstructor, ctClass, null));
    }

    /**
     * Checks whether the type is a child-type of another one
     *
     * @param child possible child class
     * @param parent possible parent class
     * @return {@code true} if child's superclass or one of superinterfaces is parent
     */
    @SneakyThrows
    public boolean isChild(@NonNull final CtClass child, @NonNull final CtClass parent) {
        if (child == parent) return true;
        if (parent.isInterface()) {
            for (val anInterface : parent.getInterfaces()) if (isChild(anInterface, parent)) return true;

            return false;
        }

        return child.getSuperclass() == parent;
    }

    @SneakyThrows
    public boolean isChildSignature(@NonNull final CtMethod method, @NonNull final CtMethod superMethod) {
        if (method.getName().equals(superMethod.getName())
                && isChild(method.getReturnType(), superMethod.getReturnType())) {
            { // there should be no declared arguments in method not related to super
                val methodParameterTypes = method.getParameterTypes();
                val superParameterTypes = superMethod.getParameterTypes();

                // at least length of arguments should be equal
                if (methodParameterTypes.length == superParameterTypes.length) {
                    for (var i = 0; i < superParameterTypes.length; i++) if (methodParameterTypes[i]
                            != superParameterTypes[i]) return false;

                } else return false;
            }

            { // there should be no declared exceptions in method not related to super
                val superExceptionTypes = superMethod.getExceptionTypes();
                exceptionTypeChecking:
                for (val exceptionType : method.getExceptionTypes()) {
                    // if none of super exception types are related to the one declared then return FALSE
                    for (val superExceptionType : superExceptionTypes) if (
                            isChild(exceptionType, superExceptionType)) continue exceptionTypeChecking;

                    return false;
                }
            }

            return true;
        }

        return false;
    }

    @SneakyThrows
    public boolean isImplementationMethod(@NonNull final CtMethod childMethod, @NonNull final CtClass parent) {
        val declaringClass = childMethod.getDeclaringClass();
        if (declaringClass == parent) return true; // if the method is declared in parent itself then return TRUE

        for (val parentMethod : parent.getDeclaredMethods()) if (isChildSignature(
                childMethod, parentMethod)) return true;

        return false;
    }
}
