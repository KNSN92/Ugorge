package com.knsn92.ugorge;

import com.knsn92.ugorge.ugocraft.*;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;

@Mod(modid = Ugorge.MOD_ID, name = Ugorge.MOD_NAME, version = Ugorge.MOD_VERSION)
public class Ugorge {

    public static final String MOD_ID = "Ugorge";
    public static final String MOD_NAME = "Ugorge";
    public static final String MOD_VERSION = "0.2.0";

    public static boolean isDevEnv = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    public static final Logger LOGGER = LogManager.getLogger("Ugorge");

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) throws Exception {
        LOGGER.info("Searching UgoCraft Jar file...");
        File ugocraftJar = UgocraftLocator.locateUgocraft();
        if(ugocraftJar == null) {
            throw new NoSuchFileException("UgoCraft jar file not found in mods folder");
        }
        LOGGER.info("UgoCraft Jar File found!");

        // Exclude UgoCraft classes from being loaded by the default classloader
        Launch.classLoader.addClassLoaderExclusion("net.maocat.");

        UgocraftClassData.loadData(ugocraftJar);
        UgocraftLoader.load(ugocraftJar);
        UgocraftHook.init(ugocraftJar);
        UgocraftInvoker.init();

        LOGGER.info("UgoCraft Loaded");

        if(isDevEnv) createTransformedUgoCraftJar(ugocraftJar);

        registerEntityRender();
        registerBlockRender();

        LOGGER.info("UgoCraft Launched");

    }

    @SuppressWarnings("unused")
    private static void createTransformedUgoCraftJar(File ugocraftJar) throws IOException {
        LOGGER.info("Generating Transformed Ugocraft Jar...");
        File ugocraftDebugFolder = new File(Minecraft.getMinecraft().mcDataDir, "ugocraft/debug");
        File ugocraftDebugJar = new File(ugocraftDebugFolder, "UgoCraft_Client_Debug.jar");
        if(ugocraftDebugFolder.mkdirs()) {
            UgocraftLoader.createJar(ugocraftJar, ugocraftDebugJar);
        }
        LOGGER.info("Transformed Ugocraft Jar generated in \"ugocraft/debug\"");
        LOGGER.info("   ※※ Redistribution prohibited in accordance with the wishes of Ugocraft creator Mao ※※");
    }

    @SuppressWarnings("unchecked")
    private static void registerEntityRender() {
        Map<Class<?>, Render> ugocraftEntityRenderMap = new HashMap<>();

        UgocraftInvoker.invoke_c002(ugocraftEntityRenderMap);

        for(Map.Entry<Class<?>, Render> entry: ugocraftEntityRenderMap.entrySet()) {
            Render render = entry.getValue();
            render.setRenderManager(RenderManager.instance);
            RenderManager.instance.entityRenderMap.put(entry.getKey(), render);
        }
    }

    private static void registerBlockRender() throws IllegalAccessException, NoSuchFieldException {
        Class<?> ugoRenderRegistry = UgocraftLoader.getClass("net.maocat.Loader.Process.Client.Shantaks");
        if(ugoRenderRegistry != null) {
            Map<?, ?> renderers = (Map<?, ?>) ugoRenderRegistry.getField("morning_glory").get(null);

            for (Object renderId : renderers.keySet()) {
                UgocraftBlockRender render = new UgocraftBlockRender((int) renderId);
                RenderingRegistry.registerBlockHandler(render);
            }
        }
    }
}
