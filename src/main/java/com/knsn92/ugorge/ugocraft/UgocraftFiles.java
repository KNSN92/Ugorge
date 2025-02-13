package com.knsn92.ugorge.ugocraft;

import net.minecraft.client.Minecraft;

import java.io.File;

public class UgocraftFiles {

    public static final String UGOCRAFT_FOLDER_NAME = "ugocraft";
    public static final String UGOCRAFT_JAR_NAME = "UgoCraft_Client.jar";

    public static final String UGOCRAFT_DEBUG_FOLDER_NAME = "debug";
    public static final String UGOCRAFT_DEBUG_JAR_NAME = "UgoCraft_Client_Debug.jar";

    public static final File UGOCRAFT_FOLDER = new File(Minecraft.getMinecraft().mcDataDir, UGOCRAFT_FOLDER_NAME);
    public static final File UGOCRAFT_JAR = new File(UGOCRAFT_FOLDER, UGOCRAFT_JAR_NAME);

    public static final File UGOCRAFT_DEBUG_FOLDER = new File(UGOCRAFT_FOLDER, UGOCRAFT_DEBUG_FOLDER_NAME);
    public static final File UGOCRAFT_DEBUG_JAR = new File(UGOCRAFT_DEBUG_FOLDER, UGOCRAFT_DEBUG_JAR_NAME);

    private UgocraftFiles() {}

}
