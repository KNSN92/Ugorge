package com.knsn92.ugorge.ugocraft.visitor;

import com.knsn92.ugorge.util.ASMHelper;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * UgoCraftの大砲コアのGUIが一つズレているバグの修正をするVisitor。
 */
public class CannonGUISlotOffsetVisitor extends ClassVisitor implements Opcodes {

    public CannonGUISlotOffsetVisitor(int api, ClassVisitor cv) {
        super(api, new DeobfuscationVisitor(api, cv));
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        if(!ASMHelper.equalsNameDesc(name, desc, "b", "()V")) {
            return mv;
        }

        return new MethodVisitor(ASM5, mv) {

            private int line = 0;

            @Override
            public void visitLineNumber(int line, Label start) {
                super.visitLineNumber(line, start);
                this.line = line;
            }

            @Override
            public void visitInsn(int opcode) {
                super.visitInsn(opcode);
                if(this.line == 27 && opcode == IDIV) {
                    super.visitInsn(ICONST_1);
                    super.visitInsn(IADD);
                }
            }
        };
    }

}
