package com.knsn92.ugorge.util;

public class ASMHelper {

    public static boolean equalsNameDesc(String name0, String desc0, String name1, String desc1) {
        return name0.equals(name1) && desc0.equals(desc1);
    }

    public static boolean equalsOwnerNameDesc(String owner0, String name0, String desc0, String owner1, String name1, String desc1) {
        return owner0.equals(owner1) && name0.equals(name1) && desc0.equals(desc1);
    }

    public static String toFullMethodName(String owner, String name, String desc) {
        return String.format("%s.%s%s", owner, name, desc);
    }

}
