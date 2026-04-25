package com.knsn92.ugorge.ugocraft;

import com.knsn92.ugorge.Ugorge;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

public class UgocraftMovingEntityRender extends Render {

    private Field blocksField;
    private Method blockMethod;
    private Method metadataMethod;
    private Field xOffsetField;
    private Field yOffsetField;
    private Field zOffsetField;
    private boolean reflectionFailed;

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        Collection<?> blocks = getMovingBlocks(entity);
        if (blocks == null || blocks.isEmpty()) {
            return;
        }

        RenderBlocks renderBlocks = new RenderBlocks();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        try {
            for (Object movingBlock : blocks) {
                renderMovingBlock(renderBlocks, movingBlock);
            }
        } finally {
            GL11.glPopMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.locationBlocksTexture;
    }

    private Collection<?> getMovingBlocks(Entity entity) {
        if (reflectionFailed) {
            return null;
        }

        try {
            if (blocksField == null) {
                blocksField = entity.getClass().getField("zinnia");
            }

            Object value = blocksField.get(entity);
            if (value instanceof Map) {
                return ((Map<?, ?>) value).values();
            }
        } catch (ReflectiveOperationException | LinkageError e) {
            reflectionFailed = true;
            Ugorge.LOGGER.warn("Unable to access UgoCraft moving block snapshots", e);
        }
        return null;
    }

    private void renderMovingBlock(RenderBlocks renderBlocks, Object movingBlock) {
        try {
            initMovingBlockAccess(movingBlock.getClass());

            Block block = (Block) blockMethod.invoke(movingBlock);
            if (block == null || block == Blocks.air) {
                return;
            }

            int metadata = (Integer) metadataMethod.invoke(movingBlock);
            float dx = xOffsetField.getFloat(movingBlock);
            float dy = yOffsetField.getFloat(movingBlock);
            float dz = zOffsetField.getFloat(movingBlock);

            GL11.glPushMatrix();
            GL11.glTranslatef(dx, dy, dz);
            UgocraftSafeRenderer.renderMovingBlock(block, metadata, renderBlocks);
            GL11.glPopMatrix();
        } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            Ugorge.LOGGER.warn("Unable to render a UgoCraft moving block snapshot", e);
        }
    }

    private void initMovingBlockAccess(Class<?> movingBlockClass) throws NoSuchFieldException, NoSuchMethodException {
        if (blockMethod != null) {
            return;
        }

        blockMethod = movingBlockClass.getMethod("iris");
        metadataMethod = movingBlockClass.getMethod("phlox");
        xOffsetField = movingBlockClass.getField("dandelion");
        yOffsetField = movingBlockClass.getField("passion_flower");
        zOffsetField = movingBlockClass.getField("lizards_tail");
    }
}
