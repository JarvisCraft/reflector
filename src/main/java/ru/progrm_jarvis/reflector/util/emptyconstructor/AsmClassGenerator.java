package ru.progrm_jarvis.reflector.util.emptyconstructor;

import org.objectweb.asm.*;

import java.util.Objects;

import static org.objectweb.asm.Opcodes.*;

public class AsmClassGenerator implements ClassGenerator {

    private static volatile int generatedEmptyConstructorAccessorCount = 0;

    private ClassDefiner classDefiner;
    private ClassLoader classLoader;

    public AsmClassGenerator(ClassDefiner classDefiner) {
        this(classDefiner, null);
    }

    public AsmClassGenerator(ClassDefiner classDefiner, ClassLoader classLoader) {
        Objects.requireNonNull(classDefiner, "Class definer is null");
        this.classDefiner = classDefiner;
        this.classLoader = classLoader;
    }

    @Override
    public EmptyConstructorCreator newClassInstanceManipulator(Class<?> clazz) {
        try {
            return newClassInstanceManipulator0(clazz);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private EmptyConstructorCreator newClassInstanceManipulator0(Class<?> clazz) throws Throwable {
        String className;
        synchronized (AsmClassGenerator.class) {
            className =
                    "ru/progrm_jarvis/reflector/util/emptyconstructor/generated/EmptyGeneratedConstructorAccessor"
                            + generatedEmptyConstructorAccessorCount++;
        }
        String dotClassName = className.replace("/", ".");
        String targetClassName = clazz.getName().replace(".", "/");
        String superClassName = "sun/reflect/MagicAccessorImpl";
        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;

        cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, superClassName,
                new String[]{
                        "ru/progrm_jarvis/reflector/util/emptyconstructor/EmptyConstructorCreator"});

        generateEmptyConstructor(cw, superClassName);
        {
            mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "()Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, targetClassName);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }

        cw.visitEnd();

        byte[] bytes = cw.toByteArray();
        Class<?> aClass = classDefiner
                .defineClass(classLoader == null ? getClass().getClassLoader() : classLoader,
                        dotClassName, bytes);
        return (EmptyConstructorCreator) aClass.newInstance();
    }

    private void generateEmptyConstructor(ClassWriter cw, String superClassName) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superClassName, "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

}
