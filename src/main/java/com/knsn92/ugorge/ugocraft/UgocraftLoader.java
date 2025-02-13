package com.knsn92.ugorge.ugocraft;

import com.knsn92.ugorge.util.ByteArrayClassLoader;
import com.knsn92.ugorge.util.MultiClassVisitor;
import com.knsn92.ugorge.util.MultiClassVisitorContext;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import com.knsn92.ugorge.ugocraft.visitor.*;

/**
 * UgoCraftのJarファイルの読み込みと処理書き換えを担当。UgoCraftのクラスはここから読み出す。
 */
public class UgocraftLoader {

    private static ByteArrayClassLoader ugocraftClassLoader = null;

    private static final MultiClassVisitorContext ugocraftClassVisitorContext;

    private static final Remapper rewriteListRemapper = new Remapper() {
        @Override
        public Object mapValue(Object value) {
            if(!(value instanceof String)) return value;
            String name = (String)value;
            if(ArrayUtils.contains(UgocraftClassData.rewriteListClasses, name)) {
                String mappedName = FMLDeobfuscatingRemapper.INSTANCE.map(name);
                return mappedName.substring(mappedName.lastIndexOf(File.separator)+1);
            }
            return value;
        }
    };

    static {
        ugocraftClassVisitorContext = new MultiClassVisitorContext();

        ugocraftClassVisitorContext.put("net/maocat/Loader/Boot/RewriteList", (api, cv) -> new RemappingClassAdapter(cv, rewriteListRemapper));
        ugocraftClassVisitorContext.put("net/maocat/Loader/Boot/ClassRewrite", ClassRewriteVisitor::new);
        ugocraftClassVisitorContext.put("net/maocat/Loader/Process/Shub_Niggurath", EntityRenderLoaderVisitor::new);
        ugocraftClassVisitorContext.put("net/maocat/UgoCraft/a/Azathoth", CannonGUISlotOffsetVisitor::new);

        ugocraftClassVisitorContext.setDefault(DeobfuscationVisitor::new);
    }

    /**
     * UgoCraftをJarファイルからロードし、書き換え、保存します。
     * @param ugocraftJarFile UgoCraftのjarファイル
     * @throws IOException UgoCraftのjarファイルの参照に失敗したとき
     */
    public static void load(File ugocraftJarFile) throws IOException {
        ugocraftClassLoader = new ByteArrayClassLoader(MinecraftServer.class.getClassLoader());

        JarInputStream jis = new JarInputStream(Files.newInputStream(ugocraftJarFile.toPath()));
        JarEntry entry;
        while((entry = jis.getNextJarEntry()) != null) {
            if(entry.getName().endsWith(".class")) {

                ClassReader cr = new ClassReader(jis);

                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

                MultiClassVisitor mcv = new MultiClassVisitor(Opcodes.ASM5, cw, ugocraftClassVisitorContext);

                cr.accept(mcv, ClassReader.EXPAND_FRAMES);

                byte[] classBytes = cw.toByteArray();

                String className = cr.getClassName();

                if(ArrayUtils.contains(UgocraftClassData.rewriteListClasses, className)) {
                    continue;
                }

                className = className.replace(File.separator, ".");
                ugocraftClassLoader.putClass(className, classBytes);
            }else {
                String resourceURLStr = "jar:file:" + ugocraftJarFile + "!" + File.separator + entry.getName();
                URL resourceURL = new URL(resourceURLStr);
                ugocraftClassLoader.putResource(entry.getName(), resourceURL);
            }
        }
    }

    /**
     * JarファイルからロードしたUgoCraftのクラスを読み出します。
     * @param className 読み出すクラス名(例:java.lang.Object)
     * @return 読み出したクラスオブジェクト。見つからなければnull
     */
    public static Class<?> getClass(String className) {
        if(ugocraftClassLoader == null) return null;
        try {
            return ugocraftClassLoader.loadClass(className);
        } catch (ClassNotFoundException ignore) {
            return null;
        }
    }

    /**
     * 内部で使われているクラスローダーのgetter。
     * @return 内部で使われているクラスローダー
     */
    public static ClassLoader getClassLoader() {
        return ugocraftClassLoader;
    }

    /**
     * 処理を変換したUgoCraftをJarとして書き出します。
     * @param ugocraftJarFile UgoCraftのjarファイル
     * @param output 出力のファイル
     * @throws IOException UgoCraftのjarファイルの参照に失敗したとき
     */
    public static void createJar(File ugocraftJarFile, File output) throws IOException {
        JarInputStream jis = new JarInputStream(Files.newInputStream(ugocraftJarFile.toPath()));
        JarOutputStream jos = new JarOutputStream(Files.newOutputStream(output.toPath()));

        JarEntry entry;
        while((entry = jis.getNextJarEntry()) != null) {
            if(entry.getName().endsWith(".class")) {

                ClassReader cr = new ClassReader(jis);

                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

                MultiClassVisitor mcv = new MultiClassVisitor(Opcodes.ASM5, cw, ugocraftClassVisitorContext);

                cr.accept(mcv, ClassReader.EXPAND_FRAMES);

                byte[] classBytes = cw.toByteArray();

                String className = entry.getName();

                if(entry.getName().startsWith("rewrite/")) {
                    String rewriteClassName = className.substring(className.lastIndexOf(File.separator)+1, className.indexOf("."));
                    String mcpName = FMLDeobfuscatingRemapper.INSTANCE.map(rewriteClassName);
                    className = "rewrite/"+mcpName.substring(mcpName.lastIndexOf(File.separator)+1)+".class";
                }

                jos.putNextEntry(new JarEntry(className));
                jos.write(classBytes);
            }else{
                jos.putNextEntry(new JarEntry(entry.getName()));
                byte[] buffer = new byte[1024];
                int read;
                while((read = jis.read(buffer)) != -1) {
                    jos.write(buffer, 0, read);
                }
            }
            jos.closeEntry();
        }
        jos.close();
    }
}
