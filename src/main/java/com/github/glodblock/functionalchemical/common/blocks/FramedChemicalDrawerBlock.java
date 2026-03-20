package com.github.glodblock.functionalchemical.common.blocks;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.block.FramedBlock;
import com.github.glodblock.functionalchemical.common.FCRegistryHandler;
import com.github.glodblock.functionalchemical.common.tileentities.FramedChemicalDrawerTile;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import mekanism.common.registries.MekanismBlocks;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class FramedChemicalDrawerBlock extends ChemicalDrawerBlock implements FramedBlock {

    public FramedChemicalDrawerBlock(FunctionalStorage.DrawerType type, Properties properties) {
        super(type, properties);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (blockPos, state) -> new FramedChemicalDrawerTile(this, FCRegistryHandler.INSTANCE.getTileType(this), blockPos, state, this.getType());
    }

    @Override
    public void registerRecipe(@NotNull RecipeOutput consumer) {
        if (this.getType() == FunctionalStorage.DrawerType.X_1) {
            TitaniumShapedRecipeBuilder.shapedRecipe(this).pattern("PPP").pattern("PCP").pattern("PPP").define('P', Tags.Items.NUGGETS_IRON).define('C', MekanismBlocks.BASIC_CHEMICAL_TANK).save(consumer);
        }
        if (this.getType() == FunctionalStorage.DrawerType.X_2) {
            TitaniumShapedRecipeBuilder.shapedRecipe(this, 2).pattern("PCP").pattern("PPP").pattern("PCP").define('P', Tags.Items.NUGGETS_IRON).define('C', MekanismBlocks.BASIC_CHEMICAL_TANK).save(consumer);
        }
        if (this.getType() == FunctionalStorage.DrawerType.X_4) {
            TitaniumShapedRecipeBuilder.shapedRecipe(this, 4).pattern("CPC").pattern("PPP").pattern("CPC").define('P', Tags.Items.NUGGETS_IRON).define('C', MekanismBlocks.BASIC_CHEMICAL_TANK).save(consumer);
        }
    }

}
