package com.knsn92.ugorge.ugocraft.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ZothOmmogFixVisitor extends ClassVisitor implements Opcodes {

    private String className;

    public ZothOmmogFixVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

    @Override
    public void visitEnd() {
        if ("net/maocat/UgoCraft/Zoth_Ommog".equals(this.className)) {
            // Injecting missing Forge method:
            // public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default)
            // { return _default; }
            MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "isSideSolid", "(IIILnet/minecraftforge/common/util/ForgeDirection;Z)Z", null, null);
            if (mv != null) {
                mv.visitCode();
                mv.visitVarInsn(ILOAD, 5); // Load the '_default' boolean argument
                mv.visitInsn(IRETURN);     // Return it
                mv.visitMaxs(1, 6);
                mv.visitEnd();
            }
        }
        super.visitEnd();
    }
}
