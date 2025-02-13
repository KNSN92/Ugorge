package com.knsn92.ugorge.ugocraft;

import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URISyntaxException;

/**
 * UgoCraftの一部の書き換えの際に、代わりに参照させる変数群。
 */
public class UgocraftHook {

    public static File ugocraftJarLocation = null;
    public static File minecraftServerLocation = null;
    public static ClassLoader ugocraftClassLoader = null;

    public static final String internalName = UgocraftHook.class.getName().replace(".", "/");

    /**
     * 初期化。{@link UgocraftLoader}の初期化が前提。
     * @param ugocraftJarFile
     */
    public static void init(File ugocraftJarFile) {
        ugocraftJarLocation = ugocraftJarFile;

        String mcServerURI = getMcServerURI();
        minecraftServerLocation = new File(mcServerURI);

        ugocraftClassLoader = UgocraftLoader.getClassLoader();
    }

    private static String getMcServerURI() {
        String mcServerURI;
        try {
            mcServerURI = MinecraftServer.class.getProtectionDomain().getCodeSource().getLocation().toURI().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mcServerURI = mcServerURI.replace("jar:", "");
        mcServerURI = mcServerURI.replace("file:", "");
        mcServerURI = mcServerURI.replace("%20", StringUtils.SPACE);
        if(mcServerURI.contains("!")) {
            mcServerURI = mcServerURI.substring(0, mcServerURI.indexOf("!"));
        }
        return mcServerURI;
    }
}
