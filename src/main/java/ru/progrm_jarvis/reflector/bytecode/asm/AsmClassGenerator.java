package ru.progrm_jarvis.reflector.bytecode.asm;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

@AllArgsConstructor
public class AsmClassGenerator implements ClassGenerator {

    private static volatile int generatedEmptyConstructorAccessorCount = 0;

    @NonNull private ClassDefiner classDefiner;
    @NonNull private GeneratedClassNameSupplier classNameSupplier;
    @Nullable private ClassLoader classLoader;

    public AsmClassGenerator(final ClassDefiner classDefiner) {
        this(classDefiner, IncrementingNameIdGeneratedClassNameSupplier
                .from(AsmClassGenerator.class.getPackage().getName()
                        .concat(".$$generated$$.emptyconstructor.EmptyConstructorCreator$")), null);
    }

    @SneakyThrows
    public EmptyConstructorCreator newClassInstanceManipulator(@NonNull final Class<?> clazz) {
        val className = classNameSupplier.get();

        String dotClassName = className.replace("/", ".");
        String targetClassName = clazz.getName().replace(".", "/");
        String superClassName = "sun/reflect/MagicAccessorImpl";
        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;

        cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, superClassName,
                new String[]{
                        "ru/progrm_jarvis/reflector/bytecode/asm/EmptyConstructorCreator"
                });

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
        val methodVisitor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassName, "<init>", "()V", false);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();
    }
}
