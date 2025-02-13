package com.knsn92.ugorge.ugocraft.visitor;

import com.knsn92.ugorge.ugocraft.UgocraftHook;
import com.knsn92.ugorge.util.ASMHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.*;

/**
 * UgoCraftの諸々の初期化を行ったりする部分の、一部の処理を代わりに{@link UgocraftHook}の変数に置き換える処理を行う。具体的には以下のことを行う。
 * <ul>
 *     <li>UgoCraft本体のJarファイルの場所を取得する処理を置き換え</li>
 *     <li>MinecraftServerクラスの場所を取得する処理を置き換え</li>
 *     <li>自身のクラスローダーを取得する処理を置き換え</li>
 * </ul>
 */
public class EntityRenderLoaderVisitor extends ClassVisitor implements Opcodes {

    public EntityRenderLoaderVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        if (ASMHelper.equalsNameDesc(name, desc, "morning_glory", "(Ljava/lang/ClassLoader;Ljava/lang/String;)V")) {

            return new MethodVisitor(ASM5, mv) {

                private int line = 0;

                @Override
                public void visitLineNumber(int line, Label start) {
                    super.visitLineNumber(line, start);
                    this.line = line;
                }

                @Override
                public void visitVarInsn(int opcode, int var) {
                    if(this.line == 182 && opcode == ALOAD && var == 0) {
                        super.visitFieldInsn(GETSTATIC, UgocraftHook.internalName, "ugocraftClassLoader", "Ljava/lang/ClassLoader;");
                    }else {
                        super.visitVarInsn(opcode, var);
                    }
                }
            };
        }else if (ASMHelper.equalsNameDesc(name, desc, "morning_glory", "()V")) {

            return new MethodVisitor(ASM5, mv) {

                private int line = 0;

                @Override
                public void visitLineNumber(int line, Label start) {
                    super.visitLineNumber(line, start);
                    this.line = line;
                }

                @Override
                public void visitTypeInsn(int opcode, String type) {
                    if(!((line == 51 || line == 53) && opcode == NEW && type.equals("java/io/File"))) {
                        super.visitTypeInsn(opcode, type);
                    }
                }

                @Override
                public void visitInsn(int opcode) {
                    if(!((line == 51 || line == 53) && opcode == DUP)) {
                        super.visitInsn(opcode);
                    }
                }

                @Override
                public void visitLdcInsn(Object value) {
                    String ignoreLdcType;
                    if (line == 51) {
                        ignoreLdcType = "net/minecraft/server/MinecraftServer";
                    } else if (line == 53) {
                        ignoreLdcType = "net/maocat/Loader/Process/Shub_Niggurath";
                    }else{
                        ignoreLdcType = null;
                    }
                    if (value instanceof Type && ((Type) value).getInternalName().equals(ignoreLdcType)) {
                        return;
                    }
                    super.visitLdcInsn(value);
                }


                private final String[] IGNORE_METHODS = new String[] {
                        "java/lang/Class.getProtectionDomain()Ljava/security/ProtectionDomain;",
                        "java/security/ProtectionDomain.getCodeSource()Ljava/security/CodeSource;",
                        "java/security/CodeSource.getLocation()Ljava/net/URL;",
                        "java/net/URL.toURI()Ljava/net/URI;",
                };

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    if (line == 51 || line == 53) {
                        boolean isIgnoreMethod = ArrayUtils.contains(IGNORE_METHODS, ASMHelper.toFullMethodName(owner, name, desc));
                        if(isIgnoreMethod) {
                            return;
                        }
                        if ("java/io/File.<init>(Ljava/net/URI;)V".equals(ASMHelper.toFullMethodName(owner, name, desc))) {
                            String fieldName = line == 51 ? "minecraftServerLocation" : "ugocraftJarLocation";
                            super.visitFieldInsn(GETSTATIC, UgocraftHook.internalName, fieldName, "Ljava/io/File;");
                            return;
                        }
                    }
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                }
            };
        }else {
            return mv;
        }
    }

}
