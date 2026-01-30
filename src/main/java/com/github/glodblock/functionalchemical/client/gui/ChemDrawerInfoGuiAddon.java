package com.github.glodblock.functionalchemical.client.gui;

import com.buuz135.functionalstorage.util.NumberUtils;
import com.github.glodblock.functionalchemical.common.tileentities.ChemicalDrawerTile;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.merged.MergedChemicalTank;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChemDrawerInfoGuiAddon extends BasicScreenAddon {

    private final ResourceLocation gui;
    private final int slotAmount;
    private final boolean locked;
    private final Function<Integer, Pair<Integer, Integer>> slotPosition;
    private final Supplier<ChemicalDrawerTile.SyncTank> chemTankSupplier;
    private final Supplier<ChemicalDrawerTile.SyncFilter> filterSupplier;
    private final Function<Integer, Long> slotMaxAmount;

    public ChemDrawerInfoGuiAddon(int posX, int posY, ResourceLocation gui, int slotAmount, boolean locked, Function<Integer, Pair<Integer, Integer>> slotPosition, Supplier<ChemicalDrawerTile.SyncTank> chemTankSupplier, Supplier<ChemicalDrawerTile.SyncFilter> filterSupplier, Function<Integer, Long> slotMaxAmount) {
        super(posX, posY);
        this.gui = gui;
        this.slotAmount = slotAmount;
        this.locked = locked;
        this.slotPosition = slotPosition;
        this.chemTankSupplier = chemTankSupplier;
        this.filterSupplier = filterSupplier;
        this.slotMaxAmount = slotMaxAmount;
    }

    public static Rect2i getSizeForSlots(int currentSlot, int slotAmount) {
        if (slotAmount == 1) {
            return new Rect2i(9, 9, 30, 30);
        }
        if (slotAmount == 2) {
            if (currentSlot == 0) return new Rect2i(0, 30, 48, 13);
            if (currentSlot == 1) return new Rect2i(0, 6, 48, 13);
        }
        if (slotAmount == 4) {
            if (currentSlot == 0) return new Rect2i(30, 30, 16, 16);
            if (currentSlot == 1) return new Rect2i(2, 30, 16, 16);
            if (currentSlot == 2) return new Rect2i(30, 2, 16, 16);
            if (currentSlot == 3) return new Rect2i(2, 2, 16, 16);
        }
        return new Rect2i(0, 0, 0, 0);
    }

    public static Rect2i getSizeForHoverSlots(int currentSlot, int slotAmount) {
        if (slotAmount == 1) {
            return new Rect2i(9, 9, 30, 30);
        }
        if (slotAmount == 2) {
            if (currentSlot == 0) return new Rect2i(6, 30, 36, 12);
            if (currentSlot == 1) return new Rect2i(6, 6, 36, 12);
        }
        if (slotAmount == 4) {
            if (currentSlot == 0) return new Rect2i(30, 30, 12, 12);
            if (currentSlot == 1) return new Rect2i(6, 30, 12, 12);
            if (currentSlot == 2) return new Rect2i(30, 6, 12, 12);
            if (currentSlot == 3) return new Rect2i(6, 6, 12, 12);
        }
        return new Rect2i(0, 0, 0, 0);
    }

    @Override
    public int getXSize() {
        return 0;
    }

    @Override
    public int getYSize() {
        return 0;
    }

    @Override
    public void drawBackgroundLayer(PoseStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        for (var i = 0; i < this.slotAmount; i++) {
            var tank = this.chemTankSupplier.get().tanks()[i];
            var filter = this.filterSupplier.get().filter()[i];
            ChemicalStack<?> chemStack = null;
            if (tank.getCurrent() == MergedChemicalTank.Current.EMPTY) {
                if (this.locked) {
                    chemStack = filter;
                }
            } else {
                chemStack = tank.getTankFromCurrent(tank.getCurrent()).getStack();
            }
            if (chemStack != null && !chemStack.isEmpty()) {
                renderChem(stack, guiX, guiY, chemStack, i, this.slotAmount);
            }
        }
        RenderSystem.setShaderTexture(0, gui);
        var size = 16 * 2 + 16;
        Screen.blit(stack, guiX + getPosX(), guiY + getPosY(), 0, 0, size, size, size, size);
        for (var i = 0; i < this.slotAmount; i++) {
            var tank = this.chemTankSupplier.get().tanks()[i];
            ChemicalStack<?> chemStack = null;
            if (tank.getCurrent() != MergedChemicalTank.Current.EMPTY) {
                chemStack = tank.getTankFromCurrent(tank.getCurrent()).getStack();
            }
            if (chemStack != null && !chemStack.isEmpty()) {
                var x = guiX + slotPosition.apply(i).getLeft() + getPosX();
                var y = guiY + slotPosition.apply(i).getRight() + getPosY();
                var amount = NumberUtils.getFormatedFluidBigNumber((int) chemStack.getAmount()) + "/" + NumberUtils.getFormatedFluidBigNumber(Math.toIntExact(slotMaxAmount.apply(i)));
                var scale = 0.5f;
                stack.translate(0, 0, 200);
                stack.scale(scale, scale, scale);
                Minecraft.getInstance().font.drawShadow(stack, amount, (x + 17 - (float) Minecraft.getInstance().font.width(amount) / 2) * (1 / scale), (y + 12) * (1 / scale), 0xFFFFFF);
                stack.scale(1 / scale, 1 / scale, 1 / scale);
                stack.translate(0, 0, -200);
            }
        }
    }

    @Override
    public void drawForegroundLayer(PoseStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        for (var i = 0; i < this.slotAmount; i++) {
            var rect = getSizeForHoverSlots(i, this.slotAmount);
            var x = rect.getX() + getPosX() + guiX;
            var y = rect.getY() + getPosY() + guiY;
            if (mouseX > x && mouseX < x + rect.getWidth() && mouseY > y && mouseY < y + rect.getHeight()) {
                x = getPosX() + rect.getX();
                y = getPosY() + rect.getY();
                stack.translate(0, 0, 200);
                GuiComponent.fill(stack, x, y, x + rect.getWidth(), y + rect.getHeight(), -2130706433);
                stack.translate(0, 0, -200);
                var componentList = new ArrayList<Component>();
                var tank = this.chemTankSupplier.get().tanks()[i];
                ChemicalStack<?> over = null;
                if (tank.getCurrent() != MergedChemicalTank.Current.EMPTY) {
                    over = tank.getTankFromCurrent(tank.getCurrent()).getStack();
                }
                if (over == null || over.isEmpty()) {
                    componentList.add(Component.translatable("gui.functionalchemical.chemical").withStyle(ChatFormatting.GOLD).append(Component.literal("Empty").withStyle(ChatFormatting.WHITE)));
                } else {
                    componentList.add(Component.translatable("gui.functionalchemical.chemical").withStyle(ChatFormatting.GOLD).append(over.getTextComponent().copy().withStyle(ChatFormatting.WHITE)));
                    var amount = NumberUtils.getFormatedFluidBigNumber((int) over.getAmount()) + "/" + NumberUtils.getFormatedFluidBigNumber(Math.toIntExact(slotMaxAmount.apply(i)));
                    componentList.add(Component.translatable("gui.functionalstorage.amount").withStyle(ChatFormatting.GOLD).append(Component.literal(amount).withStyle(ChatFormatting.WHITE)));
                }
                componentList.add(Component.translatable("gui.functionalstorage.slot").withStyle(ChatFormatting.GOLD).append(Component.literal(i + "").withStyle(ChatFormatting.WHITE)));
                screen.renderTooltip(stack, componentList, Optional.empty(), mouseX - guiX, mouseY - guiY);
            }
        }
    }

    public void renderChem(PoseStack stack, int guiX, int guiY, ChemicalStack<?> chemStack, int slot, int slotAmount) {
        var sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(chemStack.getType().getIcon());
        if (sprite != null) {
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            Color color = new Color(chemStack.getChemicalTint());
            Rect2i rect = getSizeForSlots(slot, slotAmount);
            RenderSystem.setShaderColor((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F);
            RenderSystem.enableBlend();
            for(int x = 0; x < rect.getWidth(); x += 16) {
                for(int y = 0; y < rect.getHeight(); y += 16) {
                    Screen.blit(stack, this.getPosX() + guiX + rect.getX() + x, this.getPosY() + guiY + rect.getY() + y, 0, Math.min(16, rect.getWidth() - x), Math.min(16, rect.getHeight() - y), sprite);
                }
            }
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

}