package com.knsn92.ugorge.util;

import com.knsn92.ugorge.Ugorge;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Minecraftの難読化解除マップを読み込み、参照できるようにするヘルパークラス。
 * 特にFMLDeobfuscatingRemapperが不可解な動作をする難読化解除されたフィールド名とメソッド名の取得を行うことが出来る。
 */
public class MCDeobfuscationHelper {

    private static Map<String, Map<String,String>> rawFieldMaps;
    private static Map<String,Map<String,String>> rawMethodMaps;

    static {
        if (Ugorge.isDevEnv) {
            try {
                loadRawMapsFromFile(new File(System.getProperty("user.home") + "/.gradle/caches/minecraft/net/minecraftforge/forge/1.7.10-10.13.4.1614-1.7.10/srgs/mcp-notch.srg"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            loadRawMapsFromFML();
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadRawMapsFromFML() {
        Class<FMLDeobfuscatingRemapper> fmlDeobfuscatingRemapperClass
                = FMLDeobfuscatingRemapper.class;
        try {
            Field fieldMaps = fmlDeobfuscatingRemapperClass.getDeclaredField("rawFieldMaps");
            fieldMaps.setAccessible(true);
            rawFieldMaps = (Map<String, Map<String, String>>) fieldMaps.get(FMLDeobfuscatingRemapper.INSTANCE);

            Field methodMaps = fmlDeobfuscatingRemapperClass.getDeclaredField("rawMethodMaps");
            methodMaps.setAccessible(true);
            rawMethodMaps = (Map<String, Map<String, String>>) methodMaps.get(FMLDeobfuscatingRemapper.INSTANCE);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadRawMapsFromFile(File file) throws IOException {
        rawFieldMaps = new HashMap<>();
        rawMethodMaps = new HashMap<>();

        String[] lines = FileUtils.readLines(file).toArray(new String[0]);
        for (String line : lines) {

            String[] splitResult = line.split(" ");
            if(splitResult.length <= 1) continue;

            if(splitResult[0].equals("MD:")) {

                String owner = splitResult[3].substring(0, splitResult[3].lastIndexOf("/"));
                String name  = splitResult[3].substring(splitResult[3].lastIndexOf("/")+1);
                String desc  = splitResult[4];

                Map<String, String> methods = rawMethodMaps.get(owner);
                if(methods == null) methods = new HashMap<>();

                methods.put(name+desc, splitResult[1].substring(splitResult[1].lastIndexOf("/")+1));

                rawMethodMaps.put(owner, methods);

            }else if(splitResult[0].equals("FD:")) {

                String owner = splitResult[2].substring(0, splitResult[2].lastIndexOf("/"));
                String name  = splitResult[2].substring(splitResult[2].lastIndexOf("/")+1);

                Map<String, String> fields = rawFieldMaps.get(owner);
                if(fields == null) fields = new HashMap<>();

                fields.put(name+":null", splitResult[1].substring(splitResult[1].lastIndexOf("/")+1));

                rawFieldMaps.put(owner, fields);

            }
        }
    }

    /**
     * 難読化解除されたMinecraftのクラス名を取得。
     * @param internalObfClassName 難読化された内部クラス名(例:bao(net.minecraft.client.Minecraft))
     * @return 難読化解除されたクラス名。解除できなかったら入力をそのまま返す。
     */
    public static String map(String internalObfClassName) {
        return FMLDeobfuscatingRemapper.INSTANCE.map(internalObfClassName);
    }

    /**
     * 逆に難読化解除された名前から難読化されたMinecraftのクラス名を取得。
     * @param internalClassName 内部クラス名(例:java/lang/Object)
     * @return 難読化されたクラス名。解除できなかったら入力をそのまま返す。
     */
    public static String unmap(String internalClassName) {
        return FMLDeobfuscatingRemapper.INSTANCE.unmap(internalClassName);
    }

    /**
     * 難読化解除されたMinecraftのメソッド名を取得。見つからなければ入力のメソッド名をそのまま返す。
     * @param internalObfOwner 難読化された内部クラス名(例:bao(net.minecraft.client.Minecraft))
     * @param name 難読化されたメソッド名
     * @param desc 難読化されたメソッドのシグネチャ
     * @return 難読化解除されたメソッド名。解除できなかったら入力をそのまま返す。
     */
    public static String mapMethodName(String internalObfOwner, String name, String desc) {
        Map<String, String> methods = rawMethodMaps.get(internalObfOwner);
        if(methods == null) return name;
        return ObjectUtils.defaultIfNull(methods.get(name+desc), name);
    }

    /**
     * 難読化されたMinecraftのクラス名からそのクラスのメソッドの難読化解除された名前の一覧を取得。
     * @param internalObfOwner 難読化された内部クラス名(例:bao(net.minecraft.client.Minecraft))
     * @return キーを入力のクラスに実装されているメソッドの名前とシグネチャ、値がそのメソッドの難読化解除された名前　として格納されたMap。クラスが見つからなければnull。
     */
    public static Map<String, String> mapMethodNames(String internalObfOwner) {
        return rawMethodMaps.get(internalObfOwner);
    }

    /**
     * 難読化解除されたMinecraftのフィールド名を取得。見つからなければ入力のフィールド名をそのまま返す。
     * @param internalObfOwner 難読化された内部クラス名(例:bao(net.minecraft.client.Minecraft))
     * @param name　難読化されたフィールド名
     * @return 難読化解除されたフィールド名。解除できなかったら入力をそのまま返す。
     */
    public static String mapFieldName(String internalObfOwner, String name) {
        Map<String, String> fields = rawFieldMaps.get(internalObfOwner);
        if(fields == null) return name;
        return ObjectUtils.defaultIfNull(fields.get(name+":null"), name);
    }

    /**
     * 難読化されたMinecraftのクラス名からそのクラスのフィールドの難読化解除された名前の一覧を取得。
     * @param internalObfOwner 難読化された内部クラス名(例:bao(net.minecraft.client.Minecraft))
     * @return キーを入力のクラスに実装されているフィールドの名前と型、値がそのフィールドの難読化解除された名前　として格納されたMap。クラスが見つからなければnull。
     */
    public static Map<String, String> mapFieldNames(String internalObfOwner) {
        return rawFieldMaps.get(internalObfOwner);
    }
}
