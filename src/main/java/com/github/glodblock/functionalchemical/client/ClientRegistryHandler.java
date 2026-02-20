package com.github.glodblock.functionalchemical.client;

import com.github.glodblock.functionalchemical.client.render.tesr.ChemDrawerTileTESR;
import com.github.glodblock.functionalchemical.common.FCItemAndBlock;
import com.github.glodblock.functionalchemical.common.FCRegistryHandler;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientRegistryHandler {

    public static final ClientRegistryHandler INSTANCE = new ClientRegistryHandler();

    @SubscribeEvent
    public void registerModels(ModelEvent.RegisterGeometryLoaders event) {
        BlockEntityRenderers.register(FCRegistryHandler.INSTANCE.getTileType(FCItemAndBlock.CHEM_DRAWER_1), ChemDrawerTileTESR::new);
        BlockEntityRenderers.register(FCRegistryHandler.INSTANCE.getTileType(FCItemAndBlock.CHEM_DRAWER_2), ChemDrawerTileTESR::new);
        BlockEntityRenderers.register(FCRegistryHandler.INSTANCE.getTileType(FCItemAndBlock.CHEM_DRAWER_4), ChemDrawerTileTESR::new);
        BlockEntityRenderers.register(FCRegistryHandler.INSTANCE.getTileType(FCItemAndBlock.RADIOACTIVE_DRAWER), ChemDrawerTileTESR::new);
    }

}
