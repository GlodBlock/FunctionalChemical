package com.github.glodblock.functionalchemical.datagen;

import com.buuz135.functionalstorage.util.StorageTags;
import com.github.glodblock.functionalchemical.common.FCSingletons;
import com.hrznstudio.titanium.block.BasicBlock;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tags.MekanismTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FCRecipeProvider extends RecipeProvider {

    private final Lazy<List<Block>> blocksToProcess;

    public FCRecipeProvider(DataGenerator p, Lazy<List<Block>> blocksToProcess, CompletableFuture<HolderLookup.Provider> prov) {
        super(p.getPackOutput(), prov);
        this.blocksToProcess = blocksToProcess;
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput c) {
        this.blocksToProcess.get().stream()
                .map((block) -> (BasicBlock) block)
                .forEach((basicBlock) -> basicBlock.registerRecipe(c));
        TitaniumShapedRecipeBuilder.shapedRecipe(FCSingletons.DECAY_UPGRADE)
                .pattern("EEE")
                .pattern("BDB")
                .pattern("EEE")
                .define('E', MekanismItems.HDPE_SHEET)
                .define('B', MekanismTags.Items.INGOTS_BRONZE)
                .define('D', StorageTags.DRAWER)
                .save(c);
        TitaniumShapedRecipeBuilder.shapedRecipe(FCSingletons.ADV_DECAY_UPGRADE)
                .pattern("DDD")
                .pattern("DXD")
                .pattern("DDD")
                .define('X', MekanismTags.Items.STORAGE_BLOCKS_REFINED_GLOWSTONE)
                .define('D', FCSingletons.DECAY_UPGRADE)
                .save(c);
    }

}
