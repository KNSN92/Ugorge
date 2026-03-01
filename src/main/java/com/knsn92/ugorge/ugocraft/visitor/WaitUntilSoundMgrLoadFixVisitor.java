/*
  Thanks to qiuxtao
  He gave me the way to fix this issue!!
 */

package com.knsn92.ugorge.ugocraft.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class WaitUntilSoundMgrLoadFixVisitor extends ClassVisitor implements Opcodes {

    public WaitUntilSoundMgrLoadFixVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor smv = super.visitMethod(access, name, desc, signature, exceptions);
        if("iris".equals(name) || "anemone".equals(name)) {
            return new MethodVisitor(ASM5) {

                @Override
                public void visitCode() {
                    smv.visitCode();
                }

                @Override
                public void visitMaxs(int maxStack, int maxLocals) {
                    smv.visitInsn(RETURN);
                    smv.visitMaxs(0, 0);
                }
            };
        }
        return smv;
    }
}
