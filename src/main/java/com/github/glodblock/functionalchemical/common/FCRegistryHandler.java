package com.github.glodblock.functionalchemical.common;

import com.github.glodblock.functionalchemical.FunctionalChemical;
import com.github.glodblock.functionalchemical.common.blocks.ChemicalDrawerBlock;
import com.github.glodblock.functionalchemical.common.items.DrawerBlockItem;
import com.glodblock.github.glodium.registry.RegistryHandler;
import com.hrznstudio.titanium.block.BasicTileBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.IdentityHashMap;

public class FCRegistryHandler extends RegistryHandler {

    public static final FCRegistryHandler INSTANCE = new FCRegistryHandler();
    private static final IdentityHashMap<Block, BlockEntityType<? extends BlockEntity>> TILE_TYPE = new IdentityHashMap<>();

    private FCRegistryHandler() {
        super(FunctionalChemical.MODID);
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
