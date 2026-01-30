package com.github.glodblock.functionalchemical.common;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.item.UpgradeItem;
import com.github.glodblock.functionalchemical.FunctionalChemical;
import com.github.glodblock.functionalchemical.common.blocks.ChemicalDrawerBlock;
import com.github.glodblock.functionalchemical.common.blocks.RadioactiveDrawerBlock;
import com.hrznstudio.titanium.item.BasicItem;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FCItemAndBlock {

    public static AdvancedTitaniumTab TAB = new AdvancedTitaniumTab(FunctionalChemical.MODID, true);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> CHEM_DRAWER_1;
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> CHEM_DRAWER_2;
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> CHEM_DRAWER_4;
    public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> RADIOACTIVE_DRAWER;

    public static RegistryObject<Item> DECAY_UPGRADE;

    public static void init(DeferredRegistryHelper registryHelper) {
        CHEM_DRAWER_1 = registryHelper.registerBlockWithTileItem(
                "chem_1",
                () -> new ChemicalDrawerBlock(FunctionalStorage.DrawerType.X_1, BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)),
                block -> () -> new ChemicalDrawerBlock.DrawerItem((ChemicalDrawerBlock) block.get(), new Item.Properties().tab(TAB))
        );
        CHEM_DRAWER_2 = registryHelper.registerBlockWithTileItem(
                "chem_2",
                () -> new ChemicalDrawerBlock(FunctionalStorage.DrawerType.X_2, BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)),
                block -> () -> new ChemicalDrawerBlock.DrawerItem((ChemicalDrawerBlock) block.get(), new Item.Properties().tab(TAB))
        );
        CHEM_DRAWER_4 = registryHelper.registerBlockWithTileItem(
                "chem_4",
                () -> new ChemicalDrawerBlock(FunctionalStorage.DrawerType.X_4, BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)),
                block -> () -> new ChemicalDrawerBlock.DrawerItem((ChemicalDrawerBlock) block.get(), new Item.Properties().tab(TAB))
        );
        RADIOACTIVE_DRAWER = registryHelper.registerBlockWithTileItem(
                "radioactive_drawer",
                () -> new RadioactiveDrawerBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)),
                block -> () -> new ChemicalDrawerBlock.DrawerItem((ChemicalDrawerBlock) block.get(), new Item.Properties().tab(TAB))
        );
        DECAY_UPGRADE = registryHelper.registerGeneric(ForgeRegistries.ITEMS.getRegistryKey(), "decay_upgrade", () -> new UpgradeItem(new Item.Properties(), UpgradeItem.Type.UTILITY) {

            @Override
            public void addTooltipDetails(BasicItem.@Nullable Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
                super.addTooltipDetails(key, stack, tooltip, advanced);
                tooltip.add(Component.translatable("tooltip.functionalchemical.decay_upgrade").withStyle(ChatFormatting.GRAY));
            }

        });
        TAB.addIconStack(() -> new ItemStack(CHEM_DRAWER_1.getLeft().get()));
    }

}
