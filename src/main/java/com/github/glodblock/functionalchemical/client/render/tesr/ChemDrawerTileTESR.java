package com.github.glodblock.functionalchemical.client.render.tesr;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.block.tile.ControllableDrawerTile;
import com.buuz135.functionalstorage.client.DrawerRenderer;
import com.buuz135.functionalstorage.client.FunctionalStorageClientConfig;
import com.buuz135.functionalstorage.item.ConfigurationToolItem;
import com.github.glodblock.functionalchemical.common.tileentities.ChemicalDrawerTile;
import com.github.glodblock.functionalchemical.util.FCUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class ChemDrawerTileTESR implements BlockEntityRenderer<ChemicalDrawerTile> {

    public ChemDrawerTileTESR(BlockEntityRendererProvider.Context ignored) {

    }

    public static void renderChemStack(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay, ChemicalStack<?> stack, long amount, long maxAmount, float scale, ControllableDrawerTile.DrawerOptions options, AABB bounds, boolean halfText) {
        matrixStack.pushPose();
        var still = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stack.getType().getIcon());
        var builder = bufferIn.getBuffer(RenderType.solid());

        float[] color = decomposeColorF(stack.getChemicalTint());
        float red = color[1];
        float green = color[2];
        float blue = color[3];
        float alpha = amount == 0 ? 0.3f : color[0];

        float x1 = (float) bounds.minX;
        float x2 = (float) bounds.maxX;
        float y1 = (float) bounds.minY;
        float y2 = (float) bounds.maxY;
        float z1 = (float) bounds.minZ;
        float z2 = (float) bounds.maxZ;
        double bx1 = bounds.minX * 16;
        double bx2 = bounds.maxX * 16;
        double by1 = bounds.minY * 16;
        double by2 = bounds.maxY * 16;
        double bz1 = bounds.minZ * 16;
        double bz2 = bounds.maxZ * 16;


        var posMat = matrixStack.last().pose();

        //TOP
        {
            float u1 = still.getU(bx1);
            float u2 = still.getU(bx2);
            float v1 = still.getV(bz1);
            float v2 = still.getV(bz2);
            builder.vertex(posMat, x1, y2, z2).color(red, green, blue, alpha).uv(u1, v2).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 1f, 0f).endVertex();
            builder.vertex(posMat, x2, y2, z2).color(red, green, blue, alpha).uv(u2, v2).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 1f, 0f).endVertex();
            builder.vertex(posMat, x2, y2, z1).color(red, green, blue, alpha).uv(u2, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 1f, 0f).endVertex();
            builder.vertex(posMat, x1, y2, z1).color(red, green, blue, alpha).uv(u1, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 1f, 0f).endVertex();
        }
        //FRONT
        {
            float u1 = still.getU(bx1);
            float u2 = still.getU(bx2);
            float v1 = still.getV(by1);
            float v2 = still.getV(by2);
            builder.vertex(posMat, x2, y1, z2).color(red, green, blue, alpha).uv(u2, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
            builder.vertex(posMat, x2, y2, z2).color(red, green, blue, alpha).uv(u2, v2).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
            builder.vertex(posMat, x1, y2, z2).color(red, green, blue, alpha).uv(u1, v2).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
            builder.vertex(posMat, x1, y1, z2).color(red, green, blue, alpha).uv(u1, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
        }
        matrixStack.popPose();
        if (options.isActive(ConfigurationToolItem.ConfigurationAction.TOGGLE_NUMBERS)) {
            matrixStack.pushPose();
            matrixStack.translate(0.5, 0.84, 0.97);
            if (halfText) matrixStack.translate(-0.25, 0, 0);
            DrawerRenderer.renderText(matrixStack, bufferIn, combinedOverlay, Component.literal(ChatFormatting.WHITE + FCUtil.getFormatedChemBigNumber(amount)), Direction.NORTH, scale);
            matrixStack.popPose();
        }
        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.453, 0.97);
        if (halfText) {
            matrixStack.scale(0.5f, 0.65f, 0.5f);
            matrixStack.translate(-0.5, -0.18, 0);
        }
        DrawerRenderer.renderIndicator(matrixStack, bufferIn, combinedLight, combinedOverlay, Math.min(1, amount / (float) maxAmount), options);
        matrixStack.popPose();
    }

    public static float[] decomposeColorF(int color) {
        float[] res = new float[4];
        res[0] = (color >> 24 & 0xff) / 255f;
        res[1] = (color >> 16 & 0xff) / 255f;
        res[2] = (color >> 8 & 0xff) / 255f;
        res[3] = (color & 0xff) / 255f;
        return res;
    }

    @Override
    public void render(@NotNull ChemicalDrawerTile tile, float partialTicks, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (Minecraft.getInstance().player != null && !tile.getBlockPos().closerThan(Minecraft.getInstance().player.getOnPos(), FunctionalStorageClientConfig.DRAWER_RENDER_RANGE)) {
            return;
        }
        if (tile.getLevel() == null) {
            return;
        }
        matrixStack.pushPose();
        Direction facing = tile.getFacingDirection();
        combinedOverlayIn = OverlayTexture.NO_OVERLAY;
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-180));
        if (facing == Direction.NORTH) {
            matrixStack.translate(-1, 0, -1);
        }
        if (facing == Direction.EAST) {
            matrixStack.translate(0, 0, -1);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90));
        }
        if (facing == Direction.SOUTH) {
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-180));
        }
        if (facing == Direction.WEST) {
            matrixStack.translate(-1, 0, 0);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
        }
        combinedLightIn = LevelRenderer.getLightColor(tile.getLevel(), tile.getBlockPos().relative(facing));
        if (tile.getDrawerType() == FunctionalStorage.DrawerType.X_1)
            render1Slot(matrixStack, bufferIn, combinedLightIn, combinedOverlayIn, tile);
        if (tile.getDrawerType() == FunctionalStorage.DrawerType.X_2)
            render2Slot(matrixStack, bufferIn, combinedLightIn, combinedOverlayIn, tile);
        if (tile.getDrawerType() == FunctionalStorage.DrawerType.X_4)
            render4Slot(matrixStack, bufferIn, combinedLightIn, combinedOverlayIn, tile);
        matrixStack.pushPose();
        matrixStack.translate(0, 0, 0.9688);
        DrawerRenderer.renderUpgrades(matrixStack, bufferIn, combinedLightIn, combinedOverlayIn, tile);
        matrixStack.popPose();
        matrixStack.popPose();
    }

    private void render1Slot(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, ChemicalDrawerTile tile) {
        var stack = tile.getRenderStack(0);
        if (stack.valid()) {
            var displayAmount = stack.stack().getAmount();
            if (stack.isFilter()) {
                displayAmount = 0;
            }
            var bounds = new AABB(1 / 16D, 1.25 / 16D, 1 / 16D, 15 / 16D, 1.25 / 16D + (stack.stack().getAmount() / (double) tile.getCapacity()) * (12.5 / 16D), 15 / 16D);
            renderChemStack(matrixStack, bufferIn, combinedLightIn, combinedOverlayIn, stack.stack(), displayAmount, tile.getCapacity(), 0.007f, tile.getDrawerOptions(), bounds, false);
        }
    }

    private void render2Slot(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, ChemicalDrawerTile tile) {
        {
            var stack = tile.getRenderStack(0);
            if (stack.valid()) {
                var displayAmount = stack.stack().getAmount();
                if (stack.isFilter()) {
                    displayAmount = 0;
                }
                var bounds = new AABB(1 / 16D, 1.25 / 16D, 1 / 16D, 15 / 16D, 1.25 / 16D + (stack.stack().getAmount() / (double) tile.getCapacity()) * (5.5 / 16D), 15 / 16D);
                renderChemStack(matrixStack, bufferIn, combinedLightIn, combinedOverlayIn, stack.stack(), displayAmount, tile.getCapacity(), 0.007f, tile.getDrawerOptions(), bounds, false);
            }
        }
        {
            var stack = tile.getRenderStack(1);
            if (stack.valid()) {
                matrixStack.pushPose();
                matrixStack.translate(0, 0.5, 0);
                var displayAmount = stack.stack().getAmount();
                if (stack.isFilter()) {
                    displayAmount = 0;
                }
                var bounds = new AABB(1 / 16D, 1.25 / 16D, 1 / 16D, 15 / 16D, 1.25 / 16D + (stack.stack().getAmount() / (double) tile.getCapacity()) * (5.5 / 16D), 15 / 16D);
                renderChemStack(matrixStack, bufferIn, combinedLightIn, combinedOverlayIn, stack.stack(), displayAmount, tile.getCapacity(), 0.007f, tile.getDrawerOptions(), bounds, false);
                matrixStack.popPose();
            }
        }
    }

    private void render4Slot(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, ChemicalDrawerTile tile) {
        {
            var stack = tile.getRenderStack(0);
            if (stack.valid()) {
                matrixStack.pushPose();
                matrixStack.translate(0.5, 0, 0);
                var displayAmount = stack.stack().getAmount();
                if (stack.isFilter()) {
                    displayAmount = 0;
                }
                var bounds = new AABB(1 / 16D, 1.25 / 16D, 1 / 16D, 8 / 16D, 1.25 / 16D + (stack.stack().getAmount() / (double) tile.getCapacity()) * (5.5 / 16D), 15 / 16D);
                renderChemStack(matrixStack, bufferIn, combinedLightIn, combinedOverlayIn, stack.stack(), displayAmount, tile.getCapacity(), 0.007f, tile.getDrawerOptions(), bounds, true);
                matrixStack.popPose();
            }
        }
        {
            var stack = tile.getRenderStack(1);
            if (stack.valid()) {
                matrixStack.pushPose();
                var displayAmount = stack.stack().getAmount();
                if (stack.isFilter()) {
                    displayAmount = 0;
                }
                var bounds = new AABB(1 / 16D, 1.25 / 16D, 1 / 16D, 8 / 16D, 1.25 / 16D + (stack.stack().getAmount() / (double) tile.getCapacity()) * (5.5 / 16D), 15 / 16D);
                renderChemStack(matrixStack, bufferIn, combinedLightIn, combinedOverlayIn, stack.stack(), displayAmount, tile.getCapacity(), 0.007f, tile.getDrawerOptions(), bounds, true);
                matrixStack.popPose();
            }
        }
        {
            var stack = tile.getRenderStack(2);
            if (stack.valid()) {
                matrixStack.pushPose();
                matrixStack.translate(0.5, 0.5, 0);
                var displayAmount = stack.stack().getAmount();
                if (stack.isFilter()) {
                    displayAmount = 0;
                }
                var bounds = new AABB(1 / 16D, 1.25 / 16D, 1 / 16D, 8 / 16D, 1.25 / 16D + (stack.stack().getAmount() / (double) tile.getCapacity()) * (5.5 / 16D), 15 / 16D);
                renderChemStack(matrixStack, bufferIn, combinedLightIn, combinedOverlayIn, stack.stack(), displayAmount, tile.getCapacity(), 0.007f, tile.getDrawerOptions(), bounds, true);
                matrixStack.popPose();
            }
        }
        {
            var stack = tile.getRenderStack(3);
            if (stack.valid()) {
                matrixStack.pushPose();
                matrixStack.translate(0, 0.5, 0);
                var displayAmount = stack.stack().getAmount();
                if (stack.isFilter()) {
                    displayAmount = 0;
                }
                var bounds = new AABB(1 / 16D, 1.25 / 16D, 1 / 16D, 8 / 16D, 1.25 / 16D + (stack.stack().getAmount() / (double) tile.getCapacity()) * (5.5 / 16D), 15 / 16D);
                renderChemStack(matrixStack, bufferIn, combinedLightIn, combinedOverlayIn, stack.stack(), displayAmount, tile.getCapacity(), 0.007f, tile.getDrawerOptions(), bounds, true);
                matrixStack.popPose();
            }
        }
    }

}
