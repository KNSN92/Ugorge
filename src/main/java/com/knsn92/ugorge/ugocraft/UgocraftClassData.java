package com.knsn92.ugorge.ugocraft;

import com.knsn92.ugorge.util.MCDeobfuscationHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * UgoCraftの殆どのクラスの親クラスと実装しているインターフェース、フィールドとメソッドの一覧を保存します。
 */
public class UgocraftClassData {

    public static final String[] rewriteListClasses = {"bjb", "bnn", "blm", "nh"};

    private final Map<String, String>   superClasses = new HashMap<>();
    private final Map<String, String[]> implInterfaces = new HashMap<>();
    private final Map<String, String[]> fields = new HashMap<>();
    private final Map<String, String[]> methods = new HashMap<>();

    public void putData(String internalClassName, String superClassName, String[] implInterfaceNames, String[] fieldNames, String[] methodNames) {
        this.superClasses.put(internalClassName, superClassName);
        this.implInterfaces.put(internalClassName, implInterfaceNames);
        this.fields.put(internalClassName, fieldNames);
        this.methods.put(internalClassName, methodNames);
    }

    /**
     * そのクラスがUgoCraftに含まれるかを判定します。
     * @param internalClassName 内部クラス名(例:java/lang/Object)
     * @return そのクラスがUgoCraftに含まれるか
     */
    public boolean hasClass(String internalClassName) {
        return this.superClasses.containsKey(internalClassName);
    }

    /**
     * そのUgoCraftクラスのスーパークラス名を返します。
     * @param internalClassName 内部クラス名(例:java/lang/Object)
     * @return そのUgoCraftクラスのスーパークラス名
     */
    public String getSuperClass(String internalClassName) {
        return this.superClasses.get(internalClassName);
    }

    /**
     * そのUgoCraftクラスの実装しているインターフェースの一覧を返します。
     * @param internalClassName 内部クラス名(例:java/lang/Object)
     * @return そのUgoCraftクラスの実装しているインターフェースの一覧
     */
    public String[] getImplInterfaces(String internalClassName) {
        return this.implInterfaces.get(internalClassName);
    }

    /**
     * そのUgoCraftクラスがそのインターフェースを実装しているかを返します。
     * @param internalClassName 内部クラス名(例:java/lang/Object)
     * @param internalInterfaceClassName 内部インターフェースクラス名(例:java/lang/Object)
     * @return そのUgoCraftクラスがそのインターフェースを実装しているか
     */
    public boolean hasImplInterface(String internalClassName, String internalInterfaceClassName) {
        return ArrayUtils.contains(this.getImplInterfaceNames(internalClassName), internalInterfaceClassName);
    }

    /**
     * そのUgoCraftクラスに存在するフィールドの一覧を返します。
     * @param internalClassName 内部クラス名(例:java/lang/Object)
     * @return そのUgoCraftクラスに存在するフィールドの一覧
     */
    public String[] getFields(String internalClassName) {
        return this.fields.get(internalClassName);
    }

    /**
     * そのUgoCraftクラスがそのフィールドを持っているかを返します。
     * @param internalClassName 内部クラス名(例:java/lang/Object)
     * @param fieldName フィールド名
     * @return そのUgoCraftクラスがそのフィールドを持っているかを返します。
     */
    public boolean hasField(String internalClassName, String fieldName) {
        return ArrayUtils.contains(this.getFields(internalClassName), fieldName);
    }

    /**
     * そのUgoCraftクラスに存在するメソッドの一覧を返します。
     * @param internalClassName 内部クラス名(例:java/lang/Object)
     * @return そのUgoCraftクラスに存在するメソッドの一覧
     */
    public String[] getMethods(String internalClassName) {
        return this.methods.get(internalClassName);
    }

    /**
     * そのUgoCraftクラスがそのフィールドを持っているかを返します。
     * @param internalClassName 内部クラス名(例:java/lang/Object)
     * @param methodName メソッド名
     * @param methodDesc メソッドのシグネチャ
     * @return そのUgoCraftクラスがそのフィールドを持っているかを返します。
     */
    public boolean hasMethod(String internalClassName, String methodName, String methodDesc) {
        return ArrayUtils.contains(this.getMethods(internalClassName), methodName + StringUtils.SPACE + methodDesc);
    }


    private static Class<?> getClassFromString(String internalClassName) {
        if(internalClassName == null) return null;
        try {
            return Class.forName(MCDeobfuscationHelper.map(internalClassName).replace("/", "."));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private String getSuperClassName(String internalClassName) {
        if(internalClassName == null) return null;

        if(this.hasClass(internalClassName)) {
            String superClassName = this.getSuperClass(internalClassName);
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

    private String[] getImplInterfaceNames(String internalClassName) {
        if(internalClassName == null) return null;

        String[] interfaces;
        if(this.hasClass(internalClassName)) {
            interfaces = this.getImplInterfaces(internalClassName);
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
    public String findImplSrcFieldName(String owner, String name) {
        String currentClassName = MCDeobfuscationHelper.map(owner);

        while(currentClassName != null) {
            String fieldName = MCDeobfuscationHelper.mapFieldName(MCDeobfuscationHelper.unmap(currentClassName), name);
            if(!Objects.equals(name, fieldName)) return fieldName;
            currentClassName = this.getSuperClassName(currentClassName);
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
    public String findImplSrcMethodName(String owner, String name, String desc) {

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

                String superPoppedInterfaceName = this.getSuperClassName(poppedInterfaceName);
                if(superPoppedInterfaceName != null) interfaceStack.push(superPoppedInterfaceName);
            }
            currentClassName = this.getSuperClassName(currentClassName);
        }
        return name;
    }
}
