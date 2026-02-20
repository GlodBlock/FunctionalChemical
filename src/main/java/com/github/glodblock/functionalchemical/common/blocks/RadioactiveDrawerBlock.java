package com.github.glodblock.functionalchemical.common.blocks;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.github.glodblock.functionalchemical.common.FCRegistryHandler;
import com.github.glodblock.functionalchemical.common.tileentities.RadioactiveDrawerTile;
import com.hrznstudio.titanium.nbthandler.NBTManager;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import mekanism.common.registries.MekanismBlocks;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class RadioactiveDrawerBlock extends ChemicalDrawerBlock {

    public RadioactiveDrawerBlock(Properties properties) {
        super(FunctionalStorage.DrawerType.X_1, properties);
        NBTManager.getInstance().scanTileClassForAnnotations(RadioactiveDrawerTile.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (blockPos, state) -> new RadioactiveDrawerTile(this, FCRegistryHandler.INSTANCE.getTileType(this), blockPos, state);
    }

    @Override
    public void registerRecipe(@NotNull Consumer<FinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .pattern("PPP")
                .pattern("PCP")
                .pattern("PPP")
                .define('P', ItemTags.PLANKS)
                .define('C', MekanismBlocks.RADIOACTIVE_WASTE_BARREL)
                .save(consumer);
    }

}
