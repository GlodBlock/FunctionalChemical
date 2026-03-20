package com.github.glodblock.functionalchemical.client.render.item;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.block.tile.ControllableDrawerTile;
import com.buuz135.functionalstorage.client.item.FunctionalStorageISTER;
import com.buuz135.functionalstorage.item.FSAttachments;
import com.github.glodblock.functionalchemical.client.render.tesr.ChemDrawerTileTESR;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mekanism.api.SerializationConstants;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class ChemDrawerItemRender extends FunctionalStorageISTER {

    public static final ChemDrawerItemRender SLOT1 = new ChemDrawerItemRender(FunctionalStorage.DrawerType.X_1);
    public static final ChemDrawerItemRender SLOT2 = new ChemDrawerItemRender(FunctionalStorage.DrawerType.X_2);
    public static final ChemDrawerItemRender SLOT4 = new ChemDrawerItemRender(FunctionalStorage.DrawerType.X_4);
    public static final IClientItemExtensions EXT1 = new WrapExt(SLOT1);
    public static final IClientItemExtensions EXT2 = new WrapExt(SLOT2);
    public static final IClientItemExtensions EXT4 = new WrapExt(SLOT4);
    private final FunctionalStorage.DrawerType type;

    public ChemDrawerItemRender(FunctionalStorage.DrawerType type) {
        this.type = type;
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        // NO-OP
    }

    @Override
    public void renderByItem(HolderLookup.Provider access, @NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, int overlayLight) {
        var modelData = this.getData(stack);
        this.renderBlockItem(stack, displayContext, matrix, renderer, light, overlayLight, modelData, (poseStack) -> {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.mulPose(Axis.XN.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        });
        if (stack.has(FSAttachments.TILE)) {
            ControllableDrawerTile.DrawerOptions options = new ControllableDrawerTile.DrawerOptions();
            var tag = stack.getOrDefault(FSAttachments.TILE, new CompoundTag());
            options.deserializeNBT(access, tag.getCompound("drawerOptions"));
            matrix.mulPose(Axis.YP.rotationDegrees(-90.0F));
            matrix.mulPose(Axis.XP.rotationDegrees(90.0F));
            matrix.translate(0.0, -1.0, -1.0);
            CompoundTag tileTag = tag.getCompound("chemTank");
            if (this.type == FunctionalStorage.DrawerType.X_1) {
                var chemStack = deserialize(access, tileTag, 0);
                if (!chemStack.isEmpty()) {
                    var displayAmount = chemStack.getAmount();
                    AABB bounds = new AABB(0.0625, 0.078125, 0.0625, 0.9375, 0.078125 + (double) chemStack.getAmount() / chemStack.getAmount() * 0.78125, 0.9375);
                    ChemDrawerTileTESR.renderChemStack(matrix, renderer, light, overlayLight, chemStack, displayAmount, chemStack.getAmount(), 0.007F, options, bounds, false);
                }
            } else if (this.type == FunctionalStorage.DrawerType.X_2) {
                var chemStack = deserialize(access, tileTag, 0);
                if (!chemStack.isEmpty()) {
                    var displayAmount = chemStack.getAmount();
                    AABB bounds = new AABB(0.0625, 0.078125, 0.0625, 0.9375, 0.078125 + (double) chemStack.getAmount() / chemStack.getAmount() * 0.34375, 0.9375);
                    ChemDrawerTileTESR.renderChemStack(matrix, renderer, light, overlayLight, chemStack, displayAmount, chemStack.getAmount(), 0.007F, options, bounds, false);
                }
                chemStack = deserialize(access, tileTag, 1);
                if (!chemStack.isEmpty()) {
                    matrix.pushPose();
                    matrix.translate(0.0, 0.5, 0.0);
                    var displayAmount = chemStack.getAmount();
                    AABB bounds = new AABB(0.0625, 0.078125, 0.0625, 0.9375, 0.078125 + (double) chemStack.getAmount() / chemStack.getAmount() * 0.34375, 0.9375);
                    ChemDrawerTileTESR.renderChemStack(matrix, renderer, light, overlayLight, chemStack, displayAmount, chemStack.getAmount(), 0.007F, options, bounds, false);
                    matrix.popPose();
                }
            } else if (this.type == FunctionalStorage.DrawerType.X_4) {
                var chemStack = deserialize(access, tileTag, 0);
                if (!chemStack.isEmpty()) {
                    matrix.pushPose();
                    matrix.translate(0.5, 0.0, 0.0);
                    var displayAmount = chemStack.getAmount();
                    AABB bounds = new AABB(0.0625, 0.078125, 0.0625, 0.5, 0.078125 + (double) chemStack.getAmount() / chemStack.getAmount() * 0.34375, 0.9375);
                    ChemDrawerTileTESR.renderChemStack(matrix, renderer, light, overlayLight, chemStack, displayAmount, chemStack.getAmount(), 0.007F, options, bounds, true);
                    matrix.popPose();
                }

                chemStack = deserialize(access, tileTag, 1);
                if (!chemStack.isEmpty()) {
                    matrix.pushPose();
                    var displayAmount = chemStack.getAmount();
                    AABB bounds = new AABB(0.0625, 0.078125, 0.0625, 0.5, 0.078125 + (double) chemStack.getAmount() / chemStack.getAmount() * 0.34375, 0.9375);
                    ChemDrawerTileTESR.renderChemStack(matrix, renderer, light, overlayLight, chemStack, displayAmount, chemStack.getAmount(), 0.007F, options, bounds, true);
                    matrix.popPose();
                }

                chemStack = deserialize(access, tileTag, 2);
                if (!chemStack.isEmpty()) {
                    matrix.pushPose();
                    matrix.translate(0.5, 0.5, 0.0);
                    var displayAmount = chemStack.getAmount();
                    AABB bounds = new AABB(0.0625, 0.078125, 0.0625, 0.5, 0.078125 + (double) chemStack.getAmount() / chemStack.getAmount() * 0.34375, 0.9375);
                    ChemDrawerTileTESR.renderChemStack(matrix, renderer, light, overlayLight, chemStack, displayAmount, chemStack.getAmount(), 0.007F, options, bounds, true);
                    matrix.popPose();
                }

                chemStack = deserialize(access, tileTag, 3);
                if (!chemStack.isEmpty()) {
                    matrix.pushPose();
                    matrix.translate(0.0, 0.5, 0.0);
                    var displayAmount = chemStack.getAmount();
                    AABB bounds = new AABB(0.0625, 0.078125, 0.0625, 0.5, 0.078125 + (double) chemStack.getAmount() / chemStack.getAmount() * 0.34375, 0.9375);
                    ChemDrawerTileTESR.renderChemStack(matrix, renderer, light, overlayLight, chemStack, displayAmount, chemStack.getAmount(), 0.007F, options, bounds, true);
                    matrix.popPose();
                }
            }
        }
    }

    public static ChemicalStack deserialize(HolderLookup.Provider access, CompoundTag tileTag, int slot) {
        CompoundTag tag = tileTag.getCompound("#" + slot).getCompound(SerializationConstants.STORED);
        return tag.isEmpty() ? ChemicalStack.EMPTY : ChemicalStack.parseOptional(access, tag);
    }

    private record WrapExt(ChemDrawerItemRender render) implements IClientItemExtensions {

        @Override
        public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return this.render;
        }

    }

}
