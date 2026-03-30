package com.knsn92.ugorge.ugocraft;

import com.knsn92.ugorge.util.ByteArrayClassLoader;
import com.knsn92.ugorge.util.MultiClassVisitor;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import com.knsn92.ugorge.ugocraft.visitor.*;
import org.objectweb.asm.tree.ClassNode;

/**
 * UgoCraftのJarファイルの読み込みと処理書き換えを担当。UgoCraftのクラスはここから読み出す。
 */
public class UgocraftLoader {

    private final ByteArrayClassLoader classLoader = new ByteArrayClassLoader(MinecraftServer.class.getClassLoader());
    private final UgocraftClassData classData = new UgocraftClassData();

    private final MultiClassVisitor.Context ugocraftClassVisitorContext;

    {
        this.ugocraftClassVisitorContext = new MultiClassVisitor.Context();

        this.ugocraftClassVisitorContext.put("net/maocat/Loader/Process/Shub_Niggurath", EntityRenderLoaderVisitor::new);
        this.ugocraftClassVisitorContext.put("net/maocat/Loader/Process/Client/Byakhee", WaitUntilSoundMgrLoadFixVisitor::new);
        this.ugocraftClassVisitorContext.put("net/maocat/UgoCraft/a/Azathoth", (api, cv) -> new CannonGUISlotOffsetFixVisitor(api, cv, this.classData));

        this.ugocraftClassVisitorContext.setDefault((api, cv) -> new DeobfuscationVisitor(api, cv, this.classData));
    }

    /**
     * UgoCraftをJarファイルからロードし、書き換え、保存します。
     * @param ugocraftJarFile UgoCraftのjarファイル
     * @throws IOException UgoCraftのjarファイルの参照に失敗したとき
     */
    public UgocraftLoader(File ugocraftJarFile) throws IOException {
        Map<String, ClassReader> loadedClassReaders = new HashMap<>();
        try(JarInputStream jis = new JarInputStream(Files.newInputStream(ugocraftJarFile.toPath()))) {
            JarEntry entry;
            while((entry = jis.getNextJarEntry()) != null) {
                String entryName = entry.getName();
                if(entryName.endsWith(".class")) {
                    if(entryName.startsWith("rewrite/")) {
                        continue;
                    }
                    ClassReader cr = new ClassReader(jis);
                    String internalClassName = cr.getClassName();
                    loadedClassReaders.put(internalClassName, cr);
                }else {
                    String resourceURLStr = "jar:file:" + ugocraftJarFile + "!/" + entry.getName();
                    URL resourceURL = new URL(resourceURLStr);
                    this.classLoader.putResource(entry.getName(), resourceURL);
                }
            }
        }
        // この後のロード時にClassDataをめちゃ活用するから、先にClassDataに全て保存しないとダメだよ
        for(Map.Entry<String, ClassReader> classReaderEntry: loadedClassReaders.entrySet()) {
            String internalClassName = classReaderEntry.getKey();
            ClassReader cr = classReaderEntry.getValue();

            ClassNode cn = new ClassNode();
            cr.accept(cn, ClassReader.EXPAND_FRAMES);

            this.classData.putData(
                    internalClassName,
                    cr.getSuperName(),
                    cr.getInterfaces(),
                    cn.fields.stream().map(f -> f.name + StringUtils.SPACE + f.desc).toArray(String[]::new),
                    cn.methods.stream().map(m -> m.name + m.desc).toArray(String[]::new)
            );
        }
        for(Map.Entry<String, ClassReader> classReaderEntry: loadedClassReaders.entrySet()) {
            String internalClassName = classReaderEntry.getKey();
            ClassReader cr = classReaderEntry.getValue();

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            MultiClassVisitor mcv = new MultiClassVisitor(Opcodes.ASM5, cw, this.ugocraftClassVisitorContext);
            cr.accept(mcv, ClassReader.EXPAND_FRAMES);

            byte[] classBytes = cw.toByteArray();
            String className = internalClassName.replace("/", ".");
            this.classLoader.putClass(className, classBytes);
        }
    }

    /**
     * クラスローダーのgetter。
     * @return クラスローダー
     */
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    /**
     * UgoCraftのクラスデータのgetter。
     * @return UgoCraftのクラスデータ
     */
    public UgocraftClassData getClassData() {
        return this.classData;
    }

    /**
     * 処理を変換したUgoCraftをJarとして書き出します。
     * @param ugocraftJarFile UgoCraftのjarファイル
     * @param output 出力のファイル
     * @throws IOException UgoCraftのjarファイルの参照に失敗したとき
     */
    public void createJar(File ugocraftJarFile, File output) throws IOException {

        try(
            JarInputStream jis = new JarInputStream(Files.newInputStream(ugocraftJarFile.toPath()));
            JarOutputStream jos = new JarOutputStream(Files.newOutputStream(output.toPath()));
        ) {
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                String entryName = entry.getName();
                if (entry.getName().endsWith(".class")) {
                    if(entryName.startsWith("rewrite")) {
                        continue;
                    }
                    ClassReader cr = new ClassReader(jis);

                    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                    MultiClassVisitor mcv = new MultiClassVisitor(Opcodes.ASM5, cw, this.ugocraftClassVisitorContext);
                    cr.accept(mcv, ClassReader.EXPAND_FRAMES);
                    byte[] classBytes = cw.toByteArray();

                    jos.putNextEntry(new JarEntry(entryName));
                    jos.write(classBytes);
                } else {
                    jos.putNextEntry(new JarEntry(entryName));
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = jis.read(buffer)) != -1) {
                        jos.write(buffer, 0, read);
                    }
                }
                jos.closeEntry();
            }
        }
    }
}
