package com.github.glodblock.functionalchemical.common.items;

import com.github.glodblock.functionalchemical.common.blocks.ChemicalDrawerBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class DrawerBlockItem extends BlockItem {

    public DrawerBlockItem(ChemicalDrawerBlock block, Item.Properties properties) {
        super(block, properties);
        block.getItemGroup().getTabList().add(this);
    }

}