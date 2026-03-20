package com.github.glodblock.functionalchemical.client.render.tesr;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.block.Drawer;
import com.buuz135.functionalstorage.block.tile.ControllableDrawerTile;
import com.buuz135.functionalstorage.client.DrawerRenderer;
import com.buuz135.functionalstorage.client.FunctionalStorageClientConfig;
import com.buuz135.functionalstorage.item.ConfigurationToolItem;
import com.buuz135.functionalstorage.util.MathUtils;
import com.github.glodblock.functionalchemical.common.tileentities.ChemicalDrawerTile;
import com.github.glodblock.functionalchemical.util.FCUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class ChemDrawerTileTESR implements BlockEntityRenderer<ChemicalDrawerTile> {

    public ChemDrawerTileTESR(BlockEntityRendererProvider.Context ignored) {

    }

    public static void renderChemStack(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay, ChemicalStack stack, long amount, long maxAmount, float scale, ControllableDrawerTile.DrawerOptions options, AABB bounds, boolean halfText) {
        if (options.isActive(ConfigurationToolItem.ConfigurationAction.TOGGLE_RENDER)) {
            matrixStack.pushPose();
            var still = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stack.getChemical().getIcon());
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
            float bx1 = (float) bounds.minX;
            float bx2 = (float) bounds.maxX;
            float by1 = (float) bounds.minY;
            float by2 = (float) bounds.maxY;
            float bz1 = (float) bounds.minZ;
            float bz2 = (float) bounds.maxZ;
            var posMat = matrixStack.last().pose();

            //TOP
            {
                float u1 = still.getU(bx1);
                float u2 = still.getU(bx2);
                float v1 = still.getV(bz1);
                float v2 = still.getV(bz2);
                builder.addVertex(posMat, x1, y2, z2).setColor(red, green, blue, alpha).setUv(u1, v2).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0f, 1f, 0f);
                builder.addVertex(posMat, x2, y2, z2).setColor(red, green, blue, alpha).setUv(u2, v2).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0f, 1f, 0f);
                builder.addVertex(posMat, x2, y2, z1).setColor(red, green, blue, alpha).setUv(u2, v1).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0f, 1f, 0f);
                builder.addVertex(posMat, x1, y2, z1).setColor(red, green, blue, alpha).setUv(u1, v1).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0f, 1f, 0f);
            }
            //FRONT
            {
                float u1 = still.getU(bx1);
                float u2 = still.getU(bx2);
                float v1 = still.getV(by1);
                float v2 = still.getV(by2);
                builder.addVertex(posMat, x2, y1, z2).setColor(red, green, blue, alpha).setUv(u2, v1).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0f, 0f, 1f);
                builder.addVertex(posMat, x2, y2, z2).setColor(red, green, blue, alpha).setUv(u2, v2).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0f, 0f, 1f);
                builder.addVertex(posMat, x1, y2, z2).setColor(red, green, blue, alpha).setUv(u1, v2).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0f, 0f, 1f);
                builder.addVertex(posMat, x1, y1, z2).setColor(red, green, blue, alpha).setUv(u1, v1).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0f, 0f, 1f);
            }
            matrixStack.popPose();
        }
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
    public int getViewDistance() {
        return FunctionalStorageClientConfig.DRAWER_RENDER_RANGE;
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
        Direction subfacing = tile.getFacingDirection();
        if (tile.getBlockState().hasProperty(Drawer.FACING_ALL)) {
            Direction facing = tile.getBlockState().getValue(Drawer.FACING_ALL);
            if (subfacing == Direction.UP) {
                matrixStack.mulPose(MathUtils.createTransformMatrix(new Vector3f(1,0,0), new Vector3f(90, 0, 0), 1));
                if (facing == Direction.EAST) {
                    matrixStack.mulPose(MathUtils.createTransformMatrix(new Vector3f(-1,0,0), new Vector3f(0, 0, -90), 1));
                } else if (facing == Direction.WEST) {
                    matrixStack.mulPose(MathUtils.createTransformMatrix(new Vector3f(0,1,0), new Vector3f(0, 0, 90), 1));
                }
            }
            if (subfacing == Direction.DOWN) {
                matrixStack.mulPose(MathUtils.createTransformMatrix(new Vector3f(0,1,0), new Vector3f(-90, 0, -180), 1));
                if (facing == Direction.WEST) {
                    matrixStack.mulPose(MathUtils.createTransformMatrix(new Vector3f(-1,0,0), new Vector3f(0, 0, -90), 1));
                } else if (facing == Direction.EAST) {
                    matrixStack.mulPose(MathUtils.createTransformMatrix(new Vector3f(0,1,0), new Vector3f(0, 0, 90), 1));
                }
            }
            if (facing == Direction.NORTH) {
                matrixStack.mulPose(MathUtils.createTransformMatrix(new Vector3f(-1,1,0), new Vector3f(0,0,180), 1));
            }
        }
        Direction facing = tile.getFacingDirection();
        matrixStack.mulPose(Axis.YP.rotationDegrees(-180));
        if (facing == Direction.NORTH) {
            matrixStack.translate(-1, 0, -1);
        }
        if (facing == Direction.EAST) {
            matrixStack.translate(0, 0, -1);
            matrixStack.mulPose(Axis.YP.rotationDegrees(-90));
        }
        if (facing == Direction.SOUTH) {
            matrixStack.mulPose(Axis.YP.rotationDegrees(-180));
        }
        if (facing == Direction.WEST) {
            matrixStack.translate(-1, 0, 0);
            matrixStack.mulPose(Axis.YP.rotationDegrees(90));
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
