package com.github.glodblock.functionalchemical.datagen;

import com.buuz135.functionalstorage.util.StorageTags;
import com.github.glodblock.functionalchemical.common.FCItemAndBlock;
import com.hrznstudio.titanium.block.BasicBlock;
import com.hrznstudio.titanium.recipe.generator.TitaniumRecipeProvider;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tags.MekanismTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.NonNullLazy;

import java.util.List;
import java.util.function.Consumer;

public class FCRecipeProvider extends TitaniumRecipeProvider {

    private final NonNullLazy<List<Block>> blocksToProcess;

    public FCRecipeProvider(DataGenerator p, NonNullLazy<List<Block>> blocksToProcess) {
        super(p);
        this.blocksToProcess = blocksToProcess;
    }

    @Override
    public void register(Consumer<FinishedRecipe> c) {
        this.blocksToProcess.get().stream()
                .map((block) -> (BasicBlock) block)
                .forEach((basicBlock) -> basicBlock.registerRecipe(c));
        TitaniumShapedRecipeBuilder.shapedRecipe(FCItemAndBlock.DECAY_UPGRADE.get())
                .pattern("EEE")
                .pattern("BDB")
                .pattern("EEE")
                .define('E', MekanismItems.HDPE_SHEET)
                .define('B', MekanismTags.Items.INGOTS_BRONZE)
                .define('D', StorageTags.DRAWER)
                .save(c);
    }

}
