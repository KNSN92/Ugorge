package com.knsn92.ugorge.ugocraft.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;

public class ClassRewriteVisitor extends ClassVisitor implements Opcodes {

    public ClassRewriteVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if(!name.equals("transform")) return mv;
        mv.visitCode();

        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitLdcInsn(File.separator);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "lastIndexOf", "(Ljava/lang/String;)I", false);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(IADD);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "substring", "(I)Ljava/lang/String;", false);
        mv.visitVarInsn(ASTORE,  2);

        mv.visitEnd();
        return mv;
    }

}
