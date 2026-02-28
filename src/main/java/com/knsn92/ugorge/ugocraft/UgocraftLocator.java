package com.knsn92.ugorge.ugocraft;

import net.minecraft.client.Minecraft;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.jar.*;
import java.util.zip.ZipException;

/**
 * UgoCraftのjarファイルをmodsフォルダから検索するクラス。
 */
public class UgocraftLocator {

    private static final String versionTxtMinecraft = "Minecraft1.7.10";
    private static final String versionTxtUgocraft = "UgoCraft2.3.0";

    private static final String serverInterruptClassJarPath = "net/maocat/Loader/Process/Server/ServerInterrupt";
    private static final String clientInterruptClassJarPath = "net/maocat/Loader/Process/Client/ClientInterrupt";

    public static final String ugocraftJarName = "UgoCraft_Client.jar";


    /**
     * modsフォルダからUgoCraftのjarファイルを再帰的に検索する。
     * @return UgoCraftのjarファイル。見つからない場合はnull。
     * @throws IOException ファイルの読み込みに失敗した場合
     */
    public static File locateUgocraft() throws IOException {
        File modsFolder = new File(Minecraft.getMinecraft().mcDataDir, "mods");
        return locateUgocraft(modsFolder);
    }

    private static File locateUgocraft(File folder) throws IOException {
        if(!folder.exists() || !folder.isDirectory()) return null;
        File ugocraftClientJar = new File(folder, UgocraftLocator.ugocraftJarName);
        if(ugocraftClientJar.exists() && isUgocraft(ugocraftClientJar)) {
            return ugocraftClientJar;
        }
        File[] mods = folder.listFiles();
        if(mods == null) return null;
        for(File mod : mods) {
            if(mod.isDirectory()) {
                File found = locateUgocraft(mod);
                if(found != null) return found;
            }else {
                if(isUgocraft(mod)) return mod;
            }
        }
        return null;
    }

    private static boolean isUgocraft(File file) throws IOException {
        try(JarFile jar = new JarFile(file)) {

            // Has version.txt? and it contains version of minecraft and ugocraft?
            JarEntry versionTxt = jar.getJarEntry("version.txt");
            if(versionTxt == null) return false;
            String[] lines;
            try(InputStream versionTxtInputStream = jar.getInputStream(versionTxt)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(versionTxtInputStream, StandardCharsets.UTF_8));
                lines = reader.lines().toArray(String[]::new);
            }
            if(lines.length < 2) return false;
            if(!(versionTxtMinecraft.equals(lines[0]) && versionTxtUgocraft.equals(lines[1]))) return false;

            // Has ServerInterrupt.class and ClientInterrupt.class?
            if(jar.getJarEntry(UgocraftLocator.serverInterruptClassJarPath) != null) return false;
            if(jar.getJarEntry(UgocraftLocator.clientInterruptClassJarPath) != null) return false;
        }catch(ZipException | SecurityException e) {
            return false;
        }
        return true;
    }

}
