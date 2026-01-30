package com.github.glodblock.functionalchemical.datagen;

import com.buuz135.functionalstorage.util.StorageTags;
import com.github.glodblock.functionalchemical.FunctionalChemical;
import com.github.glodblock.functionalchemical.common.FCItemAndBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class FCItemTagsProvider extends ItemTagsProvider {

    public FCItemTagsProvider(DataGenerator p, BlockTagsProvider block, @Nullable ExistingFileHelper existingFileHelper) {
        super(p, block, FunctionalChemical.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(StorageTags.DRAWER)
                .add(FCItemAndBlock.CHEM_DRAWER_1.getLeft().get().asItem())
                .add(FCItemAndBlock.CHEM_DRAWER_2.getLeft().get().asItem())
                .add(FCItemAndBlock.CHEM_DRAWER_4.getLeft().get().asItem())
                .add(FCItemAndBlock.RADIOACTIVE_DRAWER.getLeft().get().asItem());
    }
}
