package com.github.glodblock.functionalchemical.common;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.github.glodblock.functionalchemical.FunctionalChemical;
import com.github.glodblock.functionalchemical.common.blocks.ChemicalDrawerBlock;
import com.github.glodblock.functionalchemical.common.blocks.RadioactiveDrawerBlock;
import com.github.glodblock.functionalchemical.common.items.DecayUpgradeItem;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class FCItemAndBlock {

    public static final TitaniumTab TAB = new TitaniumTab(FunctionalChemical.id("main"));

    public static ChemicalDrawerBlock CHEM_DRAWER_1;
    public static ChemicalDrawerBlock CHEM_DRAWER_2;
    public static ChemicalDrawerBlock CHEM_DRAWER_4;
    public static ChemicalDrawerBlock RADIOACTIVE_DRAWER;

    public static DecayUpgradeItem DECAY_UPGRADE;
    public static DecayUpgradeItem ADV_DECAY_UPGRADE;

    public static void init(FCRegistryHandler handler) {
        CHEM_DRAWER_1 = new ChemicalDrawerBlock(FunctionalStorage.DrawerType.X_1, BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS));
        CHEM_DRAWER_2 = new ChemicalDrawerBlock(FunctionalStorage.DrawerType.X_2, BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS));
        CHEM_DRAWER_4 = new ChemicalDrawerBlock(FunctionalStorage.DrawerType.X_4, BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS));
        RADIOACTIVE_DRAWER = new RadioactiveDrawerBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS));
        DECAY_UPGRADE = new DecayUpgradeItem(new Item.Properties(), false);
        ADV_DECAY_UPGRADE = new DecayUpgradeItem(new Item.Properties().rarity(Rarity.EPIC), true);
        handler.block("chem_1", CHEM_DRAWER_1);
        handler.block("chem_2", CHEM_DRAWER_2);
        handler.block("chem_4", CHEM_DRAWER_4);
        handler.block("radioactive_drawer", RADIOACTIVE_DRAWER);
        handler.item("decay_upgrade", DECAY_UPGRADE);
        handler.item("adv_decay_upgrade", ADV_DECAY_UPGRADE);
    }

}
