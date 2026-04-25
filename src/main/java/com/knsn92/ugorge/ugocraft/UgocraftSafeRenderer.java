package com.knsn92.ugorge.ugocraft;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public final class UgocraftSafeRenderer {

    private UgocraftSafeRenderer() {
    }

    public static boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer) {
        try {
            block.setBlockBoundsBasedOnState(world, x, y, z);
            renderer.setRenderBoundsFromBlock(block);
            return renderer.renderStandardBlock(block, x, y, z);
        } catch (RuntimeException ignored) {
            return renderWorldBlockFaces(world, x, y, z, block, renderer);
        }
    }

    public static void renderInventoryBlock(Block block, int metadata, RenderBlocks renderer) {
        final Tessellator tessellator = Tessellator.instance;

        block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(block);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon(renderer, block, 0, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon(renderer, block, 1, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon(renderer, block, 2, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon(renderer, block, 3, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon(renderer, block, 4, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon(renderer, block, 5, metadata));
        tessellator.draw();

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    public static void renderMovingBlock(Block block, int metadata, RenderBlocks renderer) {
        final Tessellator tessellator = Tessellator.instance;

        block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(block);

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        tessellator.setColorOpaque_F(0.5F, 0.5F, 0.5F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon(renderer, block, 0, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon(renderer, block, 1, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon(renderer, block, 2, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon(renderer, block, 3, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon(renderer, block, 4, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon(renderer, block, 5, metadata));
        tessellator.draw();
    }

    private static boolean renderWorldBlockFaces(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer) {
        final Tessellator tessellator = Tessellator.instance;
        final int metadata = world.getBlockMetadata(x, y, z);

        block.setBlockBoundsBasedOnState(world, x, y, z);
        renderer.setRenderBoundsFromBlock(block);

        tessellator.setColorOpaque_F(0.5F, 0.5F, 0.5F);
        renderer.renderFaceYNeg(block, x, y, z, icon(renderer, block, 0, metadata));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        renderer.renderFaceYPos(block, x, y, z, icon(renderer, block, 1, metadata));
        tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
        renderer.renderFaceZNeg(block, x, y, z, icon(renderer, block, 2, metadata));
        renderer.renderFaceZPos(block, x, y, z, icon(renderer, block, 3, metadata));
        tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
        renderer.renderFaceXNeg(block, x, y, z, icon(renderer, block, 4, metadata));
        renderer.renderFaceXPos(block, x, y, z, icon(renderer, block, 5, metadata));
        return true;
    }

    private static IIcon icon(RenderBlocks renderer, Block block, int side, int metadata) {
        return renderer.getBlockIconFromSideAndMetadata(block, side, metadata);
    }
}
