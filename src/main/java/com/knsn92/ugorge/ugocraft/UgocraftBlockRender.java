package com.knsn92.ugorge.ugocraft;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

/**
 * UgoCraftのBlockのRender処理をForgeのISimpleBlockRenderingHandlerにラップするクラス。
 */
public class UgocraftBlockRender implements ISimpleBlockRenderingHandler {

    private final int renderId;

    public UgocraftBlockRender(int renderId) {
        this.renderId = renderId;
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        return UgocraftInvoker.invoke_c003(modelId, renderer, world, block, x, y, z);
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        UgocraftInvoker.invoke_c004(modelId, renderer, block, metadata);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return UgocraftInvoker.invoke_c005(modelId);
    }

    @Override
    public int getRenderId() {
        return this.renderId;
    }
}
