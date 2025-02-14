package com.knsn92.ugorge;

import com.knsn92.ugorge.ugocraft.*;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.knsn92.ugorge.ugocraft.UgocraftFiles.*;

@Mod(modid = Ugorge.MOD_ID, name = Ugorge.MOD_NAME, version = Ugorge.MOD_VERSION)
public class Ugorge {

    public static final String MOD_ID = "Ugorge";
    public static final String MOD_NAME = "Ugorge";
    public static final String MOD_VERSION = "0.1.0";

    public static boolean isDevEnv = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    public static final Logger LOGGER = LogManager.getLogger("Ugorge");

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) throws Exception {

        boolean canLoadUgoCraft = resolveUgoCraftFile();
        if(!canLoadUgoCraft) {
            LOGGER.warn("[Ugorge] Skip loading UgoCraft...");
            return;
        }

        UgocraftClassData.loadData(UGOCRAFT_JAR);
        UgocraftLoader.load(UGOCRAFT_JAR);
        UgocraftHook.init(UGOCRAFT_JAR);
        UgocraftInvoker.init();

        LOGGER.info("[Ugorge] UgoCraft Loaded");

        if(isDevEnv) {
            createTransformedUgoCraftJar();
        }

        registerEntityRender();
        registerBlockRender();

        LOGGER.info("[Ugorge] UgoCraft Launched");

    }

    private static boolean resolveUgoCraftFile() throws FileNotFoundException {
        LOGGER.info("Searching UgoCraft Jar file...");
        if(!UGOCRAFT_FOLDER.exists()) {
            boolean dirMakeSuccess = UGOCRAFT_FOLDER.mkdir();
            if(!dirMakeSuccess) {
                LOGGER.error("[Ugorge] For some reason could not generate \"" + UGOCRAFT_FOLDER_NAME + "\" folder in gamedir.");
                return false;
            }
            LOGGER.error("[Ugorge] \""+UGOCRAFT_FOLDER_NAME+"\" folder not found, New folders are created in gamedir.\n\tPlease put the UgoCraft Jar File in it.");
            throw new FileNotFoundException("\""+UGOCRAFT_FOLDER_NAME+"\" folder not found. New folders are created in gamedir.");
        }
        if(!UGOCRAFT_JAR.exists()) {
            LOGGER.error("[Ugorge] UgoCraft Jar File not found, Please put in \"" + UGOCRAFT_FOLDER_NAME + "\" folder\nIt must be named \"" + UGOCRAFT_JAR_NAME + "\".");
            throw new FileNotFoundException("\""+UGOCRAFT_JAR_NAME+"\" File not found. Please see the log.");
        }
        LOGGER.info("[Ugorge] UgoCraft Jar File found!");
        return true;
    }

    @SuppressWarnings("unused")
    private static void createTransformedUgoCraftJar() throws IOException {
        LOGGER.info("[Ugorge] Generating Transformed Ugocraft Jar...");
        UgocraftLoader.createJar(UGOCRAFT_JAR, UGOCRAFT_DEBUG_JAR);
        LOGGER.info("[Ugorge] Transformed Ugocraft Jar generated in \"" + UGOCRAFT_FOLDER_NAME + "/" + UGOCRAFT_DEBUG_FOLDER_NAME + "\"");
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
