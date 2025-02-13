package com.knsn92.ugorge.core_plugin;

import com.knsn92.ugorge.Ugorge;
import com.knsn92.ugorge.ugocraft.UgocraftInvoker;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

@SuppressWarnings("unused")
public class UgorgeTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if("net.minecraft.client.network.NetHandlerPlayClient".equals(transformedName)) {
            return clientTransform(basicClass);
        }else if("net.minecraft.network.NetHandlerPlayServer".equals(transformedName)) {
            return serverTransform(basicClass);
        }
        return basicClass;
    }

    private static byte[] clientTransform(byte[] basicClass) {

        ClassReader reader = new ClassReader(basicClass);
        ClassWriter writer = new ClassWriter(0);

        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM5, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor superVisitor = super.visitMethod(access, name, desc, signature, exceptions);

                if(!(("handleCustomPayload".equals(name) && "(Lnet/minecraft/network/play/server/S3FPacketCustomPayload;)V".equals(desc)) ||
                        ("a".equals(name) && "(Lgr;)V".equals(desc)))) return superVisitor;

                return new MethodVisitor(Opcodes.ASM5, superVisitor) {
                    @Override
                    public void visitLineNumber(int line, Label start) {
                        super.visitLineNumber(line, start);
                        if(line == (Ugorge.isDevEnv ? 1603 : 1416)) {
                            super.visitVarInsn(Opcodes.ALOAD, 0);
                            super.visitVarInsn(Opcodes.ALOAD, 1);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, UgocraftInvoker.internalName, "invoke_c001",
                                    Type.getMethodDescriptor(Type.VOID_TYPE,
                                            Type.getObjectType("net/minecraft/client/network/NetHandlerPlayClient"),
                                            Type.getObjectType("net/minecraft/network/play/server/S3FPacketCustomPayload")), false);
                        }
                    }
                };
            }
        };

        reader.accept(visitor, 0);
        return writer.toByteArray();
    }

    private static byte[] serverTransform(byte[] basicClass) {

        ClassReader reader = new ClassReader(basicClass);
        ClassWriter writer = new ClassWriter(0);

        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM5, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor superVisitor = super.visitMethod(access, name, desc, signature, exceptions);
                if(!(("processVanilla250Packet".equals(name) && "(Lnet/minecraft/network/play/client/C17PacketCustomPayload;)V".equals(desc)) ||
                        ("a".equals(name) && "(Liz;)V".equals(desc)))) return superVisitor;

                return new MethodVisitor(Opcodes.ASM5, superVisitor) {
                    @Override
                    public void visitLineNumber(int line, Label start) {
                        super.visitLineNumber(line, start);
                        if(line == (Ugorge.isDevEnv ? 1172 : 1072)) {
                            super.visitVarInsn(Opcodes.ALOAD, 0);
                            super.visitVarInsn(Opcodes.ALOAD, 1);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, UgocraftInvoker.internalName, "invoke_s001",
                                    Type.getMethodDescriptor(Type.VOID_TYPE,
                                            Type.getObjectType("net/minecraft/network/NetHandlerPlayServer"),
                                            Type.getObjectType("net/minecraft/network/play/client/C17PacketCustomPayload")), false);
                        }
                    }
                };
            }
        };

        reader.accept(visitor, 0);
        return writer.toByteArray();
    }
}
