package com.github.glodblock.functionalchemical.client;

import com.github.glodblock.functionalchemical.client.render.item.ChemDrawerItemRender;
import com.github.glodblock.functionalchemical.client.render.tesr.ChemDrawerTileTESR;
import com.github.glodblock.functionalchemical.common.FCSingletons;
import com.github.glodblock.functionalchemical.common.FCRegistryHandler;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public class ClientRegistryHandler {

    public static final ClientRegistryHandler INSTANCE = new ClientRegistryHandler();

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public void onInit(FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(FCSingletons.FRAMED_CHEM_DRAWER_1, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(FCSingletons.FRAMED_CHEM_DRAWER_2, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(FCSingletons.FRAMED_CHEM_DRAWER_4, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(FCSingletons.FRAMED_RADIOACTIVE_DRAWER, RenderType.cutout());
    }

    @SubscribeEvent
    public void registerModels(ModelEvent.RegisterGeometryLoaders event) {
        BlockEntityRenderers.register(FCRegistryHandler.INSTANCE.getTileType(FCSingletons.CHEM_DRAWER_1), ChemDrawerTileTESR::new);
        BlockEntityRenderers.register(FCRegistryHandler.INSTANCE.getTileType(FCSingletons.CHEM_DRAWER_2), ChemDrawerTileTESR::new);
        BlockEntityRenderers.register(FCRegistryHandler.INSTANCE.getTileType(FCSingletons.CHEM_DRAWER_4), ChemDrawerTileTESR::new);
        BlockEntityRenderers.register(FCRegistryHandler.INSTANCE.getTileType(FCSingletons.RADIOACTIVE_DRAWER), ChemDrawerTileTESR::new);
        BlockEntityRenderers.register(FCRegistryHandler.INSTANCE.getTileType(FCSingletons.FRAMED_CHEM_DRAWER_1), ChemDrawerTileTESR::new);
        BlockEntityRenderers.register(FCRegistryHandler.INSTANCE.getTileType(FCSingletons.FRAMED_CHEM_DRAWER_2), ChemDrawerTileTESR::new);
        BlockEntityRenderers.register(FCRegistryHandler.INSTANCE.getTileType(FCSingletons.FRAMED_CHEM_DRAWER_4), ChemDrawerTileTESR::new);
        BlockEntityRenderers.register(FCRegistryHandler.INSTANCE.getTileType(FCSingletons.FRAMED_RADIOACTIVE_DRAWER), ChemDrawerTileTESR::new);
    }

    @SubscribeEvent
    public void registerClientItemExt(RegisterClientExtensionsEvent event) {
        event.registerItem(ChemDrawerItemRender.EXT1, FCSingletons.CHEM_DRAWER_1.asItem(), FCSingletons.FRAMED_CHEM_DRAWER_1.asItem(), FCSingletons.RADIOACTIVE_DRAWER.asItem(), FCSingletons.FRAMED_RADIOACTIVE_DRAWER.asItem());
        event.registerItem(ChemDrawerItemRender.EXT2, FCSingletons.CHEM_DRAWER_2.asItem(), FCSingletons.FRAMED_CHEM_DRAWER_2.asItem());
        event.registerItem(ChemDrawerItemRender.EXT4, FCSingletons.CHEM_DRAWER_4.asItem(), FCSingletons.FRAMED_CHEM_DRAWER_4.asItem());
    }

}
