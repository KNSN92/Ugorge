package com.knsn92.ugorge.ugocraft;

import net.minecraft.block.Block;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.world.IBlockAccess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * UgoCraftのnet.maocat.Loader.ProcessのClientとServerの中にあるClientInterruptとServerInterruptのクラスの中のメソッドを呼び出す。
 */
public class UgocraftInvoker {

    public static final String internalName = UgocraftInvoker.class.getName().replace(".", "/");

    private static Method c001;
    private static Method c002;
    private static Method c003;
    private static Method c004;
    private static Method c005;
    private static Method s001;
    private static Method s002;

    /**
     * 初期化。{@link UgocraftLoader}の初期化が前提。
     */
    public static void init() {
        Class<?> serverInterrupt = UgocraftLoader.getClass("net.maocat.Loader.Process.Server.ServerInterrupt");
        Class<?> clientInterrupt = UgocraftLoader.getClass("net.maocat.Loader.Process.Client.ClientInterrupt");

        if(serverInterrupt == null || clientInterrupt == null) {
            return;
        }

        try {
            c001 = clientInterrupt.getDeclaredMethod("c001", NetHandlerPlayClient.class, S3FPacketCustomPayload.class);
            c001.setAccessible(true);

            c002 = clientInterrupt.getDeclaredMethod("c002", Map.class);
            c002.setAccessible(true);

            c003 = clientInterrupt.getDeclaredMethod("c003", int.class, RenderBlocks.class, IBlockAccess.class, Block.class, int.class, int.class, int.class);
            c003.setAccessible(true);

            c004 = clientInterrupt.getDeclaredMethod("c004", int.class, RenderBlocks.class, Block.class, int.class);
            c004.setAccessible(true);

            c005 = clientInterrupt.getDeclaredMethod("c005", int.class);
            c005.setAccessible(true);

            s001 = serverInterrupt.getDeclaredMethod("s001", NetHandlerPlayServer.class, C17PacketCustomPayload.class);
            s001.setAccessible(true);

            s002 = serverInterrupt.getDeclaredMethod("s002");
            s002.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * パケット通信関係のクライアント側？
     * @param netHandlerPlayClient
     * @param s3FPacketCustomPayload
     */
    @SuppressWarnings("unused")
    public static void invoke_c001(NetHandlerPlayClient netHandlerPlayClient, S3FPacketCustomPayload s3FPacketCustomPayload) {
        try {
            c001.invoke(null, netHandlerPlayClient, s3FPacketCustomPayload);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * UgoCraftのエンティティのレンダーを引数のMapにputする処理とUgoCraftの初期化を行う部分だと思われる
     * @param renderMap
     */
    @SuppressWarnings("unused")
    public static void invoke_c002(Map<?, ?> renderMap) {
        try {
            c002.invoke(null, renderMap);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ForgeのISimpleBlockRenderingHandlerのrenderWorldBlockの所の処理
     * @param modelId
     * @param renderer
     * @param world
     * @param block
     * @param x
     * @param y
     * @param z
     * @return
     */
    @SuppressWarnings("unused")
    public static boolean invoke_c003(int modelId, RenderBlocks renderer, IBlockAccess world, Block block, int x, int y, int z) {
        try {
            return (boolean)c003.invoke(null, modelId, renderer, world, block, x, y, z);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ForgeのISimpleBlockRenderingHandlerのrenderInventoryBlockの所の処理
     * @param modelId
     * @param renderer
     * @param block
     * @param metadata
     */
    @SuppressWarnings("unused")
    public static void invoke_c004(int modelId, RenderBlocks renderer, Block block, int metadata) {
        try {
            c004.invoke(null, modelId, renderer, block, metadata);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ForgeのISimpleBlockRenderingHandlerのshouldRender3DInInventoryの所の処理
     * @param modelId
     * @return
     */
    @SuppressWarnings("unused")
    public static boolean invoke_c005(int modelId) {
        try {
            return (boolean)c005.invoke(null, modelId);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * パケット通信関係のサーバー側？
     * @param netHandlerPlayServer
     * @param c17PacketCustomPayload
     */
    @SuppressWarnings("unused")
    public static void invoke_s001(NetHandlerPlayServer netHandlerPlayServer, C17PacketCustomPayload c17PacketCustomPayload) {
        try {
            s001.invoke(null, netHandlerPlayServer, c17PacketCustomPayload);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * c002の初期化処理だけを行う。ただUgoCraft内では使われてない
     */
    @SuppressWarnings("unused")
    public static void invoke_s002() {
        try {
            s002.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
