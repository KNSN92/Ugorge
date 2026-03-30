package com.knsn92.ugorge.ugocraft;

import com.knsn92.ugorge.Ugorge;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URISyntaxException;

/**
 * UgoCraftの一部の書き換えの際に、代わりに参照させる変数群。
 */
public class UgocraftHook {

    public static final String internalName = UgocraftHook.class.getName().replace(".", "/");


    @SuppressWarnings("unused")
    public static File getUgocraftJarLocation() {
        return Ugorge.instance().ugocraftJar;
    }

    @SuppressWarnings("unused")
    public static File getMinecraftServerLocation() {
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
        return new File(mcServerURI);
    }

    @SuppressWarnings("unused")
    public static ClassLoader getUgocraftClassLoader() {
        return Ugorge.instance().classLoader;
    }

    @SuppressWarnings("unused")
    public static void c001(NetHandlerPlayClient netHandlerPlayClient, S3FPacketCustomPayload s3FPacketCustomPayload) {
        Ugorge.instance().invoker.invoke_c001(netHandlerPlayClient, s3FPacketCustomPayload);
    }

    @SuppressWarnings("unused")
    public static void s001(NetHandlerPlayServer netHandlerPlayServer, C17PacketCustomPayload c17PacketCustomPayload) {
        Ugorge.instance().invoker.invoke_s001(netHandlerPlayServer, c17PacketCustomPayload);
    }
}
