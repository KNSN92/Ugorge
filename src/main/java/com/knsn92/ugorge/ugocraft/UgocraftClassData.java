package com.knsn92.ugorge.ugocraft;

import com.knsn92.ugorge.util.MCDeobfuscationHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * UgoCraftの殆どのクラスの親クラスと実装しているインターフェース、フィールドとメソッドの一覧を保存します。
 */
public class UgocraftClassData {

    public static final String[] rewriteListClasses = {"bjb", "bnn", "blm", "nh"};

    private static Map<String, String>   superClasses = null;
    private static Map<String, String[]> implInterfaces = null;
    private static Map<String, String[]> fields = null;
    private static Map<String, String[]> methods = null;

    /**
     * UgoCraftのJarファイルから情報を読み込みます。
     * @param ugocraftJarFile 入力のjarファイル
     * @throws IOException jarファイルが読み取れなかった場合
     */
    public static void loadData(File ugocraftJarFile) throws IOException {
        superClasses = new HashMap<>();
        implInterfaces = new HashMap<>();
        fields = new HashMap<>();
        methods = new HashMap<>();

        JarInputStream jis = new JarInputStream(Files.newInputStream(ugocraftJarFile.toPath()));
        JarEntry entry;
        while((entry = jis.getNextJarEntry()) != null) {
            if(entry.getName().endsWith(".class")) {

                ClassReader cr = new ClassReader(jis);

                String className = cr.getClassName();

                if(ArrayUtils.contains(rewriteListClasses, className)) {
                    continue;
                }

                className = className.replace(File.separator, "/");

                String superName = cr.getSuperName();

                superClasses.put(className, superName);
                implInterfaces.put(className, cr.getInterfaces());
                ClassNode cn = new ClassNode();
                cr.accept(cn, ClassReader.EXPAND_FRAMES);
                fields.put(className, cn.fields.stream().map(f -> f.name + StringUtils.SPACE + f.desc).toArray(String[]::new));
                methods.put(className, cn.methods.stream().map(m -> m.name + m.desc).toArray(String[]::new));
            }
        }
    }

    /**
     * そのクラスがUgoCraftに含まれるかを判定します。
     * @param internalClassName 内部クラス名(例:java/lang/Object)
     * @return そのクラスがUgoCraftに含まれるか
     */
    public static boolean hasClass(String internalClassName) {
        return superClasses.containsKey(internalClassName);
    }

    /**
     * そのUgoCraftクラスのスーパークラス名を返します。
     * @param internalClassName 内部クラス名(例:java/lang/Object)
     * @return そのUgoCraftクラスのスーパークラス名
     */
    public static String getSuperClass(String internalClassName) {
        return superClasses.get(internalClassName);
    }

    /**
     * そのUgoCraftクラスの実装しているインターフェースの一覧を返します。
     * @param internalClassName 内部クラス名(例:java/lang/Object)
     * @return そのUgoCraftクラスの実装しているインターフェースの一覧
     */
    public static String[] getImplInterfaces(String internalClassName) {
        return implInterfaces.get(internalClassName);
    }

    /**
     * そのUgoCraftクラスがそのインターフェースを実装しているかを返します。
     * @param internalClassName 内部クラス名(例:java/lang/Object)
     * @param internalInterfaceClassName 内部インターフェースクラス名(例:java/lang/Object)
     * @return そのUgoCraftクラスがそのインターフェースを実装しているか
     */
    public static boolean hasImplInterface(String internalClassName, String internalInterfaceClassName) {
        return ArrayUtils.contains(getImplInterfaceNames(internalClassName), internalInterfaceClassName);
    }

    /**
     * そのUgoCraftクラスに存在するフィールドの一覧を返します。
     * @param internalClassName 内部クラス名(例:java/lang/Object)
     * @return そのUgoCraftクラスに存在するフィールドの一覧
     */
    public static String[] getFields(String internalClassName) {
        return fields.get(internalClassName);
    }

    /**
     * そのUgoCraftクラスがそのフィールドを持っているかを返します。
     * @param internalClassName 内部クラス名(例:java/lang/Object)
     * @param fieldName フィールド名
     * @return そのUgoCraftクラスがそのフィールドを持っているかを返します。
     */
    public static boolean hasField(String internalClassName, String fieldName) {
        return ArrayUtils.contains(getFields(internalClassName), fieldName);
    }

    /**
     * そのUgoCraftクラスに存在するメソッドの一覧を返します。
     * @param internalClassName 内部クラス名(例:java/lang/Object)
     * @return そのUgoCraftクラスに存在するメソッドの一覧
     */
    public static String[] getMethods(String internalClassName) {
        return methods.get(internalClassName);
    }

    /**
     * そのUgoCraftクラスがそのフィールドを持っているかを返します。
     * @param internalClassName 内部クラス名(例:java/lang/Object)
     * @param methodName メソッド名
     * @param methodDesc メソッドのシグネチャ
     * @return そのUgoCraftクラスがそのフィールドを持っているかを返します。
     */
    public static boolean hasMethod(String internalClassName, String methodName, String methodDesc) {
        return ArrayUtils.contains(getMethods(internalClassName), methodName + StringUtils.SPACE + methodDesc);
    }


    private static Class<?> getClassFromString(String internalClassName) {
        if(internalClassName == null) return null;
        try {
            return Class.forName(MCDeobfuscationHelper.map(internalClassName).replace("/", "."));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static String getSuperClassName(String internalClassName) {
        if(internalClassName == null) return null;

        if(UgocraftClassData.hasClass(internalClassName)) {
            String superClassName = UgocraftClassData.getSuperClass(internalClassName);
            return MCDeobfuscationHelper.map(superClassName);
        }else {
            Class<?> superClass = getClassFromString(internalClassName);
            if(superClass == null) return null;
            superClass = superClass.getSuperclass();
            if(superClass == null) return null;
            String superClassName = superClass.getName().replace(".", "/");
            return MCDeobfuscationHelper.map(superClassName);
        }
    }

    private static String[] getImplInterfaceNames(String internalClassName) {
        if(internalClassName == null) return null;

        String[] interfaces;
        if(UgocraftClassData.hasClass(internalClassName)) {
            interfaces = UgocraftClassData.getImplInterfaces(internalClassName);
        } else {
            Class<?> clazz = getClassFromString(internalClassName);
            interfaces = Arrays.stream(clazz.getInterfaces())
                    .map(c -> c.getName().replace(".", "/"))
                    .toArray(String[]::new);
        }

        for(int i = 0; i < interfaces.length; i++) {
            interfaces[i] = MCDeobfuscationHelper.map(interfaces[i]);
        }

        return interfaces;
    }

    /**
     * 難読化されたフィールド名とそのフィールドを持つクラス名からそのフィールドがどのクラスで実装された物かをそのフィールドを持つクラスの親を辿り、
     * そこから難読化解除されたフィールド名を返します。
     * @param owner　そのフィールドを持つクラス名
     * @param name 探す難読化されたフィールド名
     * @return 難読化解除されたフィールド名
     */
    public static String findUgocraftImplSrcFieldName(String owner, String name) {
        String currentClassName = MCDeobfuscationHelper.map(owner);

        while(currentClassName != null) {
            String fieldName = MCDeobfuscationHelper.mapFieldName(MCDeobfuscationHelper.unmap(currentClassName), name);
            if(!Objects.equals(name, fieldName)) return fieldName;
            currentClassName = getSuperClassName(currentClassName);
        }
        return name;
    }

    /**
     * 難読化されたメソッド名とそのメソッドのシグネチャとそのメソッドを持つクラス名からそのメソッドがどのクラスで実装された物かをそのメソッドを持つクラスの親や実装されているインターフェースを辿り、
     * そこから難読化解除されたメソッド名を返します。
     * @param owner そのメソッドを持つクラス名
     * @param name 探す難読化されたメソッド名
     * @param desc 探すメソッドのシグネチャ
     * @return 難読化解除されたメソッド名
     */
    public static String findUgocraftImplSrcMethodName(String owner, String name, String desc) {

        String currentClassName = MCDeobfuscationHelper.map(owner);

        while(currentClassName != null) {
            String methodName = MCDeobfuscationHelper.mapMethodName(MCDeobfuscationHelper.unmap(currentClassName), name, desc);
            if(!Objects.equals(name, methodName)) {
                return methodName;
            }

            Deque<String> interfaceStack = new ArrayDeque<>(Arrays.asList(getImplInterfaceNames(currentClassName)));
            while(!interfaceStack.isEmpty()) {
                String poppedInterfaceName = interfaceStack.pop();
                methodName = MCDeobfuscationHelper.mapMethodName(MCDeobfuscationHelper.unmap(poppedInterfaceName), name, desc);
                if(!Objects.equals(name, methodName)) return methodName;

                String superPoppedInterfaceName = getSuperClassName(poppedInterfaceName);
                if(superPoppedInterfaceName != null) interfaceStack.push(superPoppedInterfaceName);
            }
            currentClassName = getSuperClassName(currentClassName);
        }
        return name;
    }
}
