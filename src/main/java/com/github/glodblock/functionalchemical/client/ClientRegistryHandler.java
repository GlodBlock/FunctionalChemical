package com.github.glodblock.functionalchemical.client;

import com.github.glodblock.functionalchemical.client.render.tesr.ChemDrawerTileTESR;
import com.github.glodblock.functionalchemical.common.FCItemAndBlock;
import com.github.glodblock.functionalchemical.common.tileentities.ChemicalDrawerTile;
import com.github.glodblock.functionalchemical.common.tileentities.RadioactiveDrawerTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientRegistryHandler {

    public static final ClientRegistryHandler INSTANCE = new ClientRegistryHandler();

    public void init() {
        this.registerGui();
    }


    public void registerGui() {

    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void registerModels(ModelEvent.RegisterGeometryLoaders event) {
        BlockEntityRenderers.register((BlockEntityType<ChemicalDrawerTile>) FCItemAndBlock.CHEM_DRAWER_1.getRight().get(), ChemDrawerTileTESR::new);
        BlockEntityRenderers.register((BlockEntityType<ChemicalDrawerTile>) FCItemAndBlock.CHEM_DRAWER_2.getRight().get(), ChemDrawerTileTESR::new);
        BlockEntityRenderers.register((BlockEntityType<ChemicalDrawerTile>) FCItemAndBlock.CHEM_DRAWER_4.getRight().get(), ChemDrawerTileTESR::new);
        BlockEntityRenderers.register((BlockEntityType<RadioactiveDrawerTile>) FCItemAndBlock.RADIOACTIVE_DRAWER.getRight().get(), ChemDrawerTileTESR::new);
    }

}
