package com.knsn92.ugorge.ugocraft.visitor;

import com.knsn92.ugorge.Ugorge;
import com.knsn92.ugorge.ugocraft.UgocraftClassData;
import cpw.mods.fml.common.asm.transformers.deobf.FMLRemappingAdapter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

/**
 * UgoCraftの殆どのクラスのMineCraftの難読化された名前の難読化解除と、
 * UgoCraft内で行われているリフレクションの参照するフィールドとメソッド名の置き換えを担当。
 */
public class DeobfuscationVisitor extends ClassVisitor implements Opcodes {

    private static final Map<String, String> ugoCraftReflectionStringReplaceMapDevEnv = new HashMap<>();
    private static final Map<String, String> ugoCraftReflectionStringReplaceMapRealEnv = new HashMap<>();

    static {
        ugoCraftReflectionStringReplaceMapDevEnv.put("net/maocat/Loader/Process/Server/Nyarlathotep.func_145826_a", "addMapping");
        ugoCraftReflectionStringReplaceMapDevEnv.put("net/maocat/Loader/Process/Client/Byakhee.field_147694_f",     "sndManager");
        ugoCraftReflectionStringReplaceMapDevEnv.put("net/maocat/Loader/Process/Client/Byakhee.field_148617_f",     "loaded");

        ugoCraftReflectionStringReplaceMapRealEnv.put("net/maocat/Loader/Process/Client/Yog_Sothoth.launchedVersion",   "field_110447_Z");
        ugoCraftReflectionStringReplaceMapRealEnv.put("net/maocat/Loader/Process/Server/Nyarlathotep.addMapping",       "func_75618_a");
        ugoCraftReflectionStringReplaceMapRealEnv.put("net/maocat/Loader/Process/Server/Nyarlathotep.addRecipe",        "func_92103_a");
        ugoCraftReflectionStringReplaceMapRealEnv.put("net/maocat/Loader/Process/Client/Deep_One.defaultResourcePacks", "field_110449_ao");
        ugoCraftReflectionStringReplaceMapRealEnv.put("net/maocat/Loader/Process/Client/Azathoth.instance",             "field_74817_a");
        ugoCraftReflectionStringReplaceMapRealEnv.put("net/maocat/Loader/Process/Client/Azathoth.languageList",         "field_74816_c");
        ugoCraftReflectionStringReplaceMapRealEnv.put("net/maocat/Loader/Umr_at_Tawil.currentWindowId",                 "field_71139_cq");
    }

    public DeobfuscationVisitor(int api, ClassVisitor cv) {
        super(api, new FMLRemappingAdapter(cv));
    }

    private String className;

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        String methodName = UgocraftClassData.findUgocraftImplSrcMethodName(className, name, desc);

        MethodVisitor superMethodVisitor = super.visitMethod(access, methodName, desc, signature, exceptions);
        return new DeobfuscationMethodVisitor(ASM5, superMethodVisitor, name);
    }


    private class DeobfuscationMethodVisitor extends MethodVisitor {

        private final String name;

        public DeobfuscationMethodVisitor(int api, MethodVisitor mv, String className) {
            super(api, mv);
            this.name = className;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {

            String methodName = UgocraftClassData.findUgocraftImplSrcMethodName(owner, name, desc);

            super.visitMethodInsn(opcode, owner, methodName, desc, itf);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {

            if(UgocraftClassData.hasClass(owner) && ArrayUtils.contains(UgocraftClassData.getFields(owner), name + StringUtils.SPACE + desc)) {
                super.visitFieldInsn(opcode, owner, name, desc);
                return;
            }

            String fieldName = UgocraftClassData.findUgocraftImplSrcFieldName(owner, name);

            super.visitFieldInsn(opcode, owner, fieldName, desc);
        }

        @Override
        public void visitLdcInsn(Object cst) {
            if(name.equals("<clinit>") && cst instanceof String) {
                String keyName = className + "." + cst;
                if (Ugorge.isDevEnv) {
                    if (ugoCraftReflectionStringReplaceMapDevEnv.containsKey(keyName)) {
                        cst = ugoCraftReflectionStringReplaceMapDevEnv.get(keyName);
                    }
                } else {
                    if (ugoCraftReflectionStringReplaceMapRealEnv.containsKey(keyName)) {
                        cst = ugoCraftReflectionStringReplaceMapRealEnv.get(keyName);
                    }
                }
            }
            super.visitLdcInsn(cst);
        }

    }
}
