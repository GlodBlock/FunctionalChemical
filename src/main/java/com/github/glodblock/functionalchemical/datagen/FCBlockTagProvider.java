package com.github.glodblock.functionalchemical.datagen;

import com.github.glodblock.functionalchemical.FunctionalChemical;
import com.github.glodblock.functionalchemical.common.FCSingletons;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class FCBlockTagProvider extends BlockTagsProvider {

    public FCBlockTagProvider(DataGenerator output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper existingFileHelper) {
        super(output.getPackOutput(), lookup, FunctionalChemical.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        TagKey<Block> pickaxe = BlockTags.MINEABLE_WITH_PICKAXE;
        tag(pickaxe)
                .add(FCSingletons.CHEM_DRAWER_1)
                .add(FCSingletons.CHEM_DRAWER_2)
                .add(FCSingletons.CHEM_DRAWER_4)
                .add(FCSingletons.RADIOACTIVE_DRAWER);
    }

}
