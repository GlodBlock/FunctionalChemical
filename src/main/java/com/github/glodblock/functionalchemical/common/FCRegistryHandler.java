package com.github.glodblock.functionalchemical.common;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.github.glodblock.functionalchemical.FunctionalChemical;
import com.github.glodblock.functionalchemical.common.blocks.ChemicalDrawerBlock;
import com.github.glodblock.functionalchemical.common.cap.ChemicalHost;
import com.github.glodblock.functionalchemical.common.items.DrawerBlockItem;
import com.glodblock.github.glodium.registry.RegistryHandler;
import com.hrznstudio.titanium.block.BasicTileBlock;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.IdentityHashMap;

public class FCRegistryHandler extends RegistryHandler {

    public static final FCRegistryHandler INSTANCE = new FCRegistryHandler();
    private static final IdentityHashMap<Block, BlockEntityType<? extends BlockEntity>> TILE_TYPE = new IdentityHashMap<>();

    private FCRegistryHandler() {
        super(FunctionalChemical.MODID);
    }

    @Override
    public void tile(String name, BlockEntityType<?> type) {
        this.tiles.add(Pair.of(name, type));
    }

    @SuppressWarnings("unchecked")
    @Override
    @SubscribeEvent
    protected void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        for (var pair : TILE_TYPE.entrySet()) {
            var block = pair.getKey();
            if (block instanceof ChemicalDrawerBlock) {
                event.registerBlockEntity(Capabilities.CHEMICAL.block(), (BlockEntityType<? extends ChemicalHost>) pair.getValue(), ChemicalHost::getChemicalHandler);
            }
        }
        event.registerBlockEntity(Capabilities.CHEMICAL.block(), (BlockEntityType<? extends ChemicalHost>) FunctionalStorage.DRAWER_CONTROLLER.type().get(), ChemicalHost::getChemicalHandler);
        event.registerBlockEntity(Capabilities.CHEMICAL.block(), (BlockEntityType<? extends ChemicalHost>) FunctionalStorage.FRAMED_DRAWER_CONTROLLER.type().get(), ChemicalHost::getChemicalHandler);
        event.registerBlockEntity(Capabilities.CHEMICAL.block(), (BlockEntityType<? extends ChemicalHost>) FunctionalStorage.CONTROLLER_EXTENSION.type().get(), ChemicalHost::getChemicalHandler);
        event.registerBlockEntity(Capabilities.CHEMICAL.block(), (BlockEntityType<? extends ChemicalHost>) FunctionalStorage.FRAMED_CONTROLLER_EXTENSION.type().get(), ChemicalHost::getChemicalHandler);
    }

    @SuppressWarnings("DataFlowIssue")
    public void block(String name, BasicTileBlock<?> block) {
        this.block(name, block, b -> new DrawerBlockItem((ChemicalDrawerBlock) b, new Item.Properties()));
        this.tile(name, TILE_TYPE.computeIfAbsent(block, (k) -> BlockEntityType.Builder.of(block.getTileEntityFactory(), block).build(null)));
    }

    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityType<T> getTileType(Block host) {
        return (BlockEntityType<T>) TILE_TYPE.get(host);
    }

}
