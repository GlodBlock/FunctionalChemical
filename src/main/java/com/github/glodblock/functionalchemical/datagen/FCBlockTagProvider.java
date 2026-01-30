package com.github.glodblock.functionalchemical.datagen;

import com.github.glodblock.functionalchemical.FunctionalChemical;
import com.github.glodblock.functionalchemical.common.FCItemAndBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

public class FCBlockTagProvider extends BlockTagsProvider {

    public FCBlockTagProvider(DataGenerator output, ExistingFileHelper existingFileHelper) {
        super(output, FunctionalChemical.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        TagKey<Block> pickaxe = BlockTags.MINEABLE_WITH_PICKAXE;
        tag(pickaxe)
                .add(FCItemAndBlock.CHEM_DRAWER_1.getLeft().get())
                .add(FCItemAndBlock.CHEM_DRAWER_2.getLeft().get())
                .add(FCItemAndBlock.CHEM_DRAWER_4.getLeft().get())
                .add(FCItemAndBlock.RADIOACTIVE_DRAWER.getLeft().get());
    }

}
