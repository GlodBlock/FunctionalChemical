package com.github.glodblock.functionalchemical.datagen;

import com.github.glodblock.functionalchemical.FunctionalChemical;
import com.hrznstudio.titanium.datagenerator.loot.TitaniumLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = FunctionalChemical.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FCDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent dataEvent) {
        final NonNullLazy<List<Block>> blocksToProcess = NonNullLazy.of(
                () -> ForgeRegistries.BLOCKS.getValues().stream().filter(
                        (basicBlock) -> Optional.ofNullable(ForgeRegistries.BLOCKS.getKey(basicBlock))
                                .map(ResourceLocation::getNamespace)
                                .filter(FunctionalChemical.MODID::equalsIgnoreCase)
                                .isPresent()).toList());
        var gen = dataEvent.getGenerator();
        gen.addProvider(true, new FCBlockTagProvider(gen, dataEvent.getLookupProvider(), dataEvent.getExistingFileHelper()));
        gen.addProvider(true, new FCRecipeProvider(gen, blocksToProcess));
        gen.addProvider(true, new TitaniumLootTableProvider(gen, blocksToProcess));
    }

}
