package com.github.glodblock.functionalchemical.common.items;

import com.buuz135.functionalstorage.inventory.item.DrawerCapabilityProvider;
import com.github.glodblock.functionalchemical.common.blocks.ChemicalDrawerBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DrawerBlockItem extends BlockItem {

    private final ChemicalDrawerBlock drawerBlock;

    public DrawerBlockItem(ChemicalDrawerBlock block, Item.Properties properties) {
        super(block, properties);
        this.drawerBlock = block;
        block.getItemGroup().getTabList().add(this);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        return super.getTooltipImage(stack);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new DrawerCapabilityProvider(stack, this.drawerBlock.getType());
    }

}