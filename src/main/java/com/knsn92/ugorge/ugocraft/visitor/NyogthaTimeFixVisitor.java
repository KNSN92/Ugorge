package com.knsn92.ugorge.ugocraft.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class NyogthaTimeFixVisitor extends ClassVisitor implements Opcodes {

    private String className;

    public NyogthaTimeFixVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        // We only modify methods in Nyogtha, which handles sliding block core logic
        if ("net/maocat/UgoCraft/Nyogtha".equals(this.className)) {
            return new MethodVisitor(api, mv) {
                @Override
                public void visitMethodInsn(int opcode, String owner, String methodName, String methodDescriptor, boolean isInterface) {
                    // Check if it's invoking getWorldTime() which is Notch mapped to J() returning long
                    if (opcode == INVOKEVIRTUAL && "ahb".equals(owner) && "J".equals(methodName) && "()J".equals(methodDescriptor)) {
                        // Replace getWorldTime (Time of Day, affected by doDaylightCycle)
                        // with getTotalWorldTime (Absolute Tick Time, never stops) mapping is I()
                        super.visitMethodInsn(INVOKEVIRTUAL, "ahb", "I", "()J", isInterface);
                    } else {
                        super.visitMethodInsn(opcode, owner, methodName, methodDescriptor, isInterface);
                    }
                }
            };
        }

        return mv;
    }
}
